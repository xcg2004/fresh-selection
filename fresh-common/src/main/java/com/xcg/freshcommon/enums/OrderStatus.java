package com.xcg.freshcommon.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {
    WAIT_PAY(0, "待付款"),
    WAIT_SHIP(1, "待发货"),
    HAD_SHIP(2, "已发货"),
    HAD_CONFIRM(3, "已完成"),
    CANCEL(4, "已关闭");

    @EnumValue
    private final Integer code;

    @JsonValue
    private final String message;

    OrderStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    public Integer getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
    public static OrderStatus getByCode(Integer code) {
        if(code == null) {
            return WAIT_PAY;
        }

        for (OrderStatus value : OrderStatus.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("无效的订单状态码: " + code);
    }
}
