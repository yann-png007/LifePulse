package com.lifepulse.util;

import com.lifepulse.constant.CouponConstant;
import com.lifepulse.constant.MQConstant;
import com.lifepulse.mapper.CouponStockMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * RabbitMQ消息消费者
 * 异步处理业务逻辑
 */
@Slf4j
@Component
public class RabbitMQConsumer {

    private final CouponStockMapper couponStockMapper;
    private final RedisUtil redisUtil;
    private final StringRedisTemplate stringRedisTemplate;

    public RabbitMQConsumer(CouponStockMapper couponStockMapper, RedisUtil redisUtil, StringRedisTemplate stringRedisTemplate) {
        this.couponStockMapper = couponStockMapper;
        this.redisUtil = redisUtil;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 监听订单创建队列
     */
    @RabbitListener(queues = MQConstant.ORDER_CREATE_QUEUE)
    public void consumeOrderMsg(String orderId){
        log.info("异步处理订单: orderId={}", orderId);
    }

    /**
     * 监听用户行为日志队列
     */
    @RabbitListener(queues = MQConstant.BEHAVIOR_QUEUE)
    public void consumeBehaviorMsg(String logData){
        log.info("接收用户行为日志: logData={}", logData);
    }

    private static final String SECKILL_IDEMPOTENT_PREFIX = "seckill:idempotent:";

    // 监听秒杀队列
    @RabbitListener(queues = MQConstant.SECKILL_QUEUE)
    public void seckillConsumer(String message) {
        // 1. 消费幂等性保证
        String idempotentKey = SECKILL_IDEMPOTENT_PREFIX + message;
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(idempotentKey, "1", 24, TimeUnit.HOURS);
        if (Boolean.FALSE.equals(success)) {
            log.warn("重复消费秒杀消息，已丢弃: message={}", message);
            return;
        }

        try {
            String[] arr = message.split(":");
            Long userId = Long.parseLong(arr[0]);
            Long couponId = Long.parseLong(arr[1]);

            // 2. 数据库扣减库存
            int rows = couponStockMapper.deductCouponStock(couponId);
            if (rows > 0) {
                // 3. 标记用户已领取（用于前端校验）
                String userKey = CouponConstant.COUPON_USER_RECEIVE_KEY + couponId + ":" + userId;
                redisUtil.set(userKey, "1", 86400);
                log.info("秒杀消费成功: userId={}, couponId={}", userId, couponId);
                return;
            }
            log.warn("秒杀消费未扣减到库存(可能已被其他消费者处理或库存不足): userId={}, couponId={}, message={}", userId, couponId, message);
        } catch (Exception e) {
            log.error("秒杀消费异常: message={}", message, e);
            // 消费异常时，删除幂等key，以便后续重试可以成功
            stringRedisTemplate.delete(idempotentKey);
            // 手动抛出异常，以便RabbitMQ可以根据配置进行重试
            throw new RuntimeException("秒杀消费异常", e);
        }
    }
}