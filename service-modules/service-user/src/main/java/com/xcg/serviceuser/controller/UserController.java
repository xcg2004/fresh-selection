package com.xcg.serviceuser.controller;

import com.xcg.freshcommon.core.utils.JwtUtil;
import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.domain.user.dto.UserLoginDto;
import com.xcg.freshcommon.domain.user.dto.UserRegisterDto;
import com.xcg.freshcommon.domain.user.vo.UserVO;
import com.xcg.serviceuser.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "用户模块")
public class UserController {

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;
    private final IUserService userService;

//    @GetMapping("/token")
//    public Result<String> getToken() {
//        String token = jwtUtil.generateToken(1L, "xcg");
//        log.info("token:{}",token);
//        redisTemplate.opsForValue().set(RedisConstants.TOKEN_PREFIX + 1L,token);
//        return Result.success(token);
//    }


    @PostMapping("/register")
    @ApiOperation("用户注册")
    public Result<String> register(@Valid @RequestBody UserRegisterDto userRegisterDto) {
        log.info("用户注册:{}", userRegisterDto);
        return userService.register(userRegisterDto);
    }

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<String> login(UserLoginDto userLoginDto) {
        log.info("用户登录:{}", userLoginDto);
        return userService.login(userLoginDto);
    }

    @GetMapping("/info")
    @ApiOperation("获取用户信息")
    public Result<UserVO> info(HttpServletRequest request) {
        String userId = request.getHeader("X-User-ID");

        log.info("获取用户id：{}的详细信息", userId);
        return userService.getInfoById(Long.valueOf(userId));
    }

    @PostMapping("/logout")
    @ApiOperation("用户登出")
    public Result<String> logout(HttpServletRequest request) {
        String userId = request.getHeader("X-User-ID");
        log.info("用户:{}登出", userId);
        return userService.logout(Long.valueOf(userId));
    }


}
