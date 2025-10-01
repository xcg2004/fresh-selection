package com.xcg.freshcommon.core.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class BizException extends RuntimeException {
    private int code;
    private Object data;
    private Throwable throwable;

    public BizException(int code, String message) {
        super(message); // 调用父类构造函数设置message
        this.code = code;
    }

    public BizException(int code, String message, Object data) {
        super(message);
        this.code = code;
        this.data = data;
    }

    public BizException(int code, String message, Throwable throwable) {
        super(message, throwable); // 调用父类构造函数
        this.code = code;
        this.throwable = throwable;
    }

    public BizException(int code, String message, Object data, Throwable throwable) {
        super(message, throwable);
        this.code = code;
        this.data = data;
        this.throwable = throwable;
    }

    // 注意：这里不应该有单独的message参数构造函数，因为会遮蔽父类的message
    // public BizException(String message) {
    //     super(message);
    // }

    public BizException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public BizException(Throwable throwable) {
        super(throwable);
    }

    // 移除getMessage()方法，让父类的方法生效
    // 移除getCode()方法，使用你自己定义的getCode()
}
