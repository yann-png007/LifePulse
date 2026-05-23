package com.lifepulse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lifepulse.common.Result;
import com.lifepulse.dto.CouponCreateDTO;
import com.lifepulse.dto.CouponReceiveDTO;
import com.lifepulse.entity.Coupon;
import com.lifepulse.vo.CouponVO;

import java.util.List;

public interface CouponService extends IService<Coupon> {
    void createCoupon(CouponCreateDTO dto);
    void userReceiveCoupon(CouponReceiveDTO dto);
    /**
     * 秒杀下单（Redis+Lua防超卖，Redisson分布式锁一人一单）
     */
    Result<String> seckillCoupon(Long couponId);

    /**
     * 预加载秒杀库存到Redis
     */
    void preloadStockToRedis();

    List<CouponVO> getAvailableCouponList();
}