package com.lifepulse.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("lp_coupon")
public class Coupon {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String couponName;
    private Integer couponType;
    private Integer status;

    private BigDecimal amount;
    private BigDecimal minCost;

    private Integer totalStock; // 剩余可领库存（原子扣减用）
    private Integer usedStock;
    private Integer limitPerUser;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}