package com.xcg.serviceproduct.controller;


import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.core.utils.ScrollResultVO;
import com.xcg.freshcommon.domain.cart.vo.CartVO;
import com.xcg.freshcommon.domain.order.dto.OrderCreateDto;
import com.xcg.freshcommon.domain.product.dto.ProductDto;
import com.xcg.freshcommon.domain.product.entity.Product;
import com.xcg.freshcommon.domain.product.vo.ProductInfoVO;
import com.xcg.freshcommon.domain.product.vo.ProductScrollVO;
import com.xcg.freshcommon.domain.productSku.entity.ProductSku;
import com.xcg.serviceproduct.service.IProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品SPU表 前端控制器
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-01
 */
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "商品")
public class ProductController {

    private final IProductService productService;

    private final RabbitTemplate rabbitTemplate;


    @PostMapping("/send-message")
    public Result<String> sendMessage() {
        Message message = MessageBuilder.withBody("hello world".getBytes()).build();
        rabbitTemplate.send("my.exchange", "my.routing.key", message);
        return Result.success("发送成功");
    }

    @PostMapping("/create")
    @ApiOperation("创建商品")
    public Result<Long> createProduct(@RequestBody @Valid ProductDto productDto) {
        return productService.createProduct(productDto);
    }

    @GetMapping("/scroll/page")
    @ApiOperation("分页查询商品")
    public Result<ScrollResultVO<ProductScrollVO>> scrollPage(
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Long lastId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime lastCreateTime) {
        log.info("分页查询商品： size={} lastId={} lastCreateTime={}", pageSize, lastId, lastCreateTime);
        return productService.scrollPage(pageSize, lastId, lastCreateTime);
    }


    @PutMapping("/batch-change/status")
    @ApiOperation("批量修改商品状态")
    public Result<Boolean> batchChangeStatus(@RequestBody List<Long> productIds) {
        return productService.batchChangeStatus(productIds);
    }

    @GetMapping("/check/status-with-stock/{skuId}")
    @ApiOperation("检查商品状态及库存")
    public Result<Boolean> checkStatusWithStock(@PathVariable Long skuId,
                                                @RequestParam("quantity") Integer quantity,
                                                @RequestParam("strictStockCheck") Boolean strictStockCheck) {
        log.info("检查商品状态及库存： skuId={} quantity={} strictCheck={}", skuId, quantity, strictStockCheck);
        return productService.checkStatusWithStock(skuId, quantity, strictStockCheck);
    }

    @GetMapping("/info/{productId}")
    @ApiOperation("查询商品信息")
    public Result<ProductScrollVO> getProductInfo(@PathVariable Long productId) {
        return productService.getProductInfo(productId);
    }

    @PostMapping("/fill-other-fields")
    @ApiOperation("填充其他字段")
    public Result<CartVO> fillOtherFields(@RequestBody CartVO cartVO) {
        return productService.fillOtherFields(cartVO);
    }

    @GetMapping("/{skuId}")
    @ApiOperation("查询商品信息")
    public Result<Product> getProductBySkuId(@PathVariable Long skuId) {
        return productService.getProductBySkuId(skuId);
    }

    @PutMapping("/deduct")
    public Result<List<ProductSku>> deductStock(@RequestBody Map<Long, Integer> skuIdAndQuantity){
        return productService.deductStock(skuIdAndQuantity);
    }

    @PostMapping("/batch/check-status-with-stock")
    @ApiOperation("批量检查商品状态及库存")
    public Result<Boolean> batchCheckStatusWithStock(@RequestBody Map<Long,Integer> skuIdAndQuantity){
        return productService.batchCheckStatusWithStock(skuIdAndQuantity);
    }

    @PutMapping("/recover")
    Result<Boolean> recoverStock(@RequestBody Map<Long, Integer> skuIdAndQuantity){
        return productService.recoverStock(skuIdAndQuantity);
    }
}

