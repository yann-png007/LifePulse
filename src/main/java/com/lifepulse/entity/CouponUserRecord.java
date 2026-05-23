package com.lifepulse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 优惠券用户领取记录表
 * 完全兼容你的数据库表：lp_coupon_user
 */
@Data
@TableName("lp_coupon_user")
public class CouponUserRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;       // 用户ID
    private Long couponId;    // 优惠券ID
    private Integer status;   // 状态 0=未使用 1=已使用 2=已过期

    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime; // 更新时间
}