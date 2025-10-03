package com.xcg.serviceproduct.controller;



import com.xcg.freshcommon.core.exception.BizException;
import com.xcg.freshcommon.core.utils.Result;
import com.xcg.serviceproduct.domain.entity.Product;
import com.xcg.serviceproduct.service.IProductService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("list")
    public Result<List<Product>> list() {
        return Result.success(productService.list());
//        throw new BizException(500,"异常");
    }

    @PostMapping("send-message")
    public Result<String> sendMessage() {
        Message message = MessageBuilder.withBody("hello world".getBytes()).build();
        rabbitTemplate.send("my.exchange", "my.routing.key", message);
        return Result.success("发送成功");
    }


}
