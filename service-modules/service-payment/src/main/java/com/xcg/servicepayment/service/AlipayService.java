package com.xcg.servicepayment.service;

import com.xcg.freshcommon.core.utils.Result;
import com.xcg.freshcommon.enums.OrderStatus;
import com.xcg.freshcommon.feign.OrderFeignClient;
import com.xcg.servicepayment.result.VerifyResult;
import com.xcg.servicepayment.util.AlipayUtil;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
@RequiredArgsConstructor
public class AlipayService {

    private final AlipayUtil alipayUtil;

    private final OrderFeignClient orderFeignClient;

    private final IPaymentRecordService paymentRecordService;

    private final StringRedisTemplate redisTemplate;

    private static final String PAY_NOTIFY_PREFIX = "pay:notify:";

    private static final String TIMEOUT = "1h"; // 1小时


    public String pay(String orderNo, String totalAmount, String title) {
        String form = alipayUtil.pay(orderNo, totalAmount, title, TIMEOUT);

        // 修复重复的gateway.do问题
        if (form != null && form.contains("/gateway.do/gateway.do")) {
            form = form.replace("/gateway.do/gateway.do", "/gateway.do");
            log.debug("已修复重复的gateway.do");
        }

        return form;
    }

    /**
     * 支付异步通知
     * - 规定时间内支付成功，则会异步触发
     * - 或者规定时间内未支付，则也会触发,但是TradeStatus=TRADE_CLOSED
     * @param request
     * @return
     */
    @GlobalTransactional(name = "tx-pay-notify")
    public String asyncNotify(HttpServletRequest request) {
        try {
            // 验证签名
            VerifyResult verifyResult = alipayUtil.verify(request);
            Boolean signVerified = verifyResult.getSignVerified();
            if (signVerified) {
                // 商户订单号
                String outTradeNo = verifyResult.getOutTradeNo();

                // 支付宝交易号(流水号)
                String tradeNo = verifyResult.getTradeNo();

                // 支付金额
                BigDecimal paymentAmount = new BigDecimal(verifyResult.getTotalAmount());

                // 交易状态
                String tradeStatus = verifyResult.getTradeStatus();

                // 支付时间
                LocalDateTime payTime = LocalDateTime.now();

                //幂等控制
                if (isPaymentRecordExist(tradeNo)) {
                    log.warn("幂等控制：支付宝重复通知，商户订单号：{}", outTradeNo);
                    return "success";
                }

                // redis 记录通知
                redisTemplate.opsForValue().set(PAY_NOTIFY_PREFIX + tradeNo, "1", 60, TimeUnit.MINUTES);

                if ("TRADE_FINISHED".equals(tradeStatus) || "TRADE_SUCCESS".equals(tradeStatus)) {
                    // 处理支付成功的业务逻辑
                    log.info("支付宝异步通知：支付成功，商户订单号：{}，支付宝交易号：{}", outTradeNo, tradeNo);

                    // 更新订单状态为待发货
                    Result<Boolean> updateOrderResult = orderFeignClient.updateStatusAndPayTime(outTradeNo, OrderStatus.WAIT_SHIP.getCode());
                    if(!updateOrderResult.isSuccess()) {
                        log.error("更新订单状态失败，商户订单号：{}", outTradeNo);
                        return "success";// 返回success告知支付宝已经收到通知，不要再重复发送
                    }

                    // 插入支付记录，同时保存商户订单号和支付宝交易号
                    paymentRecordService.insert(outTradeNo, tradeNo, paymentAmount, payTime);
                } else if ("TRADE_CLOSED".equals(tradeStatus)) {
                    // 处理支付失败/交易关闭的情况
                    log.info("支付宝异步通知：交易关闭，商户订单号：{}，支付宝交易号：{}", outTradeNo, tradeNo);

                    // 无需修改订单状态->已关闭,订单状态由RabbitMQ延迟消息处理

                    // 可以记录失败的支付信息
                    paymentRecordService.insertFailedPayment(outTradeNo, tradeNo, paymentAmount, payTime);
                }

                // 返回success告知支付宝已经收到通知，不要再重复发送
                return "success";
            } else {
                log.warn("支付宝异步通知：验签失败");
                // 检查是否真的有sign参数
                if (verifyResult.getAllParams() != null) {
                    String sign = verifyResult.getAllParams().get("sign");
                    log.error("请求中的sign参数: {}", sign != null ? "存在，长度=" + sign.length() : "不存在");

                    // 打印所有参数用于调试
                    verifyResult.getAllParams().forEach((key, value) -> {
                        // 避免打印敏感信息
                        if (!"sign".equals(key)) {
                            log.error("参数 {}: {}", key, value);
                        }
                    });
                }
                return "fail";
            }
        } catch (Exception e) {
            log.error("处理支付宝异步通知异常", e);
            return "fail";
        }
    }

    private boolean isPaymentRecordExist(String tradeNo) {
        String s = redisTemplate.opsForValue().get(PAY_NOTIFY_PREFIX + tradeNo);
        return s != null;
    }


}
