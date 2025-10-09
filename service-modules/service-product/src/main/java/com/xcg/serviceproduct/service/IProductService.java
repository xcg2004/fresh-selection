package com.xcg.serviceproduct.service;

import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.core.utils.ScrollResultVO;
import com.xcg.freshcommon.domain.product.dto.ProductDto;
import com.xcg.freshcommon.domain.product.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xcg.freshcommon.domain.product.vo.ProductScrollVO;

import java.time.LocalDateTime;
import java.util.List;

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

    Result<ScrollResultVO<ProductScrollVO>> scrollPage(Integer pageSize, Long lastId, LocalDateTime lastCreateTime);

    Result<Boolean> batchChangeStatus(List<Long> productIds);
}
