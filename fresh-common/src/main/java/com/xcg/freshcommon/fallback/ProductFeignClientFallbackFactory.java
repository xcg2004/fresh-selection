package com.xcg.freshcommon.fallback;

import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.domain.cart.vo.CartVO;
import com.xcg.freshcommon.domain.product.entity.Product;
import com.xcg.freshcommon.feign.ProductFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductFeignClientFallbackFactory implements FallbackFactory<ProductFeignClient> {
    @Override
    public ProductFeignClient create(Throwable cause) {
        return new ProductFeignClient() {
            @Override
            public Result<Boolean> checkStatusWithStock(Long skuId, Integer quantity) {
                log.error("校验状态及库存失败： {} {}", skuId, quantity, cause);
                return Result.error("商品服务暂时不可用");
            }

            @Override
            public Result<CartVO> fillOtherFields(CartVO cartVO) {
                log.error("填充CartVO其他字段失败：{}",cartVO, cause);
                return Result.error("商品服务暂时不可用");
            }

            @Override
            public Result<Product> getProductBySkuId(Long skuId) {
                log.error("查询商品信息失败：{}", skuId, cause);
                return Result.error("商品服务暂时不可用");
            }
        };
    }
}
