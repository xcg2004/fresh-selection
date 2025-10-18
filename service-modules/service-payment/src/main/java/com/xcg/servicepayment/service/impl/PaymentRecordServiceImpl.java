package com.xcg.servicepayment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcg.freshcommon.core.exception.BizException;
import com.xcg.freshcommon.core.utils.PageQueryResult;
import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.domain.order.entity.Orders;
import com.xcg.freshcommon.domain.payment.entity.PaymentRecord;
import com.xcg.freshcommon.enums.PaymentType;
import com.xcg.freshcommon.feign.OrderFeignClient;
import com.xcg.servicepayment.mapper.PaymentRecordMapper;
import com.xcg.servicepayment.service.IPaymentRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 支付记录表 服务实现类
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-15
 */
@Service
@RequiredArgsConstructor
public class PaymentRecordServiceImpl extends ServiceImpl<PaymentRecordMapper, PaymentRecord> implements IPaymentRecordService {

    private final OrderFeignClient orderFeignClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(String outTradeNo, String tradeNo, BigDecimal paymentAmount, LocalDateTime payTime) {
        //1.获取orderId
        Result<Orders> ordersResult = orderFeignClient.getByOrderNo(outTradeNo);
        if (!ordersResult.isSuccess()) {
            throw new BizException("订单不存在");
        }

        Orders orders = ordersResult.getData();

        //2.幂等
        PaymentRecord one = getOne(new LambdaQueryWrapper<PaymentRecord>()
                .eq(PaymentRecord::getOrderId, orders.getId())
                .eq(PaymentRecord::getPaymentNo, outTradeNo)
        );
        if (one != null) {
            return;
        }

        //3.插入支付记录
        PaymentRecord paymentRecord = new PaymentRecord();

        paymentRecord.setOrderId(orders.getId());
        paymentRecord.setPaymentNo(outTradeNo);
        paymentRecord.setPaymentAmount(paymentAmount);
        paymentRecord.setPaymentType(PaymentType.ALI_PAY);//支付宝
        paymentRecord.setPaymentStatus(1);//支付成功
        paymentRecord.setThirdPartyTransactionId(tradeNo);
        paymentRecord.setPayTime(payTime);
        save(paymentRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertFailedPayment(String outTradeNo, String tradeNo, BigDecimal paymentAmount, LocalDateTime payTime) {
        //1.获取orderId
        Result<Orders> ordersResult = orderFeignClient.getByOrderNo(outTradeNo);
        if (!ordersResult.isSuccess()) {
            throw new BizException("订单不存在");
        }

        Orders orders = ordersResult.getData();

        //2.幂等
        PaymentRecord one = getOne(new LambdaQueryWrapper<PaymentRecord>()
                .eq(PaymentRecord::getOrderId, orders.getId())
                .eq(PaymentRecord::getPaymentNo, outTradeNo)
        );
        if (one != null) {
            return;
        }

        //3.插入支付记录
        PaymentRecord paymentRecord = new PaymentRecord();

        paymentRecord.setOrderId(orders.getId());
        paymentRecord.setPaymentNo(outTradeNo);
        paymentRecord.setPaymentAmount(paymentAmount);
        paymentRecord.setPaymentType(PaymentType.ALI_PAY);//支付宝
        paymentRecord.setPaymentStatus(2);//支付成功
        paymentRecord.setThirdPartyTransactionId(tradeNo);
        paymentRecord.setPayTime(payTime);
        save(paymentRecord);
    }

    @Override
    public PaymentRecord getByOutTradeNo(String outTradeNo) {
        return getOne(new LambdaQueryWrapper<PaymentRecord>()
                .eq(PaymentRecord::getPaymentNo, outTradeNo)
        );
    }

    @Override
    public Result<PageQueryResult<PaymentRecord>> pageQuery(Integer pageNum, Integer pageSize, Integer status, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            //1.校验参数
            if (pageNum == null || pageNum < 1 || pageNum > 100) {
                return Result.error("页码异常");
            }

            if (pageSize == null || pageSize < 1) {
                return Result.error("页大小异常");
            }

            if (status != null && (status < 0 || status > 2)) {
                return Result.error("状态异常");
            }

            // 时间范围校验可以更完善
            if (startTime != null && endTime != null && !startTime.isBefore(endTime)) {
                return Result.error("开始时间必须小于结束时间");
            }

            if (startTime != null && startTime.isBefore(LocalDateTime.now().minusDays(90))) {
                return Result.error("开始时间不能超过90天");
            }

            //2.查询
            Page<PaymentRecord> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<PaymentRecord> wrapper = new LambdaQueryWrapper<PaymentRecord>()
                    .eq(status != null, PaymentRecord::getPaymentStatus, status)
                    .ge(startTime != null, PaymentRecord::getPayTime, startTime)
                    .le(endTime != null, PaymentRecord::getPayTime, endTime)
                    .orderByDesc(PaymentRecord::getPayTime); // 按支付时间倒序排列

            page(page, wrapper);
            List<PaymentRecord> records = page.getRecords();
            PageQueryResult<PaymentRecord> pageQueryResult = new PageQueryResult<>(page.getTotal(), page.getCurrent(), page.getPages(), records);

            return Result.success(pageQueryResult);
        } catch (Exception e) {
            log.error("分页查询异常：", e);
            return Result.error("分页查询异常");
        }
    }

}
