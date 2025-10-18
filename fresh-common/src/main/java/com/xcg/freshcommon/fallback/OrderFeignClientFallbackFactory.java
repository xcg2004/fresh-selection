package com.xcg.freshcommon.fallback;

import com.xcg.freshcommon.feign.OrderFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderFeignClientFallbackFactory implements FallbackFactory<OrderFeignClient> {

    @Override
    public OrderFeignClient create(Throwable cause) {
        return null;
    }
}
