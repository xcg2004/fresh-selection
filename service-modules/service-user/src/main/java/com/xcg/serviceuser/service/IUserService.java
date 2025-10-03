package com.xcg.serviceuser.service;

import com.xcg.freshcommon.core.utils.Result;
import com.xcg.serviceuser.domain.dto.UserLoginDto;
import com.xcg.serviceuser.domain.dto.UserRegisterDto;
import com.xcg.serviceuser.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xcg.serviceuser.domain.vo.UserVO;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-03
 */
public interface IUserService extends IService<User> {

    Result<String> register(UserRegisterDto userRegisterDto);

    Result<String> login(UserLoginDto userLoginDto);

    Result<UserVO> getInfoById(Long userId);

    Result<String> logout(Long userId);
}
