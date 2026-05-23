package com.lifepulse.controller.admin;

import com.lifepulse.common.Result;
import com.lifepulse.dto.MerchantDTO;
import com.lifepulse.entity.Merchant;
import com.lifepulse.service.MerchantService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/merchant")
public class MerchantAdminController {

    private final MerchantService merchantService;

    public MerchantAdminController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    /**
     * 新增商户
     */
    @PostMapping("/add")
    public Result<Void> addMerchant(@RequestBody MerchantDTO dto) {
        // 把 DTO 转成 Entity
        Merchant merchant = new Merchant();
        merchant.setName(dto.getMerchantName());
        merchant.setId(dto.getCategoryId());
        merchant.setAddress(dto.getAddress());
        merchant.setPhone(dto.getPhone());
        // ...其他字段复制

        // 调用 Service
        merchantService.addMerchant(merchant);
        return Result.success();
    }

    /**
     * 编辑商户
     */
    @PutMapping("/update")
    public Result<Void> updateMerchant(@RequestBody Merchant merchant) {
        merchantService.updateMerchant(merchant);
        return Result.success();
    }

    /**
     * 删除商户
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteMerchant(@PathVariable Long id) {
        merchantService.removeMerchant(id);
        return Result.success();
    }

}