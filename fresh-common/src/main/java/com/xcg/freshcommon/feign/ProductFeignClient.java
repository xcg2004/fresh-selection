package com.xcg.freshcommon.feign;

import com.xcg.freshcommon.core.config.FeignConfig;
import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.domain.cart.vo.CartVO;
import com.xcg.freshcommon.domain.product.entity.Product;
import com.xcg.freshcommon.fallback.ProductFeignClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.awt.datatransfer.Clipboard;

@FeignClient(name = "service-product", path = "/api/product",
        fallbackFactory = ProductFeignClientFallbackFactory.class,
        configuration = FeignConfig.class
)
public interface ProductFeignClient {

    @GetMapping("/check/status-with-stock/{skuId}")
    Result<Boolean> checkStatusWithStock(@PathVariable Long skuId, @RequestParam("quantity") Integer quantity);

    @PostMapping("/fill-other-fields")
    Result<CartVO> fillOtherFields(@RequestBody CartVO cartVO);

    @GetMapping("/{skuId}")
    Result<Product> getProductBySkuId(@PathVariable Long skuId);
}
