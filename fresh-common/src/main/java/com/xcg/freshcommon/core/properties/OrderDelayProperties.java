package com.xcg.freshcommon.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "order.delay")
public class OrderDelayProperties {
    /**
     * 订单自动取消时间（毫秒），默认30分钟
     */
    private int cancelTime = 30 * 60 * 1000;

    public int getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(int cancelTime) {
        this.cancelTime = cancelTime;
    }
}
