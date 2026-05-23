package com.lifepulse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lifepulse.entity.CouponUserRecord;

public interface CouponUserRecordService extends IService<CouponUserRecord> {

    /**
     * 保存用户领取记录
     */
    void saveReceiveRecord(Long userId, Long couponId);
}