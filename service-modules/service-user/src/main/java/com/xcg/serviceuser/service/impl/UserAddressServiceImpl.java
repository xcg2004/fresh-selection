package com.xcg.serviceuser.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xcg.freshcommon.core.exception.BizException;
import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.core.utils.UserHolder;
import com.xcg.freshcommon.domain.userAddress.dto.UserAddressDto;
import com.xcg.freshcommon.domain.userAddress.entity.UserAddress;
import com.xcg.freshcommon.domain.userAddress.vo.UserAddressVO;
import com.xcg.serviceuser.mapper.UserAddressMapper;
import com.xcg.serviceuser.service.IUserAddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 用户地址表 服务实现类
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-11
 */
@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements IUserAddressService {

    private final UserHolder userHolder;

    private final UserAddressMapper userAddressMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> add(UserAddressDto userAddressDto) {
        //1.判断地址总数是否已经达到了10个
        Long userId = userHolder.getUserId();
        Long count = query().eq("user_id", userId).count();
        if (count >= 10) {
            throw new BizException("用户地址数量已达到上限");
        }

        //2.构建实体类
        UserAddress userAddress = UserAddress.builder()
                .userId(userId)
                .consignee(userAddressDto.getConsignee())
                .phone(userAddressDto.getPhone())
                .province(userAddressDto.getProvince())
                .city(userAddressDto.getCity())
                .district(userAddressDto.getDistrict())
                .detailAddress(userAddressDto.getDetailAddress())
                .postalCode(userAddressDto.getPostalCode())
                .isDefault(userAddressDto.getIsDefault())
                .build();

        //如果需要设置成默认地址，则将当前默认地址改为非默认
        updateDefault(userAddressDto, userId);

        //3.保存
        boolean success = save(userAddress);
        if (!success) {
            throw new BizException("添加用户地址失败");
        }
        return Result.success(userAddress.getId());
    }

    @Override
    public Result<List<UserAddressVO>> listByUser() {
        Long userId = userHolder.getUserId();

        List<UserAddress> userAddressList = list(new QueryWrapper<UserAddress>().eq("user_id", userId));

        List<UserAddressVO> userAddressVOList = userAddressList.stream().map(this::convertToVO).toList();

        return Result.success(userAddressVOList);
    }

    @Override
    public Result<UserAddressVO> selectById(Long id) {
        Long userId = userHolder.getUserId();

        UserAddress userAddress = getOne(new QueryWrapper<UserAddress>().eq("user_id", userId).eq("id", id));
        if (userAddress != null) {
            return Result.success(convertToVO(userAddress));
        }
        return Result.error("未找到对应的用户地址信息");
    }

    private UserAddressVO convertToVO(UserAddress userAddress) {
        return UserAddressVO.builder()
                .id(userAddress.getId())
                .consignee(userAddress.getConsignee())
                .phone(userAddress.getPhone())
                .province(userAddress.getProvince())
                .city(userAddress.getCity())
                .district(userAddress.getDistrict())
                .detailAddress(userAddress.getDetailAddress())
                .postalCode(userAddress.getPostalCode())
                .isDefault(userAddress.getIsDefault())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> updateInfoById(Long id, UserAddressDto userAddressDto) {
        Long userId = userHolder.getUserId();
        UserAddress userAddress = UserAddress.builder()
                .id(id)
                .userId(userId)
                .consignee(userAddressDto.getConsignee())
                .phone(userAddressDto.getPhone())
                .province(userAddressDto.getProvince())
                .city(userAddressDto.getCity())
                .district(userAddressDto.getDistrict())
                .detailAddress(userAddressDto.getDetailAddress())
                .postalCode(userAddressDto.getPostalCode())
                .isDefault(userAddressDto.getIsDefault())
                .build();
        //如果需要设置成默认地址，则将当前默认地址改为非默认
        updateDefault(userAddressDto, userId);

        //更新用户地址
        Boolean success = userAddressMapper.updateInfo(userAddress);

        if (!success) {
            throw new BizException("更新用户地址失败");
        }
        return Result.success(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deleteById(Long id) {
        Long userId = userHolder.getUserId();
        boolean success = remove(new QueryWrapper<UserAddress>().eq("user_id", userId).eq("id", id));
        if (!success) {
            throw new BizException("删除用户地址失败");
        }
        return Result.success(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deleteBatchById(List<Long> ids) {
        if (ids.isEmpty()) {
            return Result.success(true);
        }
        Long userId = userHolder.getUserId();
        boolean success = remove(new QueryWrapper<UserAddress>().eq("user_id", userId).in("id", ids));

        if (!success) {
            throw new BizException("批量删除用户地址失败");
        }
        return Result.success(true);
    }

    private void updateDefault(UserAddressDto userAddressDto, Long userId) {
        //如果需要设置成默认地址，则将当前默认地址改为非默认
        if (userAddressDto.getIsDefault() == 1) {
            try {
                update(new LambdaUpdateWrapper<UserAddress>()
                        .set(UserAddress::getIsDefault, 0)
                        .eq(UserAddress::getUserId, userId)
                        .eq(UserAddress::getIsDefault, 1)
                );
            } catch (Exception e) {
                log.error("更新用户默认地址失败", e);
            }
        }
    }
}
