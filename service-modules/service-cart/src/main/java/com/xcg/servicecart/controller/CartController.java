package com.xcg.servicecart.controller;


import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.core.utils.ScrollResultVO;
import com.xcg.freshcommon.domain.cart.vo.CartVO;
import com.xcg.servicecart.service.ICartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 购物车表 前端控制器
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-09
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "购物车")
public class CartController {

    private final ICartService cartService;

    @PostMapping("/add/{skuId}")
    @ApiOperation("添加购物车")
    public Result<Long> add(@PathVariable Long skuId,
                            @RequestParam("quantity") @NotNull Integer quantity) {
        log.info("添加购物车：skuId={}, quantity={}", skuId, quantity);
        return cartService.add(skuId, quantity);
    }

    @PutMapping("/update/quantity/{cartId}")
    @ApiOperation("更新购物车数量")
    public Result<Boolean> updateQuantity(@PathVariable Long cartId,
                                       @RequestParam @NotNull Integer quantity) {
        log.info("更新购物车数量：cartId={}, quantity={}", cartId, quantity);
        return cartService.updateQuantity(cartId, quantity);
    }

    @DeleteMapping("/delete/{cartId}")
    @ApiOperation("删除购物车")
    public Result<Boolean> delete(@PathVariable Long cartId) {
        log.info("删除购物车：cartId={}", cartId);
        return cartService.deleteById(cartId);
    }

    @DeleteMapping("/batch/delete")
    @ApiOperation("批量删除购物车")
    public Result<Boolean> batchDelete(@RequestBody @NotNull List<Long> cartIds){
        log.info("批量删除购物车：cartIds={}", cartIds);
        return cartService.batchDelete(cartIds);
    }


    @PutMapping("/update/selected/{cartId}")
    @ApiOperation("更新购物车选中状态")
    public Result<Boolean> updateSelected(@PathVariable Long cartId) {
        log.info("更新购物车选中状态：cartId={}", cartId);
        return cartService.updateSelected(cartId);
    }

    @GetMapping("/scroll/page")
    @ApiOperation("分页查询购物车")
    public Result<ScrollResultVO<CartVO>> scrollPage(
        @RequestParam(required = false) Integer pageSize,
        @RequestParam(required = false) Long lastId,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime lastCreateTime)
    {
        log.info("分页查询购物车： size={} lastId={} lastCreateTime={}", pageSize, lastId, lastCreateTime);
        return cartService.scrollPage(pageSize, lastId, lastCreateTime);
    }

    @PutMapping("/update/spec/{cartId}")
    @ApiOperation("更换购物车规格")
    public Result<Boolean> updateSpec(@PathVariable Long cartId,
                                     @RequestParam @NotNull Long skuId) {
        log.info("更换购物车规格：cartId={}, skuId={}", cartId, skuId);
        return cartService.updateSpec(cartId, skuId);
    }
}
