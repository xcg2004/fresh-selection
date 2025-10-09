package com.xcg.serviceproduct.controller;



import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.core.utils.ScrollResultVO;
import com.xcg.freshcommon.domain.product.dto.ProductDto;
import com.xcg.freshcommon.domain.product.entity.Product;
import com.xcg.freshcommon.domain.product.vo.ProductScrollVO;
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
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime lastCreateTime)
    {
        log.info("分页查询商品： size={} lastId={} lastCreateTime={}", pageSize, lastId, lastCreateTime);
        return productService.scrollPage(pageSize, lastId, lastCreateTime);
    }

}

