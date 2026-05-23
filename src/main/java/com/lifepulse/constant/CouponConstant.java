package com.lifepulse.constant;

public class CouponConstant {
    // 用户领取缓存前缀
    public static final String COUPON_USER_RECEIVE_KEY = "coupon:user:receive:";
    // 库存预扣缓存
    public static final String COUPON_STOCK_PRE_KEY = "coupon:stock:pre:";
    // 领取限制过期时间 24小时
    public static final long COUPON_RECEIVE_EXPIRE = 86400L;
}
