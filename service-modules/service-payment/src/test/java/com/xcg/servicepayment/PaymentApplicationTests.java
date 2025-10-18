package com.xcg.servicepayment;

import com.alipay.v3.util.AlipaySignature;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@Slf4j
class PaymentApplicationTests {

    @Value("${alipay.public_key}")
    private String publicKey;

    @Test
    void contextLoads() {
    }

    @Test
    public void testSignature() {
        try {
            log.info("支付宝公钥: {}", publicKey);
            if (publicKey == null || publicKey.isEmpty()) {
                log.error("支付宝公钥未正确加载");
                return;
            }

            Map<String, String> testParams = new HashMap<>();
            testParams.put("gmt_create", "2025-10-18 15:35:38");
            testParams.put("charset", "UTF-8");
            testParams.put("gmt_payment", "2025-10-18 15:35:53");
            testParams.put("notify_time", "2025-10-18 15:35:54");
            testParams.put("subject", "冰淇淋");
            testParams.put("sign", "ipcMAA/j/MYh9vfKvsMyACFfhOduOW3ZozSWb1946s6lELhBnjWx/XoiSNKRVArKQPLqlu6FSWOEh8CIb8j2qU81rXKOZMcxFACBGRrxRg5Fx8ocXqqJVwN14dE9c02tDnnOQWxWq+LLE67e6FdZkcxmVPcFzwFToNr5J0jKJ0OFVw8c8kdn59Fc7Bj36X376uJMFwawv4L2GjgdhyjSvLoYFjRxLc79AC9qf3CF+GvlfFV5A/YIa4UDgWkPeUmOfjH3uqhjosaAQZhGsaM10uUy3kgdfwwy15509q0wgySnGFUxZ5UWDepc3KuDlXM1lCenDoqPEYPBqthrJ3ZMOg==");
            testParams.put("buyer_id", "2088722084489581");
            testParams.put("invoice_amount", "4070.23");
            testParams.put("version", "1.0");
            testParams.put("notify_id", "2025101801222153554089580507088447");
            testParams.put("fund_bill_list", "[{\"amount\":\"4070.23\",\"fundChannel\":\"ALIPAYACCOUNT\"}]");
            testParams.put("notify_type", "trade_status_sync");
            testParams.put("out_trade_no", "1020251018000009");
            testParams.put("total_amount", "4070.23");
            testParams.put("trade_status", "TRADE_SUCCESS");
            testParams.put("trade_no", "2025101822001489580507108271");
            testParams.put("auth_app_id", "9021000156668228");
            testParams.put("receipt_amount", "4070.23");
            testParams.put("point_amount", "0.00");
            testParams.put("buyer_pay_amount", "4070.23");
            testParams.put("app_id", "9021000156668228");
            testParams.put("sign_type", "RSA2");
            testParams.put("seller_id", "2088721084511179");

            boolean result = AlipaySignature.verifyV1(testParams, publicKey, "UTF-8", "RSA2");
            log.info("手动测试签名验证结果: {}", result ? "成功" : "失败");
        } catch (Exception e) {
            log.error("手动测试签名验证异常", e);
        }
    }
}
