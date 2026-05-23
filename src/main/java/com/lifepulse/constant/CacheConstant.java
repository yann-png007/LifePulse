package com.lifepulse.constant;

/**
 * 缓存全局常量
 * 统一管理Redis、本地缓存前缀与过期时间
 */
public class CacheConstant {
    // 缓存Key前缀
    public static final String USER_LOGIN_PREFIX = "lifepulse:user:login:";
    public static final String MERCHANT_INFO_PREFIX = "lifepulse:merchant:info:";
    public static final String CATEGORY_TREE_PREFIX = "lifepulse:category:tree";
    public static final String COUPON_STOCK_PREFIX = "lifepulse:coupon:stock:";
    public static final String USER_BEHAVIOR_PREFIX = "lifepulse:behavior:";
    // 用户行为防重缓存前缀
    public static final String BEHAVIOR_REPEAT_PREFIX = "lifepulse:behavior:repeat:";

    // 缓存过期时间 单位：秒
    public static final Long LOGIN_TOKEN_EXPIRE = 604800L;
    public static final Long COMMON_CACHE_EXPIRE = 3600L;
    public static final Long CATEGORY_CACHE_EXPIRE = 7200L;
    public static final Long EMPTY_DATA_EXPIRE = 60L;
    // 新增：用户行为防重缓存过期时间（比如30秒，防止重复提交）
    public static final Long BEHAVIOR_REPEAT_EXPIRE = 30L;
}