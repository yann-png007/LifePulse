package com.lifepulse.config;

import com.lifepulse.constant.MQConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 声明日志交换机
    @Bean
    public DirectExchange logExchange() {
        return new DirectExchange(MQConstant.LOG_EXCHANGE, true, false);
    }

    // 声明日志队列
    @Bean
    public Queue logQueue() {
        return new Queue(MQConstant.LOG_QUEUE, true);
    }

    // 绑明日志队列到交换机
    @Bean
    public Binding logBinding() {
        return BindingBuilder.bind(logQueue()).to(logExchange()).with(MQConstant.LOG_ROUTING_KEY);
    }
    // ==================== 秒杀业务 ====================
    // 交换机
    @Bean
    public DirectExchange seckillExchange() {
        return new DirectExchange(MQConstant.SECKILL_EXCHANGE, true, false);
    }

    // 死信交换机 (DLX)
    @Bean
    public DirectExchange seckillDeadLetterExchange() {
        return new DirectExchange(MQConstant.SECKILL_DEAD_LETTER_EXCHANGE, true, false);
    }

    // 秒杀队列，并绑定死信交换机
    @Bean
    public Queue seckillQueue() {
        return QueueBuilder.durable(MQConstant.SECKILL_QUEUE)
                .withArgument("x-dead-letter-exchange", MQConstant.SECKILL_DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", MQConstant.SECKILL_DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    // 死信队列 (DLQ)
    @Bean
    public Queue seckillDeadLetterQueue() {
        return new Queue(MQConstant.SECKILL_DEAD_LETTER_QUEUE, true);
    }

    // 绑定
    @Bean
    public Binding seckillBinding() {
        return BindingBuilder.bind(seckillQueue()).to(seckillExchange()).with(MQConstant.SECKILL_ROUTING_KEY);
    }

    // 绑定死信队列到死信交换机
    @Bean
    public Binding seckillDeadLetterBinding() {
        return BindingBuilder.bind(seckillDeadLetterQueue()).to(seckillDeadLetterExchange()).with(MQConstant.SECKILL_DEAD_LETTER_ROUTING_KEY);
    }
}