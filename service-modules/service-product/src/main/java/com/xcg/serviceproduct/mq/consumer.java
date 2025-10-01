package com.xcg.serviceproduct.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class consumer {

    @RabbitListener(bindings = @QueueBinding
            (
                value = @Queue(name = "my.queue"), exchange = @Exchange(value = "my.exchange", type = "topic"),
                key = "my.routing.key"
            )
    )
    public void receive(String message) {
        System.out.println("消费者收到消息：" + message);
    }
}
