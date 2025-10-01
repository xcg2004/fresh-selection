package com.xcg.freshcommon.core.handler;

import com.xcg.freshcommon.core.exception.BizException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j

public class GlobalExceptionHandler {


    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BizException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(BizException e) {
        log.error("业务异常: ", e);
        Map<String, Object> result = new HashMap<>();
        result.put("code", e.getCode());
        result.put("message", e.getMessage());
        result.put("success", false);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("参数异常: ", e);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 400);
        result.put("message", "参数错误: " + e.getMessage());
        result.put("success", false);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, Object>> handleNullPointerException(NullPointerException e) {
        log.error("空指针异常: ", e);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", "服务器内部错误");
        result.put("success", false);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 处理其他未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        log.error("系统异常: ", e);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", "系统繁忙，请稍后再试");
        result.put("success", false);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
}
