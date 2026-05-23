package com.lifepulse.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("lp_coupon_stock")
public class CouponStock {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long couponId;
    private Integer stock;

    @Version
    private Integer version;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
