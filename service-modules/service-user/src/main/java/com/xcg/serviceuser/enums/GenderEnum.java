package com.xcg.serviceuser.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum GenderEnum {
    UNKNOWN(0, "未知"),
    MALE(1, "男"),
    FEMALE(2, "女");

    @EnumValue // 存储到数据库的值
    private final int code;

    @JsonValue // JSON序列化时使用的值
    private final String description;

    GenderEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    // 根据code获取枚举
    public static GenderEnum fromCode(Integer code) {
        if (code == null) {
            return UNKNOWN; // 或者返回null，根据业务需求
        }
        for (GenderEnum gender : GenderEnum.values()) {
            if (gender.code == code) {
                return gender;
            }
        }
        throw new IllegalArgumentException("无效的性别编码: " + code);
    }

    // 根据description获取枚举（可选）
    public static GenderEnum fromDescription(String description) {
        if (description == null) {
            return UNKNOWN;
        }
        for (GenderEnum gender : GenderEnum.values()) {
            if (gender.description.equals(description)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("无效的性别描述: " + description);
    }
}