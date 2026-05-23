package com.lifepulse.controller.user;

import com.lifepulse.common.PageResult;
import com.lifepulse.common.Result;
import com.lifepulse.dto.PageDTO;
import com.lifepulse.entity.Merchant;
import com.lifepulse.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/merchant")
public class MerchantUserController {

    private final MerchantService merchantService;

    public MerchantUserController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @Operation(summary = "分页查询商户列表", description = "支持分页、关键词模糊搜索商户名称")
    @GetMapping("/list")
    public Result<PageResult<Merchant>> listMerchant(
            @Valid PageDTO dto,
            @RequestParam(required = false, defaultValue = "") String keyword) {
        return merchantService.listMerchant(dto, keyword);
    }

    /**
     * 根据ID查询商户详情（用户端）
     */
    @Operation(summary = "根据ID查询商户详情", description = "根据商户ID查询单条商户信息")
    @GetMapping("/{id}")
    public Result<Merchant> getMerchantById(@PathVariable Long id) {
        return merchantService.getMerchantById(id);
    }

}