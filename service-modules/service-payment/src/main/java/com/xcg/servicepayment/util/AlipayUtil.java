package com.xcg.servicepayment.util;

import com.alipay.v3.ApiClient;
import com.alipay.v3.ApiException;
import com.alipay.v3.Configuration;
import com.alipay.v3.util.AlipaySignature;
import com.alipay.v3.util.GenericExecuteApi;
import com.alipay.v3.util.model.AlipayConfig;
import com.xcg.servicepayment.result.VerifyResult;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;



@Component
@Slf4j
public class AlipayUtil {

    @Value("${alipay.app_id}")
    private String appId;

    @Value("${alipay.private_key}")
    private String privateKey;

    @Value("${alipay.public_key}")
    private String publicKey;

    @Value("${alipay.gateway}")
    private String baseUrl;

    @Value("${alipay.notify_url:}")
    private String notifyUrl;

    @Value("${alipay.return_url:}")
    private String returnUrl;

    private ApiClient apiClient;

    @PostConstruct
    public void init() {
        try {
            apiClient = Configuration.getDefaultApiClient();
            AlipayConfig alipayConfig = new AlipayConfig();

            // 修正网关地址
            if (baseUrl.endsWith("/gateway.do/gateway.do")) {
                baseUrl = baseUrl.replace("/gateway.do/gateway.do", "/gateway.do");
            }
            if (!baseUrl.endsWith("/gateway.do")) {
                baseUrl = baseUrl + "/gateway.do";
            }

            alipayConfig.setServerUrl(baseUrl);
            alipayConfig.setAppId(appId);
            alipayConfig.setPrivateKey(privateKey);
            alipayConfig.setAlipayPublicKey(publicKey);
//            alipayConfig.setFormat("json");
//            alipayConfig.setCharset("UTF-8");
//            alipayConfig.setSignType("RSA2");

            apiClient.setAlipayConfig(alipayConfig);
            log.info("支付宝配置初始化成功, 网关: {}", baseUrl);
        } catch (Exception e) {
            log.error("初始化支付宝配置异常", e);
            throw new RuntimeException(e);
        }
    }

    public String pay(String orderNo, String totalAmount, String title, String timeoutExpress) {
        try {
            GenericExecuteApi api = new GenericExecuteApi();
            Map<String, Object> bizParams = new HashMap<>();

            Map<String, Object> bizContent = new HashMap<>();
            bizContent.put("out_trade_no", orderNo);
            bizContent.put("total_amount", totalAmount);
            bizContent.put("subject", title);
            bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");

            bizParams.put("biz_content", bizContent);

            // 添加 notify_url 和 return_url 参数
            if (StringUtils.hasText(notifyUrl)) {
                bizParams.put("notify_url", notifyUrl);
            }
            if (StringUtils.hasText(returnUrl)) {
                bizParams.put("return_url", returnUrl);
            }

            // 设置交易超时时间
//            if (StringUtils.hasText(timeoutExpress)) {
//                bizContent.put("timeout_express", timeoutExpress);
//            }


            // 打印请求参数用于调试
            log.info("请求参数: {}", bizParams);

            String result = api.pageExecute("alipay.trade.page.pay", "GET", bizParams);

            log.info("原始返回: {}", result);

            if (result != null && result.contains("/gateway.do/gateway.do")) {
                result = result.replace("/gateway.do/gateway.do", "/gateway.do");
                log.info("修复后的URL: {}", result);
            }

            return result;

        } catch (ApiException e) {
            log.error("支付宝API异常 - 错误码: {}", e.getCode());
            log.error("支付宝API异常 - 错误信息: {}", e.getMessage());
            log.error("支付宝API异常 - 响应头: {}", e.getResponseHeaders());
            log.error("支付宝API异常 - 响应体: {}", e.getResponseBody());
            return null;
        }
    }

    public VerifyResult verify(HttpServletRequest request) throws ApiException {
        // 将异步通知中收到的所有参数都存放到map中
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();

        log.info("支付宝回调请求中的所有参数:");
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            // 为调试目的，打印每个参数
            log.info("支付宝回调参数: {} = {}", name, valueStr);
            params.put(name, valueStr);
        }

