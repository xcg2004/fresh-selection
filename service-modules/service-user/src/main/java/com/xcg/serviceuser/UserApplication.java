package com.xcg.serviceuser;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.xcg"})
@MapperScan(basePackages = {"com.xcg.serviceuser.mapper"})
@EnableFeignClients(basePackages = "com.xcg.freshcommon.feign") // 指定Feign客户端所在包
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

}
