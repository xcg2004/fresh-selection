package com.xcg.serviceproduct.service;

import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.core.utils.ScrollQueryParam;
import com.xcg.freshcommon.core.utils.ScrollResultVO;
import com.xcg.freshcommon.domain.cart.vo.CartVO;

import com.xcg.freshcommon.domain.product.dto.ProductDto;
import com.xcg.freshcommon.domain.product.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;

import com.xcg.freshcommon.domain.product.vo.ProductScrollVO;
import com.xcg.freshcommon.domain.productSku.entity.ProductSku;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品SPU表 服务类
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-01
 */
public interface IProductService extends IService<Product> {

    Result<Long> createProduct(ProductDto productDto);

    Result<ScrollResultVO<ProductScrollVO>> scrollPage(ScrollQueryParam scrollQueryParam);

    Result<Boolean> batchChangeStatus(List<Long> productIds);

    Result<Boolean> checkStatusWithStock(Long skuId, Integer quantity, Boolean strictStockCheck);

    Result<ProductScrollVO> getProductInfo(Long productId);

    Result<CartVO> fillOtherFields(CartVO cartVO);

    Result<Product> getProductBySkuId(Long skuId);

    Result<List<ProductSku>> deductStock(Map<Long, Integer> skuIdAndQuantity);

    Result<Boolean> batchCheckStatusWithStock(Map<Long,Integer> skuIdAndQuantity);

    Result<Boolean> recoverStock(Map<Long, Integer> skuIdAndQuantity);
}
