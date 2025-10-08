package com.xcg.freshcommon.fallback;

import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.feign.AttributeFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AttributeFeignClientFallbackFactory implements FallbackFactory<AttributeFeignClient> {

    @Override
    public AttributeFeignClient create(Throwable cause) {
        log.error("调用属性服务失败", cause);
        return null;
    }
}