        try {
            // 特别检查sign参数
            if (!params.containsKey("sign")) {
                log.error("支付宝回调请求中缺少sign参数!");
            } else {
                log.info("sign参数存在，值长度为: {}", params.get("sign").length());
                log.debug("sign参数值: {}", params.get("sign"));
            }

            // 检查是否有sign_type参数
            if (!params.containsKey("sign_type")) {
                log.warn("支付宝回调请求中缺少sign_type参数!");
            } else {
                log.info("sign_type参数存在，值为: {}", params.get("sign_type"));
            }

            // 验证公钥
            validatePublicKey(publicKey);

            // 记录用于验签的公钥（隐藏敏感信息）
            log.debug("支付宝公钥长度: {}", publicKey != null ? publicKey.length() : 0);
            log.debug("支付宝公钥是否为空: {}", StringUtils.isEmpty(publicKey));
            if (publicKey != null) {
                String cleanKey = publicKey.replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "")
                        .replaceAll("\\s", "");
                log.debug("清理后的支付宝公钥长度: {}", cleanKey.length());
            }

            // 创建参数副本，避免verifyV2方法修改原始参数
            Map<String, String> paramsForVerify = new HashMap<>(params);

            // 打印用于验证的参数
            log.debug("用于签名验证的参数:");
            paramsForVerify.forEach((key, value) -> {
                if ("sign".equals(key)) {
                    log.debug("  {}: [长度为{}的签名]", key, value.length());
                } else {
                    log.debug("  {}: {}", key, value);
                }
            });

            // 使用参数副本进行签名验证
            boolean signVerified = AlipaySignature.verifyV1(paramsForVerify, publicKey, "UTF-8", "RSA2");
            log.info("支付宝签名验证结果: {}", signVerified ? "成功" : "失败");

            if (!signVerified) {
                // 打印更多调试信息帮助排查问题
                log.warn("签名验证失败的详细信息:");
                log.warn("  - out_trade_no: {}", params.get("out_trade_no"));
                log.warn("  - trade_no: {}", params.get("trade_no"));
                log.warn("  - trade_status: {}", params.get("trade_status"));
                log.warn("  - sign长度: {}", params.get("sign") != null ? params.get("sign").length() : "null");
                log.warn("  - sign_type: {}", params.get("sign_type"));

                // 检查参数是否在验证过程中被修改
                log.warn("验证后参数是否还包含sign: {}", paramsForVerify.containsKey("sign"));
                if (paramsForVerify.containsKey("sign")) {
                    log.warn("验证后sign参数长度: {}", paramsForVerify.get("sign").length());
                }
            }

            return VerifyResult.builder()
                    .signVerified(signVerified)
                    .outTradeNo(params.get("out_trade_no"))
                    .tradeNo(params.get("trade_no"))
                    .totalAmount(params.get("total_amount"))
                    .tradeStatus(params.get("trade_status"))
                    .allParams(new HashMap<>(params)) // 添加所有参数以便调试，创建副本防止外部修改
                    .build();
        } catch (Exception e) {
            log.error("支付宝签名验证过程中发生异常", e);
            return VerifyResult.builder()
                    .signVerified(false)
                    .outTradeNo(params.get("out_trade_no"))
                    .tradeNo(params.get("trade_no"))
                    .totalAmount(params.get("total_amount"))
                    .tradeStatus(params.get("trade_status"))
                    .allParams(new HashMap<>(params))
                    .errorMessage(e.getMessage())
                    .build();
        }
    }


    private void validatePublicKey(String publicKey) {
        if (publicKey == null || publicKey.isEmpty()) {
            log.error("支付宝公钥为空");
            return;
        }

        // 移除PEM标头和尾部（如果存在）
        String cleanKey = publicKey.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        log.info("清理后的公钥长度: {}", cleanKey.length());
        log.debug("清理后的公钥前缀: {}", cleanKey.substring(0, Math.min(50, cleanKey.length())));

        // 检查是否包含有效的Base64字符
        if (!cleanKey.matches("^[A-Za-z0-9+/=]+$")) {
            log.error("支付宝公钥包含无效的Base64字符");
            return;
        }

        log.info("支付宝公钥格式验证通过");
    }


}
