package com.lifepulse.common;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(200,"操作成功"),
    FAIL(500,"系统异常"),
    NOT_LOGIN(401,"未登录，请先登录"),
    NO_PERMISSION(403, "无权限访问"),
    COUPON_NOT_EXIST(10001,"优惠券不存在"),
    COUPON_EXPIRED(10002,"优惠券已过期"),
    COUPON_NOT_START(10003,"优惠券未开始领取"),
    STOCK_NOT_ENOUGH(10004,"优惠券库存不足"),
    USER_LIMIT_OVER(10005,"已达到单人领取上限"),
    REPEAT_RECEIVE(10006,"请勿重复领取"),

    // 秒杀场景
    SECKILL_FINISH(10101, "已抢完"),
    SECKILL_REPEAT(10102, "已领取过"),
    COUPON_INVALID(10103, "优惠券无效"),

    // 商户场景
    MERCHANT_NOT_EXIST(10201, "商户不存在"),

    // 通用场景
    REQUEST_BUSY(503, "请求繁忙，请稍后再试"),
    USERNAME_OR_PASSWORD_ERROR(1004, "用户名或密码错误"),
    PASSWORD_ERROR(1005, "密码错误"),
    OLD_PASSWORD_ERROR(1006, "旧密码错误"),
    USER_NOT_EXIST(1007, "用户不存在"),
    USER_ACCOUNT_DISABLE(1008, "账号已被禁用"),
    VERIFY_CODE_ERROR(1009, "验证码错误");

    private final Integer code;
    private final String msg;

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}