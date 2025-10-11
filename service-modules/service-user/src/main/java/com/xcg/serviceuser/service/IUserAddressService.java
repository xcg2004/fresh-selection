package com.xcg.serviceuser.service;

import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.domain.userAddress.dto.UserAddressDto;
import com.xcg.freshcommon.domain.userAddress.entity.UserAddress;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xcg.freshcommon.domain.userAddress.vo.UserAddressVO;

import java.util.List;

/**
 * <p>
 * 用户地址表 服务类
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-11
 */
public interface IUserAddressService extends IService<UserAddress> {

    Result<Long> add(UserAddressDto userAddressDto);

    Result<List<UserAddressVO>> listByUser();

    Result<UserAddressVO> selectById(Long id);

    Result<Boolean> updateInfoById(Long id, UserAddressDto userAddressDto);

    Result<Boolean> deleteById(Long id);

    Result<Boolean> deleteBatchById(List<Long> ids);
}
