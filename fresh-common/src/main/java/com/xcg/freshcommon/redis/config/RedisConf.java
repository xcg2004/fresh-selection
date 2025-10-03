package com.xcg.freshcommon.redis.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
public class RedisConf {

//    @Bean
//    @ConditionalOnMissingBean(StringRedisTemplate.class)
//    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        StringRedisTemplate template = new StringRedisTemplate();
//        template.setConnectionFactory(redisConnectionFactory);
//
//        // 设置 key 和 hashKey 的序列化器
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setHashKeySerializer(new StringRedisSerializer());
//
//        // 设置 value 和 hashValue 的序列化器
//        template.setValueSerializer(new StringRedisSerializer());
//        template.setHashValueSerializer(new StringRedisSerializer());
//
//        // 启用事务支持（可选）
//        template.setEnableTransactionSupport(true);
//
//        return template;
//    }
}
