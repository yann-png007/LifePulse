package com.lifepulse.constant;

/**
 * RabbitMQ消息队列全局常量
 * 统一管理交换机、队列、路由键
 * 对应30天计划：异步下单、超时关单、行为日志
 */

public class MQConstant {
    // 日志交换机
    public static final String LOG_EXCHANGE = "life_pulse_log_exchange";
    // 日志队列
    public static final String LOG_QUEUE = "life_pulse_log_queue";
    // 日志路由键
    public static final String LOG_ROUTING_KEY = "life.pulse.log";
    // 订单基础消息队列
    public static final String ORDER_TOPIC_EXCHANGE = "lifepulse.order.topic.exchange";
    public static final String ORDER_CREATE_QUEUE = "lifepulse.order.create.queue";
    public static final String ORDER_CREATE_ROUTING_KEY = "order.create";

    // 订单超时死信队列
    public static final String ORDER_DEAD_EXCHANGE = "lifepulse.order.dead.exchange";
    public static final String ORDER_DEAD_QUEUE = "lifepulse.order.dead.queue";
    public static final String ORDER_DEAD_ROUTING_KEY = "order.dead";

    // 用户行为埋点队列
    public static final String BEHAVIOR_EXCHANGE = "lifepulse.behavior.exchange";
    public static final String BEHAVIOR_QUEUE = "lifepulse.behavior.queue";
    public static final String BEHAVIOR_ROUTING_KEY = "behavior.log";
    /**
     * 秒杀专用 RabbitMQ 常量
     * 抗高并发、防超卖、削峰、异步扣库存
     */
        // 秒杀优惠券交换机
        public static final String SECKILL_EXCHANGE = "seckill.exchange";

        // 秒杀优惠券队列
        public static final String SECKILL_QUEUE = "seckill.queue";

        // 路由KEY
        public static final String SECKILL_ROUTING_KEY = "seckill.coupon";

        // 秒杀死信交换机
        public static final String SECKILL_DEAD_LETTER_EXCHANGE = "seckill.dead.letter.exchange";

        // 秒杀死信队列
        public static final String SECKILL_DEAD_LETTER_QUEUE = "seckill.dead.letter.queue";

        // 秒杀死信路由KEY
        public static final String SECKILL_DEAD_LETTER_ROUTING_KEY = "seckill.dead.letter.coupon";
}