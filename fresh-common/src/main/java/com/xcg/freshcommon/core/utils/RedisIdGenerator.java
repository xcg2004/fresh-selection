package com.xcg.freshcommon.core.utils;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisIdGenerator {

    private final StringRedisTemplate redisTemplate;

    public String generateId(String key) {
        // 使用年月日作为key的一部分，避免key过期或过大
        String dateKey = key + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        Long increment = redisTemplate.opsForValue().increment(dateKey);
        // 设置key在当天结束时过期
        if (increment != null && increment == 1) {
            LocalDateTime tomorrow = LocalDate.now().plusDays(1).atStartOfDay();
            long secondsUntilTomorrow = Duration.between(LocalDateTime.now(), tomorrow).getSeconds();
            redisTemplate.expire(dateKey, secondsUntilTomorrow, TimeUnit.SECONDS);
        }

        // 格式化为订单号：业务码(2位)+日期(8位)+序号(6位)
        return String.format("10%s%06d",
                LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE),
                increment);
    }


}
