package com.xcg.freshcommon.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum PaymentType {
    WECHAT_PAY(1, "微信支付"),
    ALI_PAY(2, "支付宝");

    @EnumValue
    private final int code;

    @JsonValue
    private final String description;

    PaymentType(int code, String description) {
        this.code = code;
        this.description = description;
    }
    public static PaymentType fromCode(Integer code) {
        for (PaymentType value : PaymentType.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
