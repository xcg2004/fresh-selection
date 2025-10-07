package com.xcg.freshcommon.fallback;

import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.feign.CategoryFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

// 添加熔断实现
@Component
@Slf4j
public class CategoryFeignClientFallbackFactory implements FallbackFactory<CategoryFeignClient> {
    @Override
    public CategoryFeignClient create(Throwable cause) {
        return categoryId -> {
            log.error("调用分类服务失败，categoryId: {}", categoryId, cause);
            return Result.error("分类服务暂时不可用");
        };
    }
}