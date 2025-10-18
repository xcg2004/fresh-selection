package com.xcg.servicepayment.controller;


import com.xcg.freshcommon.core.utils.PageQueryResult;
import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.domain.payment.entity.PaymentRecord;
import com.xcg.servicepayment.service.IPaymentRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * <p>
 * 支付记录表 前端控制器
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-15
 */
@RestController
@RequestMapping("/api/payment-record")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "支付记录")
public class PaymentRecordController {

    private final IPaymentRecordService paymentRecordService;

    @GetMapping("/{id}")
    @ApiOperation("通过id查询")
    public Result<PaymentRecord> getById(@PathVariable Long id) {
        return Result.success(paymentRecordService.getById(id));
    }

    @GetMapping("/outTradeNo/{outTradeNo}")
    @ApiOperation("通过orderNo查询")
    public Result<PaymentRecord> getByOutTradeNo(@PathVariable String outTradeNo) {
        return Result.success(paymentRecordService.getByOutTradeNo(outTradeNo));
    }

    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageQueryResult<PaymentRecord>> page(
            @RequestParam(required = false, defaultValue = "1", name = "pageNum") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10", name = "pageSize") Integer pageSize,
            @RequestParam(required = false, name = "status") Integer status,
            @RequestParam(required = false, name = "startTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false, name = "endTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime
    ) {
        log.info("分页查询： pageNum={} pageSize={} status={} startTime={} endTime={}", pageNum, pageSize, status, startTime, endTime);
        return paymentRecordService.pageQuery(pageNum, pageSize, status, startTime, endTime);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除单个记录")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(paymentRecordService.removeById(id));
    }

    @PutMapping("/{id}")
    @ApiOperation("更新单个记录")
    public Result<Boolean> update(@RequestBody PaymentRecord paymentRecord) {
        return Result.success(paymentRecordService.updateById(paymentRecord));
    }

}
