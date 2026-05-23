package com.lifepulse.controller.user;

import com.google.common.util.concurrent.RateLimiter;
import com.lifepulse.common.Result;
import com.lifepulse.common.ResultCode;
import com.lifepulse.constant.CacheConstant;
import com.lifepulse.constant.CouponConstant;
import com.lifepulse.dto.CouponReceiveDTO;
import com.lifepulse.service.CouponService;
import com.lifepulse.util.UserContext;
import com.lifepulse.util.RabbitMQProducer;
import com.lifepulse.vo.CouponVO;
import com.lifepulse.util.RedisUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/coupon")
public class CouponUserController {

    // 你的业务Service
    private final CouponService couponService;

    // 秒杀需要注入的工具
    private final RedisUtil redisUtil;

    private final RabbitMQProducer rabbitMQProducer;

    // Guava RateLimiter，每秒放行100个请求
    private static final RateLimiter rateLimiter = RateLimiter.create(100);

    // 构造器注入
    public CouponUserController(CouponService couponService, RedisUtil redisUtil, RabbitMQProducer rabbitMQProducer) {
        this.couponService = couponService;
        this.redisUtil = redisUtil;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    // ==========================================
    // 普通领取优惠券
    // ==========================================
    @PostMapping("/receive")
    public Result<Void> receive(@Validated @RequestBody CouponReceiveDTO dto) {
        couponService.userReceiveCoupon(dto);
        return Result.success();
    }

    // ==========================================
    // 高并发秒杀接口
    // ==========================================
    @PostMapping("/seckill/{couponId}")
    public Result<String> seckill(@PathVariable Long couponId) {
        // 1. 单机限流（顶层保护）
        if (!rateLimiter.tryAcquire()) {
            return Result.error(ResultCode.REQUEST_BUSY);
        }

        // 2. 调用 Service 层完成秒杀业务
        return couponService.seckillCoupon(couponId);
    }

    // ==========================================
    // 查询可领取优惠券列表
    // ==========================================
    @GetMapping("/available/list")
    public Result<List<CouponVO>> availableList() {
        return Result.success(couponService.getAvailableCouponList());
    }
}