package com.xcg.serviceattribute;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.xcg.serviceattribute.mapper")
@ComponentScan("com.xcg")
@EnableFeignClients(basePackages = "com.xcg.freshcommon.feign") // 指定Feign客户端所在包
public class AttributeApplication {
    public static void main(String[] args) {
        SpringApplication.run(AttributeApplication.class, args);
    }
}
