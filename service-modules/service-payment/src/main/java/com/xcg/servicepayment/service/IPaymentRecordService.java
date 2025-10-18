package com.xcg.servicepayment.service;

import com.xcg.freshcommon.core.utils.PageQueryResult;
import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.domain.payment.entity.PaymentRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 支付记录表 服务类
 * </p>
 *
 * @author xcg2004
 * @since 2025-10-15
 */
public interface IPaymentRecordService extends IService<PaymentRecord> {

    void insert(String outTradeNo, String tradeNo, BigDecimal paymentAmount, LocalDateTime payTime);

    void insertFailedPayment(String outTradeNo, String tradeNo, BigDecimal paymentAmount, LocalDateTime payTime);

    PaymentRecord getByOutTradeNo(String outTradeNo);

    Result<PageQueryResult<PaymentRecord>> pageQuery(Integer pageNum, Integer pageSize, Integer status, LocalDateTime startTime, LocalDateTime endTime);
}
