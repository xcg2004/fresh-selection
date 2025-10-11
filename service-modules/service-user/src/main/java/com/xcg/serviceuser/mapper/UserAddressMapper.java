package com.xcg.serviceuser.mapper;

import com.xcg.freshcommon.domain.userAddress.entity.UserAddress;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 用户地址表 Mapper 接口
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-11
 */
public interface UserAddressMapper extends BaseMapper<UserAddress> {

    Boolean updateInfo(UserAddress userAddress);


}
