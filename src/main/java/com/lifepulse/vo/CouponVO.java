package com.lifepulse.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponVO {
    private Long id;
    private String couponName;
    private Integer couponType;
    private String typeDesc;
    private Integer status;
    private String statusDesc;
    private BigDecimal amount;
    private BigDecimal minCost;
    private Integer surplusStock;
    private Integer limitPerUser;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}