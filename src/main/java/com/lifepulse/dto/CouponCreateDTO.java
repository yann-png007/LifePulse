package com.lifepulse.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponCreateDTO {
    @NotBlank(message = "优惠券名称不能为空")
    private String couponName;

    @NotNull(message = "优惠券类型不能为空")
    @Min(value = 1, message = "优惠券类型不合法")
    @Max(value = 3, message = "优惠券类型不合法")
    private Integer couponType;

    @NotNull(message = "优惠金额不能为空")
    @DecimalMin(value = "0.01",message = "优惠金额必须大于0")
    private BigDecimal amount;

    private BigDecimal minCost;

    @NotNull(message = "发放总库存不能为空")
    @Min(value = 1,message = "库存至少为1")
    private Integer totalStock;

    @NotNull(message = "单人限领数量不能为空")
    @Min(value = 1,message = "限领数量至少为1")
    private Integer limitPerUser;

    @NotNull(message = "领取开始时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @NotNull(message = "领取结束时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}