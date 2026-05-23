package com.lifepulse.enums;

import lombok.Getter;

@Getter
public enum CouponStatusEnum {
    DRAFT(0,"草稿"),
    PUBLISH(1,"已发布"),
    EXPIRED(2,"已过期"),
    DISABLE(3,"已停用");

    private final Integer status;
    private final String desc;

    CouponStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }
}