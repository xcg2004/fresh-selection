package com.xcg.serviceorder.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfig {

    // 主队列 - 设置死信队列参数
    @Bean
    public Queue orderQueue() {
        Map<String, Object> args = new HashMap<>();
        // 指定死信交换机
        args.put("x-dead-letter-exchange", "order.dlx.exchange");
        // 指定死信路由键
        args.put("x-dead-letter-routing-key", "order.dlx");
        // 设置消息TTL（可选）
        // args.put("x-message-ttl", 1800000); // 30分钟
        return new Queue("order.cancel.queue", true, false, false, args);
    }

    // 死信队列
    @Bean
    public Queue orderDlxQueue() {
        return new Queue("order.dlx.queue", true);
    }

    // 延迟消息交换机
    @Bean
    public CustomExchange delayExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "topic");
        return new CustomExchange("order.delay.exchange", "x-delayed-message", true, false, args);
    }

    // 死信交换机 - 改为 Topic 类型
    @Bean
    public TopicExchange orderDlxExchange() {
        return new TopicExchange("order.dlx.exchange");
    }

    @Bean
    public Binding delayedBinding(Queue orderQueue, CustomExchange delayExchange) {
        return BindingBuilder.bind(orderQueue).to(delayExchange).with("order.cancel").noargs();
    }

    // 死信队列绑定
    @Bean
    public Binding orderDlxBinding(Queue orderDlxQueue, TopicExchange orderDlxExchange) {
        return BindingBuilder.bind(orderDlxQueue).to(orderDlxExchange).with("order.dlx");
    }
}
