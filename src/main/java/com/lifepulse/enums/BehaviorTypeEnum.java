package com.lifepulse.enums;

import lombok.Getter;

@Getter
public enum BehaviorTypeEnum {
    VIEW("VIEW", "浏览行为"),
    COLLECT("COLLECT", "收藏行为"),
    LIKE("LIKE", "点赞行为"),
    ORDER("ORDER", "下单行为");

    private final String code;
    private final String desc;

    BehaviorTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static BehaviorTypeEnum getByCode(String code) {
        for (BehaviorTypeEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }
}
