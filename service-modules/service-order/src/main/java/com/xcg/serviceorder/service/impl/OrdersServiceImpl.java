package com.xcg.serviceorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rabbitmq.client.Address;
import com.xcg.freshcommon.core.exception.BizException;
import com.xcg.freshcommon.core.properties.OrderDelayProperties;
import com.xcg.freshcommon.core.utils.*;
import com.xcg.freshcommon.domain.order.dto.OrderCreateDto;
import com.xcg.freshcommon.domain.order.entity.Orders;
import com.xcg.freshcommon.domain.order.vo.OrderVO;
import com.xcg.freshcommon.domain.orderItem.entity.OrderItem;
import com.xcg.freshcommon.domain.orderItem.vo.OrderItemVO;
import com.xcg.freshcommon.domain.product.entity.Product;
import com.xcg.freshcommon.domain.productSku.entity.ProductSku;
import com.xcg.freshcommon.domain.userAddress.vo.UserAddressVO;
import com.xcg.freshcommon.enums.PayType;
import com.xcg.freshcommon.feign.CartFeignClient;
import com.xcg.freshcommon.feign.ProductFeignClient;
import com.xcg.freshcommon.feign.UserAddressFeignClient;
import com.xcg.serviceorder.mapper.OrderItemMapper;
import com.xcg.serviceorder.mapper.OrdersMapper;
import com.xcg.serviceorder.service.IOrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-11
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {

    private final OrdersMapper ordersMapper;

    private final ProductFeignClient productFeignClient;

    private final UserHolder userHolder;

    private final RedisIdGenerator redisIdGenerator;

    private final UserAddressFeignClient userFeignClient;
    private final CartFeignClient cartFeignClient;
    private final OrderItemMapper orderItemMapper;
    private final RabbitTemplate rabbitTemplate;

    private final OrderDelayProperties orderDelayProperties;

    @Override
    @GlobalTransactional(name = "order-create", timeoutMills = 60000,rollbackFor = Exception.class)
    public Result<Long> create(List<OrderCreateDto> orderCreateDto, Long addressId, PayType payType) {
        //1. 验证参数(非空已经由Spring-Validation处理)
        Long userId = userHolder.getUserId();

        // 校验用户地址，分布式场景下跨数据库外键失效
        Result<UserAddressVO> addressResult = userFeignClient.get(addressId);
        if(!addressResult.isSuccess() || addressResult.getData() == null) {
            throw new BizException(addressResult.getMessage());
        }

        // 校验cartId与skuId以及quantity是否对应正确
        for (OrderCreateDto createDto : orderCreateDto) {
            Long cartId = createDto.getCartId();
            Long skuId = createDto.getSkuId();
            Integer quantity = createDto.getQuantity();
            Result<Boolean> cartCheckResult = cartFeignClient.check(cartId, skuId, quantity);
            if(!cartCheckResult.isSuccess()) {
                throw new BizException(cartCheckResult.getMessage());
            }
        }

        //构建skuId->quantity的Map，方便批量处理
        Map<Long, Integer> skuIdAndQuantity = new HashMap<>();
        for (OrderCreateDto createDto : orderCreateDto) {
            skuIdAndQuantity.computeIfAbsent(createDto.getSkuId(), k -> createDto.getQuantity());
        }

        //2. 校验库存
        Result<Boolean> batchCheckResult= productFeignClient.batchCheckStatusWithStock(skuIdAndQuantity);
        if(!batchCheckResult.isSuccess()) {
            throw new BizException(batchCheckResult.getMessage());
        }

        //3. 扣减库存，锁定库存 stock-= quantity ,lock_stock+= quantity
        Result<List<ProductSku>> listResult = productFeignClient.deductStock(skuIdAndQuantity);
        if(!listResult.isSuccess()) {
            throw new BizException(listResult.getMessage());
        }
        List<ProductSku> productSkus = listResult.getData();
        Map<Long, ProductSku> productSkuMap = productSkus.stream().collect(Collectors.toMap(ProductSku::getId, Function.identity()));

        //4. 创建订单
        BigDecimal total = new BigDecimal(0);
//        BigDecimal pay = new BigDecimal(0);
        //todo 运费
//        BigDecimal freight = new BigDecimal(0);
        //todo 优惠
//        BigDecimal discount = new BigDecimal(0);
        for (ProductSku sku : productSkus) {
            Long skuId = sku.getId();
            BigDecimal price = sku.getPrice();
            Integer quantityOrDefault = skuIdAndQuantity.getOrDefault(skuId, 0);
            BigDecimal totalAmount = price.multiply(BigDecimal.valueOf(quantityOrDefault));
//            BigDecimal freightAmount = new BigDecimal(0);
//            BigDecimal discountAmount = new BigDecimal(0);

            //加上当前商品的总金额
            total = total.add(totalAmount);
//            freight = freight.add(freightAmount);
        }
        //todo 计算订单支付金额 = total + freight - discount
//        pay = total.add(freight).subtract(discount);

        Orders orders = new Orders();
        String no = redisIdGenerator.generateId("order:no");

        orders.setOrderNo(no);
        orders.setUserId(userId);
        orders.setTotalAmount(total);
        orders.setPayAmount(total);
        orders.setFreightAmount(new BigDecimal("0"));
        orders.setDiscountAmount(new BigDecimal("0"));
        orders.setPaymentType(payType);
        orders.setAddressId(addressId);

        String remarks = orderCreateDto.stream()
                .map(OrderCreateDto::getRemark)
                .filter(remark -> remark != null && !remark.isEmpty())  // 过滤掉空备注
                .collect(Collectors.joining(","));

        orders.setRemark(remarks);
        save(orders);

        //5. 批量创建订单item
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderCreateDto createDto : orderCreateDto) {
            OrderItem ordersItem = new OrderItem();
            ProductSku productSku = productSkuMap.get(createDto.getSkuId());
            ordersItem.setOrderId(orders.getId());
            ordersItem.setSkuId(productSku.getId());
            //todo 商品名称
            Result<Product> productBySkuId = productFeignClient.getProductBySkuId(createDto.getSkuId());
            if (productBySkuId.isSuccess() && productBySkuId.getData() != null) {
                ordersItem.setProductName(productBySkuId.getData().getName());
            } else {
                ordersItem.setProductName("未知商品");
            }
            ordersItem.setProductName(productBySkuId.getData().getName());
            ordersItem.setSkuSpecsText(productSku.getSpecsText());
            ordersItem.setSkuImage(productSku.getImage());
            ordersItem.setPrice(productSku.getPrice());
            ordersItem.setQuantity(createDto.getQuantity());
            ordersItem.setTotalPrice(productSku.getPrice().multiply(BigDecimal.valueOf(createDto.getQuantity())));
            orderItems.add(ordersItem);
        }

        orderItemMapper.insert(orderItems);

        //6. 创建订单成功，删除购物车
        Result<Boolean> deleteCartResult = cartFeignClient.batchDelete(orderCreateDto.stream().map(OrderCreateDto::getCartId).collect(Collectors.toList()));
        if(!deleteCartResult.isSuccess()) {
            throw new BizException(deleteCartResult.getMessage());
        }

        //7.RabbitMQ发送延迟消息，订单支付超时自动取消，stock += quantity，lock_stock -= quantity
        // 设置30分钟(1800000毫秒)后自动取消未支付订单
        log.info("准备发送订单取消延迟消息，订单ID: {}", orders.getId());

        rabbitTemplate.convertAndSend("order.delay.exchange",
                "order.cancel",
                orders.getId(), // 确保这里发送的是正确的订单ID
                msg -> {
                    msg.getMessageProperties().setHeader("x-delay", orderDelayProperties.getCancelTime());
                    // 设置消息持久化
                    msg.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    log.info("订单取消消息已发送，订单ID: {}, 延迟时间: {}ms", orders.getId(), orderDelayProperties.getCancelTime());
                    return msg;
                },
                new CorrelationData(orders.getId().toString()) // 添加关联数据用于确认
        );

        //8. 返回订单ID
        return Result.success(orders.getId());
    }

    @Override
    public Result<OrderVO> selectById(Long orderId) {
        //1. 获取用户id
        Long userId = userHolder.getUserId();

        //2. 查询订单
        Orders orders = getOne(new LambdaQueryWrapper<Orders>()
                .eq(Orders::getId,orderId)
                .eq(Orders::getUserId,userId)
        );
        if(orders == null) {
            return Result.error("订单不存在");
        }

        OrderVO orderVO = convertToVO(orders);

        //3. 查询订单项,构建订单项ListVO
        List<OrderItem> orderItemList = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId)
        );
        List<OrderItemVO> orderItemVOList = orderItemList.stream()
                .map(this::convertOrderItemToVO).toList();
        orderVO.setOrderItemVOList(orderItemVOList);

        //4. 查询收货地址,构建订单收货地址VO
        Result<UserAddressVO> userAddressVOResult = userFeignClient.get(orders.getAddressId());
        if(!userAddressVOResult.isSuccess()) {
            return Result.error(userAddressVOResult.getMessage());
        }
        orderVO.setUserAddressVO(userAddressVOResult.getData());

        //5. 返回VO
        return Result.success(orderVO);
    }

    private OrderVO convertToVO(Orders orders) {
        OrderVO orderVO = new OrderVO();

        orderVO.setId(orders.getId());
        orderVO.setOrderNo(orders.getOrderNo());
        orderVO.setTotalAmount(orders.getTotalAmount());
        orderVO.setPayAmount(orders.getPayAmount());
        orderVO.setFreightAmount(orders.getFreightAmount());
        orderVO.setDiscountAmount(orders.getDiscountAmount());
        orderVO.setPaymentType(orders.getPaymentType());
        orderVO.setStatus(orders.getStatus());
        orderVO.setRemark(orders.getRemark());
        orderVO.setCreateTime(orders.getCreateTime());
        orderVO.setUpdateTime(orders.getUpdateTime());
        orderVO.setPayTime(orders.getPayTime());
        orderVO.setShipTime(orders.getShipTime());
        orderVO.setConfirmTime(orders.getConfirmTime());

        return orderVO;
    }

    private OrderItemVO convertOrderItemToVO(OrderItem orderItem) {
        return OrderItemVO.builder()
                .id(orderItem.getId())
                .orderId(orderItem.getOrderId())
                .skuId(orderItem.getSkuId())
                .productName(orderItem.getProductName())
                .skuSpec(orderItem.getSkuSpecsText())
                .skuImage(orderItem.getSkuImage())
                .price(orderItem.getPrice())
                .quantity(orderItem.getQuantity())
                .totalPrice(orderItem.getTotalPrice())
                .createTime(orderItem.getCreateTime())
                .build();
    }

    @Override
    public Result<ScrollResultVO<OrderVO>> scrollPage(ScrollQueryParam scrollQueryParam) {
        //1. 获取用户id
        Long userId = userHolder.getUserId();
        //2. 分页查询
        Integer pageSize = scrollQueryParam.getValidPageSize();
        List<Orders> ordersPage = ordersMapper.scrollPage(pageSize,
                scrollQueryParam.getLastId(), scrollQueryParam.getLastCreateTime(), userId);

        //3.构建orderId -> userAddressVO的Map
        Map<Long, UserAddressVO> userAddressVOMap = new HashMap<>();

        for (Orders orders : ordersPage) {
            Long addressId = orders.getAddressId();
            Result<UserAddressVO> userAddressVOResult = userFeignClient.get(addressId);
            if(!userAddressVOResult.isSuccess()) {
                return Result.error(userAddressVOResult.getMessage());
            }
            userAddressVOMap.computeIfAbsent(addressId, k -> userAddressVOResult.getData());
        }

        //4. 转成VO
        List<OrderVO> orderVOList = ordersPage.stream().map(this::convertToVO).toList();

        //5. 获取订单项
        for (OrderVO orderVO : orderVOList) {
            List<OrderItem> orderItemList = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                    .eq(OrderItem::getOrderId, orderVO.getId()));
            orderVO.setOrderItemVOList(orderItemList.stream().map(this::convertOrderItemToVO).toList());
            orderVO.setUserAddressVO(userAddressVOMap.getOrDefault(orderVO.getId(), null));
        }

        //6. 计算下次查询的游标
        Long nextCursor = null;
        if (!orderVOList.isEmpty()) {
            nextCursor = orderVOList.get(orderVOList.size() - 1).getId();
        }
        LocalDateTime nextCursorTime = null;
        if (!orderVOList.isEmpty()) {
            nextCursorTime = orderVOList.get(orderVOList.size() - 1).getCreateTime();
        }

        //7. 返回结果
        return Result.success(ScrollResultVO.of(orderVOList, nextCursor, nextCursorTime, pageSize));
    }
}
