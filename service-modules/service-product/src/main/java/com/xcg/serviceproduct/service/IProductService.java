package com.xcg.serviceproduct.service;

import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.domain.product.dto.ProductDto;
import com.xcg.freshcommon.domain.product.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
