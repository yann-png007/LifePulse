package com.lifepulse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("lp_user_behavior")
public class UserBehavior {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;        // 用户ID
    private Long targetId;      // 目标ID（商户/优惠券ID）
    private String type;        // 行为类型：view浏览、collect收藏、coupon领券
    private LocalDateTime createTime;
}
