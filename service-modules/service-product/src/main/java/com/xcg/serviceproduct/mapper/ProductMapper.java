package com.xcg.serviceproduct.mapper;

import com.xcg.freshcommon.domain.product.entity.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 商品SPU表 Mapper 接口
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-01
 */
public interface ProductMapper extends BaseMapper<Product> {

    List<Product> scrollPageByCursor(Integer pageSize, Long lastId, LocalDateTime lastCreateTime);

    Product selectProductBySkuId(Long skuId);
}
