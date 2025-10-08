package com.xcg.serviceproduct.controller;


import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.domain.productSku.dto.ProductSkuDto;
import com.xcg.serviceproduct.service.IProductSkuService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 商品SKU表 前端控制器
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-08
 */
@RestController
@RequestMapping("/api/product-sku")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "商品SKU")
public class ProductSkuController {

    private final IProductSkuService productSkuService;


}
