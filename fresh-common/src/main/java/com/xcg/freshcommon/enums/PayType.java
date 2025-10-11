package com.xcg.freshcommon.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum PayType {
    WECHAT(1, "微信"),
    ALIPAY(2, "支付宝");

    @EnumValue // 存储到数据库的值
    private final int code;

    @JsonValue // JSON序列化时返回的值
    private final String description;

    PayType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    // 根据code获取枚举
    public static PayType fromCode(Integer code) {
        if (code == null) {
            return WECHAT; // 默认微信支付
        }
        for (PayType payType : values()) {
            if (payType.code == code) {
                return payType;
            }
        }
        throw new IllegalArgumentException("无效的支付类型编码: " + code);
    }
}