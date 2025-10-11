package com.xcg.serviceuser.controller;


import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.domain.userAddress.dto.UserAddressDto;
import com.xcg.freshcommon.domain.userAddress.vo.UserAddressVO;
import com.xcg.serviceuser.service.IUserAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户地址表 前端控制器
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-11
 */
@RestController
@RequestMapping("/api/user-address")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "用户地址")
public class UserAddressController {

    private final IUserAddressService userAddressService;


    @PostMapping("/add")
    @ApiOperation("添加用户地址")
    public Result<Long> add(@RequestBody @Valid UserAddressDto userAddressDto) {
        log.info("添加用户地址：{}", userAddressDto);
        return userAddressService.add(userAddressDto);
    }

    @GetMapping("/list")
    @ApiOperation("获取用户地址列表")
    public Result<List<UserAddressVO>> list() {
        return userAddressService.listByUser();
    }

    @GetMapping("/{id}")
    @ApiOperation("获取指定ID的用户地址")
    public Result<UserAddressVO> get(@PathVariable Long id) {
        return userAddressService.selectById(id);
    }

    @PutMapping("/update/{id}")
    @ApiOperation("更新指定ID的用户地址")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody @Valid UserAddressDto userAddressDto) {
        log.info("更新用户地址：{}", userAddressDto);
        return userAddressService.updateInfoById(id, userAddressDto);
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除指定ID的用户地址")
    public Result<Boolean> delete(@PathVariable Long id) {
        log.info("删除用户地址：{}", id);
        return userAddressService.deleteById(id);
    }

    @DeleteMapping("/delete-batch")
    @ApiOperation("批量删除用户地址")
    public Result<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        log.info("批量删除用户地址：{}", ids);
        return userAddressService.deleteBatchById(ids);
    }


}
