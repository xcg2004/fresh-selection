package com.xcg.freshcommon.fallback;

import com.xcg.freshcommon.feign.UserAddressFeignClient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserAddressFeignClientFallbackFactory implements FallbackFactory<UserAddressFeignClient> {
    @Override
    public UserAddressFeignClient create(Throwable cause) {
        return null;
    }
}
