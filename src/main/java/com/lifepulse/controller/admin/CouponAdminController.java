package com.lifepulse.controller.admin;

import com.lifepulse.common.Result;
import com.lifepulse.dto.CouponCreateDTO;
import com.lifepulse.service.CouponService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/coupon")
public class CouponAdminController {

    private final CouponService couponService;

    public CouponAdminController(CouponService couponService) {
        this.couponService = couponService;
    }

    // 后台创建优惠券
    @PostMapping("/create")
    public Result<Void> createCoupon(@Validated @RequestBody CouponCreateDTO dto){
        couponService.createCoupon(dto);
        return Result.success();
    }
    // 优惠券库存预热到Redis
    @PostMapping("/stock/preload")
    public Result<Void> preloadStock(){
        couponService.preloadStockToRedis();
        return Result.success();
    }
}