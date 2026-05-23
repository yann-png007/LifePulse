package com.lifepulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lifepulse.common.Result;
import com.lifepulse.common.ResultCode;
import com.lifepulse.constant.CouponConstant;
import com.lifepulse.dto.CouponCreateDTO;
import com.lifepulse.dto.CouponReceiveDTO;
import com.lifepulse.entity.Coupon;
import com.lifepulse.entity.CouponUserRecord;
import com.lifepulse.enums.CouponStatusEnum;
import com.lifepulse.enums.CouponTypeEnum;
import com.lifepulse.exception.BizException;
import com.lifepulse.mapper.CouponMapper;
import com.lifepulse.mapper.CouponStockMapper;
import com.lifepulse.service.CouponService;
import com.lifepulse.service.CouponUserRecordService;
import com.lifepulse.util.RabbitMQProducer;
import com.lifepulse.util.RedisUtil;
import com.lifepulse.util.UserContext;
import com.lifepulse.vo.CouponVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {

    private final CouponStockMapper couponStockMapper;
    private final CouponUserRecordService userRecordService;
    private final RedisUtil redisUtil;
    private final RabbitMQProducer rabbitMQProducer;


    public CouponServiceImpl(CouponStockMapper couponStockMapper, CouponUserRecordService userRecordService, RedisUtil redisUtil, RabbitMQProducer rabbitMQProducer) {
        this.couponStockMapper = couponStockMapper;
        this.userRecordService = userRecordService;
        this.redisUtil = redisUtil;
        this.rabbitMQProducer = rabbitMQProducer;
    }


    // ==================== 管理员：创建优惠券 ====================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCoupon(CouponCreateDTO dto) {
        Coupon coupon = new Coupon();
        coupon.setCouponName(dto.getCouponName());
        coupon.setCouponType(dto.getCouponType());
        coupon.setStatus(CouponStatusEnum.PUBLISH.getStatus());
        coupon.setAmount(dto.getAmount());
        coupon.setMinCost(dto.getMinCost());
        coupon.setTotalStock(dto.getTotalStock());
        coupon.setUsedStock(0);
        coupon.setLimitPerUser(dto.getLimitPerUser());
        coupon.setStartTime(dto.getStartTime());
        coupon.setEndTime(dto.getEndTime());
        save(coupon);
        log.info("【优惠券创建】成功，券ID：{}", coupon.getId());
    }

    // ==================== 管理员：预热库存到Redis ====================
    @Override
    public void preloadStockToRedis() {
        List<Coupon> couponList = list(new LambdaQueryWrapper<Coupon>()
                .eq(Coupon::getStatus, CouponStatusEnum.PUBLISH.getStatus())
                .le(Coupon::getStartTime, LocalDateTime.now())
                .ge(Coupon::getEndTime, LocalDateTime.now())
        );

        for (Coupon coupon : couponList) {
            String stockKey = CouponConstant.COUPON_STOCK_PRE_KEY + coupon.getId();
            redisUtil.set(stockKey, coupon.getTotalStock(), 24 * 3600);
            log.info("【Redis库存预热】券ID：{}，库存：{}", coupon.getId(), coupon.getTotalStock());
        }
    }

    // ==================== 用户：普通领取 ====================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userReceiveCoupon(CouponReceiveDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BizException(ResultCode.NOT_LOGIN);
        }

        Long couponId = dto.getCouponId();
        String userKey = CouponConstant.COUPON_USER_RECEIVE_KEY + userId + ":" + couponId;

        // 1. 防重复领取
        if (redisUtil.hasKey(userKey)) {
            throw new BizException(ResultCode.REPEAT_RECEIVE);
        }

        // 2. 校验优惠券
        Coupon coupon = getById(couponId);
        if (coupon == null) {
            throw new BizException(ResultCode.COUPON_NOT_EXIST);
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartTime())) {
            throw new BizException(ResultCode.COUPON_NOT_START);
        }
        if (now.isAfter(coupon.getEndTime())) {
            throw new BizException(ResultCode.COUPON_EXPIRED);
        }

        // 3. 单人限领校验
        long count = userRecordService.count(new LambdaQueryWrapper<CouponUserRecord>()
                .eq(CouponUserRecord::getUserId, userId)
                .eq(CouponUserRecord::getCouponId, couponId));

        if (count >= coupon.getLimitPerUser()) {
            throw new BizException(ResultCode.USER_LIMIT_OVER);
        }

        // 4. 原子扣减库存（防超卖）
        int rows = couponStockMapper.deductCouponStock(couponId);
        if (rows <= 0) {
            throw new BizException(ResultCode.STOCK_NOT_ENOUGH);
        }

        // 5. 写入领取记录
        userRecordService.saveReceiveRecord(userId, couponId);

        // 6. Redis标记已领取
        redisUtil.set(userKey, 1, CouponConstant.COUPON_RECEIVE_EXPIRE);

        log.info("【普通领取成功】用户：{}，券ID：{}", userId, couponId);
    }

    @Override
    public Result<String> seckillCoupon(Long couponId) {
        Long userId = UserContext.getUserId();

        // 2. 库存判断（Redis）
        String stockKey = CouponConstant.COUPON_STOCK_PRE_KEY + couponId;
        Object stockObj = redisUtil.get(stockKey);
        if (stockObj == null || (Integer)stockObj <= 0) {
            return Result.error(ResultCode.SECKILL_FINISH);
        }

        // 3. 防止重复抢
        String userKey = CouponConstant.COUPON_USER_RECEIVE_KEY + couponId + ":" + userId;
        if (redisUtil.hasKey(userKey)) {
            return Result.error(ResultCode.SECKILL_REPEAT);
        }

        // 4. Redis 预扣库存
        Long afterDecrement = redisUtil.decrement(stockKey, 1);
        if (afterDecrement != null && afterDecrement < 0) {
            redisUtil.increment(stockKey, 1); // 扣多了，加回去
            return Result.error(ResultCode.SECKILL_FINISH);
        }

        // 5. 发送消息到 RabbitMQ（高并发异步）
        rabbitMQProducer.sendSeckillMessage(couponId, userId);

        return Result.success("抢券中，请稍后查看结果！");
    }

    // ==================== 查询可领取优惠券列表 ====================
    @Override
    public List<CouponVO> getAvailableCouponList() {
        LocalDateTime now = LocalDateTime.now();
        List<Coupon> list = list(new LambdaQueryWrapper<Coupon>()
                .eq(Coupon::getStatus, CouponStatusEnum.PUBLISH.getStatus())
                .le(Coupon::getStartTime, now)
                .ge(Coupon::getEndTime, now)
        );

        return list.stream().map(c -> {
            CouponVO vo = new CouponVO();
            vo.setId(c.getId());
            vo.setCouponName(c.getCouponName());
            vo.setCouponType(c.getCouponType());
            // 防止NPE
            CouponTypeEnum typeEnum = CouponTypeEnum.getByType(c.getCouponType());
            if (typeEnum != null) {
                vo.setTypeDesc(typeEnum.getDesc());
            }
            vo.setStatus(c.getStatus());
            vo.setStatusDesc(CouponStatusEnum.PUBLISH.getDesc());
            vo.setAmount(c.getAmount());
            vo.setMinCost(c.getMinCost());
            vo.setSurplusStock(c.getTotalStock());
            vo.setLimitPerUser(c.getLimitPerUser());
            vo.setStartTime(c.getStartTime());
            vo.setEndTime(c.getEndTime());
            return vo;
        }).collect(Collectors.toList());
    }
}