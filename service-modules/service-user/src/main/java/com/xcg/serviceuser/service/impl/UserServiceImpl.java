package com.xcg.serviceuser.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xcg.freshcommon.core.utils.JwtUtil;
import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.rabbitmq.constants.RedisConstants;
import com.xcg.serviceuser.domain.dto.UserLoginDto;
import com.xcg.serviceuser.domain.dto.UserRegisterDto;
import com.xcg.serviceuser.domain.entity.User;
import com.xcg.serviceuser.domain.vo.UserVO;
import com.xcg.serviceuser.enums.GenderEnum;
import com.xcg.serviceuser.mapper.UserMapper;
import com.xcg.serviceuser.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-03
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final JwtUtil jwtUtil;

    private final StringRedisTemplate redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> register(UserRegisterDto userRegisterDto) {
        //1.先校验参数
        if(userRegisterDto.getGender() != null && (userRegisterDto.getGender() < 0 || userRegisterDto.getGender() > 2)){
            return Result.error("性别异常");
        }
        if(userRegisterDto.getBirthday() != null && userRegisterDto.getBirthday().isBefore(LocalDate.of(1900, 1, 1))){
            return Result.error("生日异常");
        }
        // 安全的枚举转换
        GenderEnum genderEnum = null;
        if (userRegisterDto.getGender() != null) {
            try {
                genderEnum = GenderEnum.fromCode(userRegisterDto.getGender());
            } catch (IllegalArgumentException e) {
                return Result.error("性别参数异常");
            }
        }

        User user = User.builder()
                .username(userRegisterDto.getUsername())
                .password(userRegisterDto.getPassword())
                .phone(userRegisterDto.getPhone())
                .email(userRegisterDto.getEmail())
                .nickname(userRegisterDto.getNickname())
                .avatar(userRegisterDto.getAvatar())
                .gender(genderEnum)
                .birthday(userRegisterDto.getBirthday())
                .build();

        //2.密码加密
        String password = user.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        user.setPassword(password);

        //3.保存用户
        boolean save = save(user);
        if(save){
            return Result.success("注册成功");
        }
        return Result.error("注册失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> login(UserLoginDto userLoginDto) {
        //分析参数
        User user = null;
        if(StringUtils.hasText(userLoginDto.getUsername())){
            //用户名登录
            user = getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, userLoginDto.getUsername()));
        }else if(StringUtils.hasText(userLoginDto.getPhone())){
            //手机号登录
            user = getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, userLoginDto.getPhone()));
        }else if(StringUtils.hasText(userLoginDto.getEmail())){
            //邮箱登录
            user = getOne(new LambdaQueryWrapper<User>().eq(User::getEmail, userLoginDto.getEmail()));
        }else{
            return Result.error("请输入用户名、手机号或邮箱");
        }

        if(user == null){
            return Result.error("用户不存在");
        }

        //校验密码
        if(!DigestUtils.md5DigestAsHex(userLoginDto.getPassword().getBytes()).equals(user.getPassword())){
            return Result.error("密码错误");
        }

        //登录成功
        //1.生成token
        Long userId = user.getId();
        String token = jwtUtil.generateToken(userId, user.getUsername());
        //2.保存token到redis 60分钟
        redisTemplate.opsForValue().set(
                RedisConstants.TOKEN_PREFIX + user.getId(), token,
                RedisConstants.TOKEN_EXPIRE_TIME, TimeUnit.MINUTES
        );
        //更新最后登录时间
        update(User.builder().build().setLastLoginTime(LocalDateTime.now()), new LambdaQueryWrapper<User>().eq(User::getId, userId));
        //3.返回结果
        return Result.success(token);
    }

    @Override
    public Result<UserVO> getInfoById(Long userId) {
        if(userId == null){
            return Result.error("用户ID不能为空");
        }

        User user = getById(userId);
        if(user == null){
            return Result.error("用户不存在");
        }
        return Result.success(UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .phone(user.getPhone())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .gender(user.getGender().getDescription())
                .birthday(user.getBirthday())
                .lastLoginTime(user.getLastLoginTime()).build()
        );
    }

    @Override
    public Result<String> logout(Long userId) {
        //删除redis中的token
        redisTemplate.delete(RedisConstants.TOKEN_PREFIX + userId);
        return Result.success("注销成功");
    }
}
