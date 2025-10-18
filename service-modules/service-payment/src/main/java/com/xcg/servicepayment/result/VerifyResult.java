package com.xcg.servicepayment.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyResult implements Serializable {

    /**
     * 签名验证结果
     */
    private Boolean signVerified;

    /**
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 支付宝交易号
     */
    private String tradeNo;

    /**
     * 交易金额
     */
    private String totalAmount;

    /**
     * 交易状态
     */
    private String tradeStatus;

    /**
     * 所有回调参数
     */
    private Map<String, String> allParams;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 获取签名参数
     * @return 签名
     */
    public String getSign() {
        if (allParams != null) {
            return allParams.get("sign");
        }
        return null;
    }

    /**
     * 获取签名类型参数
     * @return 签名类型
     */
    public String getSignType() {
        if (allParams != null) {
            return allParams.get("sign_type");
        }
        return null;
    }
}
