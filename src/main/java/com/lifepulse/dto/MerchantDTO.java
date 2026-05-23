package com.lifepulse.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 商户信息创建/编辑请求DTO
 */
@Data
public class MerchantDTO {

    @NotBlank(message = "商户名称不能为空")
    private String merchantName;

    @NotNull(message = "商户分类ID不能为空")
    private Long categoryId;

    private String address;

    private String phone;

    /** 商户评分，前端可不传，后端自动维护 */
    private BigDecimal score;

    /** 营业状态：0-休息 1-营业 */
    private Integer status;
}