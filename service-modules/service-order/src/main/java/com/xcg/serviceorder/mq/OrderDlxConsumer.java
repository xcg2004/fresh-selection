package com.xcg.serviceorder.mq;

import com.xcg.serviceorder.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderDlxConsumer {

    private final EmailService emailService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "order.dlx.queue"),
            exchange = @Exchange(name = "order.dlx.exchange", type = ExchangeTypes.TOPIC),
            key = "order.dlx"
    ))
    public void handleDlxMessage(Long orderId) {
        log.error("订单取消消息重试失败，已进入死信队列，订单ID: {}。消息将被丢弃。", orderId);
        // 这里可以添加报警逻辑，比如发送邮件或短信通知运维人员
        // 也可以将失败的消息存储到数据库中供后续人工处理

        //示例，采用qq邮箱发送邮件
        emailService.sendSimpleEmail(
                "3039049837@qq.com",
                "订单取消消息重试失败",
                "订单ID: " + orderId + "，已进入死信队列，消息将被丢弃。"
        );

    }
}
