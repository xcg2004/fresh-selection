package com.xcg.freshcommon.core.utils;

import com.xcg.freshcommon.core.exception.BizException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;

@Component
@RequiredArgsConstructor
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class UserHolder {
    private final HttpServletRequest request;

    public Long getUserId(){
        String userId = request.getHeader("X-User-ID");
        if (userId == null || userId.isEmpty()){
            throw new BizException("用户未登录");
        }
        return Long.valueOf(userId);
    }
}
