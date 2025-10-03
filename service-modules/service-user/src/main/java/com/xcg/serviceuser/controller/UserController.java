package com.xcg.serviceuser.controller;

import com.xcg.freshcommon.core.utils.JwtUtil;
import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.rabbitmq.constants.RedisConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "用户模块")
public class UserController {
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;
    @GetMapping("/token")
    public Result<String> getToken()
    {
        String token = jwtUtil.generateToken(1L, "xcg");
        log.info("token:{}",token);
        redisTemplate.opsForValue().set(RedisConstants.TOKEN_PREFIX + 1L,token);
        return Result.success(token);
    }

}
