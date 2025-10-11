package com.xcg.serviceorder.controller;


import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.domain.order.dto.OrderCreateDto;
import com.xcg.freshcommon.enums.PayType;
import com.xcg.serviceorder.service.IOrdersService;
import io.swagger.annotations.Api;
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

    /** todo
     * - [ ] 从购物车生成订单 `POST /api/orders/create`
     * - [ ] 订单列表查询 `GET /api/orders`
     * - [ ] 订单详情查询 `GET /api/orders/{id}`
     */


//    @PostMapping("/create")
//    public Result<Long> create(@RequestBody List<OrderCreateDto> orderCreateDto,
//                               @RequestParam @NotNull Long addressId,
//                               @RequestParam(required = false,defaultValue = "1") PayType payType) {
//        log.info("创建订单: {} {} {}", orderCreateDto, addressId, payType);
//        return ordersService.create(orderCreateDto, addressId, payType);
//    }

}
