package com.xcg.serviceproduct.service.impl;

import com.xcg.serviceproduct.domain.entity.Product;
import com.xcg.serviceproduct.mapper.ProductMapper;
import com.xcg.serviceproduct.service.IProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品SPU表 服务实现类
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-01
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

}
