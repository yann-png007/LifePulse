package com.lifepulse.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
/**
 * 用户领取优惠券请求DTO
 */
@Data
public class CouponReceiveDTO {
    @NotNull(message = "优惠券ID不能为空")
    private Long couponId;
}