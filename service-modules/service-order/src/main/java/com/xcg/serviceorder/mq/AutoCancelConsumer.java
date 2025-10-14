package com.xcg.serviceorder.mq;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.rabbitmq.client.Channel;
import com.xcg.freshcommon.core.exception.BizException;
import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.domain.order.entity.Orders;
import com.xcg.freshcommon.domain.orderItem.entity.OrderItem;
import com.xcg.freshcommon.feign.ProductFeignClient;
import com.xcg.serviceorder.mapper.OrderItemMapper;
import com.xcg.serviceorder.mapper.OrdersMapper;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class AutoCancelConsumer {

    private final OrdersMapper ordersMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductFeignClient productFeignClient;
    private final StringRedisTemplate redisTemplate;

    private static final String RETRY_COUNT_KEY_PREFIX = "order:cancel:retry:";
    private static final int MAX_RETRIES = 3;
    private static final Duration RETRY_TTL = Duration.ofHours(24);
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "order.cancel.queue")
    @GlobalTransactional(name = "order-cancel-tx", rollbackFor = Exception.class)
    public void cancelOrder(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        Long orderId = (Long) rabbitTemplate.getMessageConverter().fromMessage(message);

        String retryKey = RETRY_COUNT_KEY_PREFIX + orderId;

        try {
            log.info("开始处理订单取消消息，订单ID: {}", orderId);

            // 查询订单
            Orders orders = ordersMapper.selectById(orderId);
            if (orders == null) {
                log.warn("订单不存在，订单ID: {}", orderId);
                redisTemplate.delete(retryKey);
                channel.basicAck(deliveryTag, false);
                return;
            }

            // 检查订单状态
            if (orders.getStatus().getCode() != 0) {
                log.info("订单状态不是待付款，无需取消，订单ID: {}, 状态: {}", orderId, orders.getStatus().getMessage());
                redisTemplate.delete(retryKey);
                channel.basicAck(deliveryTag, false);
                return;
            }

            // 执行取消订单操作
            processCancelOrder(orderId);

            // 处理成功
            redisTemplate.delete(retryKey);
            channel.basicAck(deliveryTag, false);
            log.info("订单取消处理完成，订单ID: {}", orderId);

        } catch (Exception e) {
            handleConsumeFailure(orderId, retryKey, deliveryTag, channel, e);
        }
    }

    private void handleConsumeFailure(Long orderId, String retryKey,
                                      long deliveryTag, Channel channel, Exception e) throws IOException {
        // 增加重试次数
        Integer currentRetry = incrementRetryCount(retryKey);

        log.error("处理订单取消消息失败，订单ID: {}, 重试次数: {}/{}",
                orderId, currentRetry, MAX_RETRIES, e);

        if (currentRetry >= MAX_RETRIES) {
            log.error("订单取消消息重试次数已达上限，订单ID: {}，消息将进入死信队列", orderId);
            redisTemplate.delete(retryKey);
            channel.basicNack(deliveryTag, false, false);// 拒绝消息,进入死信队列
        } else {
            channel.basicNack(deliveryTag, false, true);// 重新入队消费
        }
    }

    private Integer incrementRetryCount(String retryKey) {
        Long newCount = redisTemplate.opsForValue().increment(retryKey);
        newCount = newCount == null ? 1 : newCount;
        redisTemplate.expire(retryKey, RETRY_TTL);
        return newCount.intValue();
    }

    protected void processCancelOrder(Long orderId) {
        // 1. 修改订单状态：待付款 -> 已关闭
        int updateResult = ordersMapper.update(new LambdaUpdateWrapper<Orders>()
                .eq(Orders::getId, orderId)
                .eq(Orders::getStatus, 0)
                .set(Orders::getStatus, 4)
        );
        if (updateResult <= 0) {
            log.warn("更新订单状态失败，订单ID: {}", orderId);
            throw new BizException("更新订单状态失败");
        }

        log.info("订单状态已更新为已关闭，订单ID: {}", orderId);

        // 2. 查询订单项
        List<OrderItem> orderItems = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId)
        );

        if (orderItems.isEmpty()) {
            log.warn("订单项为空，订单ID: {}", orderId);
            return;
        }

        // 3. 恢复库存
        Map<Long, Integer> skuIdAndQuantity = orderItems.stream()
                .collect(Collectors.toMap(OrderItem::getSkuId, OrderItem::getQuantity));

        Result<Boolean> result = productFeignClient.recoverStock(skuIdAndQuantity);
        if (!result.isSuccess()) {
            log.error("恢复库存失败，订单ID: {}, 错误信息: {}", orderId, result.getMessage());
            throw new BizException("恢复库存失败: " + result.getMessage());
        }

        log.info("库存恢复成功，订单ID: {}", orderId);
    }
}