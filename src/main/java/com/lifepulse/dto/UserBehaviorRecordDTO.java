package com.lifepulse.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class UserBehaviorRecordDTO {

    /**
     * 目标业务ID
     */
    @NotNull(message = "目标业务ID不能为空")
    private Long targetId;

    /**
     * 行为类型编码
     */
    @NotBlank(message = "行为类型不能为空")
    private String behaviorType;

    /** 业务类型：如商户/优惠券/订单，可选 */
    private String bizType;
}