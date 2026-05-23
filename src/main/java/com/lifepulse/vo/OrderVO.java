package com.lifepulse.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单视图对象（View Object）
 * 用于向前端展示订单信息
 */
@Data
public class OrderVO {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 优惠券标题
     */
    private String couponTitle;

    /**
     * 订单状态（0:待支付, 1:已完成, 2:已取消, 3:已退款, 4:支付失败）
     */
    private Integer status;

    /**
     * 订单状态文字描述
     */
    private String statusText;

    /**
     * 订单金额
     */
    private BigDecimal amount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}