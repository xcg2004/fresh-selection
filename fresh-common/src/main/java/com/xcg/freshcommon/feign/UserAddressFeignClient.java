package com.xcg.freshcommon.feign;

import com.xcg.freshcommon.core.config.FeignConfig;
import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.domain.userAddress.vo.UserAddressVO;

import com.xcg.freshcommon.fallback.UserAddressFeignClientFallbackFactory;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



/**
 * 属性服务Feign客户端
 * 用于远程调用属性相关接口
 */
@FeignClient(
        name = "service-user",  // 对应属性服务的服务名
        path = "/api/user-address",     // 基础路径
        fallbackFactory = UserAddressFeignClientFallbackFactory.class,  // 降级处理工厂
        configuration = FeignConfig.class  // Feign配置类
)
public interface UserAddressFeignClient {


    @GetMapping("/{id}")
    Result<UserAddressVO> get(@PathVariable Long id);


}
