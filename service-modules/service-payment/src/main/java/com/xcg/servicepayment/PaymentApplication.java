package com.xcg.servicepayment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.xcg")
@EnableFeignClients(basePackages = "com.xcg.freshcommon.feign") // 指定Feign客户端所在包
@MapperScan("com.xcg.servicepayment.mapper")
public class PaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }

}
