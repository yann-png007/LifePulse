package com.lifepulse.util;

import com.lifepulse.constant.MQConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 发送日志消息
     *
     * @param message 消息内容
     */
    public void sendLogMsg(String message) {
        rabbitTemplate.convertAndSend(
                MQConstant.LOG_EXCHANGE,
                MQConstant.LOG_ROUTING_KEY,
                message
        );
    }
    /**
     * 秒杀生产者
     * 高并发 → 消息入队 → 异步削峰
     */

    /**
     * 发送秒杀请求
     *
     * @param couponId 优惠券ID
     * @param userId   用户ID
     */
    public void sendSeckillMessage(Long couponId, Long userId) {
        String message = userId + ":" + couponId;
        rabbitTemplate.convertAndSend(
                MQConstant.SECKILL_EXCHANGE,
                MQConstant.SECKILL_ROUTING_KEY,
                message
        );
    }
}