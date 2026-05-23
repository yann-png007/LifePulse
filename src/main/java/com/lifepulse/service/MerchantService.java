package com.lifepulse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lifepulse.common.PageResult;
import com.lifepulse.common.Result;
import com.lifepulse.dto.PageDTO;
import com.lifepulse.entity.Merchant;

public interface MerchantService extends IService<Merchant> {

    // 管理员
    void addMerchant(Merchant merchant);
    void updateMerchant(Merchant merchant);
    void removeMerchant(Long id);

    // 用户
    Result<Merchant> getMerchantById(Long id);
    Result<PageResult<Merchant>> listMerchant(PageDTO dto, String keyword);

}