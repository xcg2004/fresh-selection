package com.xcg.freshcommon.feign;

import com.xcg.freshcommon.core.config.FeignConfig;
import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.domain.category.entity.Category;
import com.xcg.freshcommon.domain.category.vo.CategoryVO;
import com.xcg.freshcommon.fallback.CategoryFeignClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-category", path = "/api/category",
        fallbackFactory = CategoryFeignClientFallbackFactory.class,
        configuration = FeignConfig.class)
public interface CategoryFeignClient {

    @GetMapping("/{id}")
    Result<CategoryVO> getById(@PathVariable Long id);
}

