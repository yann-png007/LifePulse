package com.lifepulse.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 通用分页查询请求DTO
 */
@Data
public class PageDTO {

    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码必须大于等于1")
    private Integer pageNum;

    @NotNull(message = "页大小不能为空")
    @Min(value = 1, message = "页大小必须大于等于1")
    private Integer pageSize;

    /** 通用搜索关键字，可选 */
    private String keyword;
}