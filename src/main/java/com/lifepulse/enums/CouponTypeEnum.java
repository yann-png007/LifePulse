package com.lifepulse.enums;

import lombok.Getter;

@Getter
public enum CouponTypeEnum {
    FULL_REDUCE(1,"满减券"),
    DISCOUNT(2,"折扣券"),
    FREE_SHIPPING(3,"免邮券");

    private final Integer type;
    private final String desc;

    CouponTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static CouponTypeEnum getByType(Integer type){
        for (CouponTypeEnum e : values()) {
            if(e.getType().equals(type)){
                return e;
            }
        }
        return null;
    }
}
