package com.xcg.freshcommon.feign;

import com.xcg.freshcommon.core.config.FeignConfig;
import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.domain.cart.vo.CartVO;
import com.xcg.freshcommon.domain.order.dto.OrderCreateDto;
import com.xcg.freshcommon.domain.product.entity.Product;
import com.xcg.freshcommon.domain.productSku.entity.ProductSku;
import com.xcg.freshcommon.fallback.ProductFeignClientFallbackFactory;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.awt.datatransfer.Clipboard;
import java.util.List;
import java.util.Map;

@FeignClient(name = "service-product", path = "/api/product",
        fallbackFactory = ProductFeignClientFallbackFactory.class,
        configuration = FeignConfig.class
)
public interface ProductFeignClient {

    @GetMapping("/check/status-with-stock/{skuId}")
    Result<Boolean> checkStatusWithStock(@PathVariable Long skuId,
                                         @RequestParam("quantity") Integer quantity,
                                         @RequestParam("strictStockCheck") Boolean strictStockCheck);

    @PostMapping("/fill-other-fields")
    Result<CartVO> fillOtherFields(@RequestBody CartVO cartVO);

    @GetMapping("/{skuId}")
    Result<Product> getProductBySkuId(@PathVariable Long skuId);

    @PutMapping("/deduct")
    Result<List<ProductSku>> deductStock(@RequestBody Map<Long, Integer> skuIdAndQuantity);

    @PostMapping("/batch/check-status-with-stock")
    Result<Boolean> batchCheckStatusWithStock(@RequestBody Map<Long,Integer> skuIdAndQuantity);

    @PutMapping("/recover")
    Result<Boolean> recoverStock(@RequestBody Map<Long, Integer> skuIdAndQuantity);
}
