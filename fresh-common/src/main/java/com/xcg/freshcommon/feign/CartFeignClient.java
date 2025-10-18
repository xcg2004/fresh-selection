package com.xcg.freshcommon.feign;

import com.xcg.freshcommon.core.config.FeignConfig;
import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.fallback.CartFeignClientFallbackFactory;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "service-cart",
        path = "/api/cart",
        fallbackFactory = CartFeignClientFallbackFactory.class,
        configuration = FeignConfig.class)
public interface CartFeignClient {


    @GetMapping("/check/{cartId}")
    Result<Boolean> check(@PathVariable Long cartId,
                          @RequestParam("skuId") Long skuId,
                          @RequestParam("quantity") Integer quantity);

    @DeleteMapping("/batch/delete")
    Result<Boolean> batchDelete(@RequestBody @NotNull List<Long> cartIds);

    @PostMapping("/add/{skuId}")
    Result<Long> add(@PathVariable Long skuId,
                     @RequestParam("quantity") @NotNull Integer quantity);
}
