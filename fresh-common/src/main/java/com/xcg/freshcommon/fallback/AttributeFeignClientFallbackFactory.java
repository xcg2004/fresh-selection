package com.xcg.freshcommon.fallback;

import com.xcg.freshcommon.core.exception.BizException;
import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.feign.AttributeFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class AttributeFeignClientFallbackFactory implements FallbackFactory<AttributeFeignClient> {

    @Override
    public AttributeFeignClient create(Throwable cause) {
        log.error("调用属性服务失败", cause);
        return new AttributeFeignClient() {
            @Override
            public Result<List<Long>> getAttrIdsByCategoryId(Long categoryId) {
                throw new BizException("调用属性服务失败");
            }

            @Override
            public Result<Boolean> checkAttributeValueValid(Long attributeId, Long attributeValueId) {
                throw new BizException("调用属性服务失败");
            }

            @Override
            public Result<String> getAttributeName(Long attributeId) {
                throw new BizException("调用属性服务失败");
            }

            @Override
            public Result<String> getAttributeValue(Long attributeValueId) {
                throw new BizException("调用属性服务失败");
            }
        };
    }
}
