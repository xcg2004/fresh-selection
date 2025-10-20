package com.xcg.freshcommon.core.constants;

public class RedisConstants {
    public static final String TOKEN_PREFIX = "auth:token:user:";
    public static final long TOKEN_EXPIRE_TIME = 60;

    public static final String PRODUCT_INFO_PREFIX = "product:info:";
    public static final long PRODUCT_INFO_EXPIRE_TIME = 3600L;// 1小时
    public static final long PRODUCT_INFO_NULL_EXPIRE_TIME = 300L; // 5分钟，防止缓存穿透

    public static final String CATEGORY_TREE_KEY = "category:tree";

    public static final String USER_INFO_PREFIX = "user:info:";

    public static final String ATTRIBUTE_NAME_KEY = "attribute:name:";
    public static final String ATTRIBUTE_VALUE_NAME_KEY = "attribute:value:";
    public static final long ATTRIBUTE_NAME_EXPIRE_TIME = 1800L; // 30分钟
    public static final long ATTRIBUTE_VALUE_NAME_EXPIRE_TIME = 1800L; // 30分钟

}
