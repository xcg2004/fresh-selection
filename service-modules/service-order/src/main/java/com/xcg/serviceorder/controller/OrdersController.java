package com.xcg.serviceorder.controller;


import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.core.utils.ScrollQueryParam;
import com.xcg.freshcommon.core.utils.ScrollResultVO;
import com.xcg.freshcommon.domain.order.dto.OrderCreateDto;
import com.xcg.freshcommon.domain.order.entity.Orders;
import com.xcg.freshcommon.domain.order.vo.OrderVO;
import com.xcg.freshcommon.enums.PayType;
import com.xcg.serviceorder.service.IOrdersService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-11
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "订单")
public class OrdersController {

    private final IOrdersService ordersService;

    @PostMapping("/create")
    @ApiOperation("创建订单")
    public Result<Long> create(@RequestBody @Valid List<OrderCreateDto> orderCreateDto,
                               @RequestParam @NotNull Long addressId,
                               @RequestParam(required = false,defaultValue = "WECHAT") PayType payType) {
        log.info("创建订单: {} {} {}", orderCreateDto, addressId, payType);
        return ordersService.create(orderCreateDto, addressId, payType);
    }

    @GetMapping("/{orderId}")
    @ApiOperation("查询订单")
    public Result<OrderVO> get(@PathVariable @NotNull Long orderId) {
        log.info("查询订单: {}", orderId);
        return ordersService.selectById(orderId);
    }

    @GetMapping("/scroll/page")
    @ApiOperation("滚动分页查询订单")
    public Result<ScrollResultVO<OrderVO>> scrollPage(@Valid ScrollQueryParam scrollQueryParam) {
        log.info("滚动分页查询订单: {}", scrollQueryParam);
        return ordersService.scrollPage(scrollQueryParam);
    }

    @GetMapping("/getByOrderNo")
    @ApiOperation("根据订单号查询订单")
    public Result<Orders> getByOrderNo(@RequestParam("outTradeNo") String outTradeNo) {
        return ordersService.getByOrderNo(outTradeNo);
    }

    @PutMapping("/update/status-paytime")
    @ApiOperation("更新订单状态")
    public Result<Boolean> updateStatusAndPayTime(@RequestParam("outTradeNo") String outTradeNo,
                                                  @RequestParam("status") Integer status) {
        return ordersService.updateStatusAndPayTime(outTradeNo, status);
    }
}
