package com.lifepulse.dto;

import jakarta.validation.constraints.Min;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 用户下单请求DTO
 */
@Data
public class OrderDTO {

    @NotNull(message = "请求唯一ID不能为空")
    private String requestId;

    @NotNull(message = "商户ID不能为空")
    private Long merchantId;

    /** 优惠券ID，可选 */
    private Long couponId;

    @NotEmpty(message = "订单商品列表不能为空")
    private List<OrderItemDTO> items;

    private String remark;

    @Data
    public static class OrderItemDTO {
        @NotNull(message = "商品ID不能为空")
        private Long goodsId;

        @NotNull(message = "购买数量不能为空")
        @Min(value = 1, message = "购买数量至少为1")
        private Integer count;
    }
}