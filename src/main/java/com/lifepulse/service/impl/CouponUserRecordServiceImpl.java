package com.lifepulse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lifepulse.entity.CouponUserRecord;
import com.lifepulse.mapper.CouponUserRecordMapper;
import com.lifepulse.service.CouponUserRecordService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class CouponUserRecordServiceImpl extends ServiceImpl<CouponUserRecordMapper, CouponUserRecord>
        implements CouponUserRecordService {

    // ===================== 核心方法：保存领取记录 =====================
    @Override
    public void saveReceiveRecord(Long userId, Long couponId) {
        CouponUserRecord record = new CouponUserRecord();
        record.setUserId(userId);
        record.setCouponId(couponId);
        record.setStatus(0); // 0=未使用（完全匹配你的字段）
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        save(record);
    }
}
