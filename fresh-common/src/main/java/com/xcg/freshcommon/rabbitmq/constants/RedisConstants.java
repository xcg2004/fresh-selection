package com.xcg.freshcommon.rabbitmq.constants;

public class RedisConstants {
    // Redis缓存中的token前缀
    public static final String TOKEN_PREFIX = "auth:token:user:";
    //60分钟
    public static final long TOKEN_EXPIRE_TIME = 60;
}
