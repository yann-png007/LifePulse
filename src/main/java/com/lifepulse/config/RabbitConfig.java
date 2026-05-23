package com.lifepulse.config;

import com.lifepulse.constant.MQConstant;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ交换机、队列、绑定关系配置
 */
@Configuration
public class RabbitConfig {

    // 订单交换机
    @Bean
    public Exchange orderTopicExchange(){
        return ExchangeBuilder.topicExchange(MQConstant.ORDER_TOPIC_EXCHANGE).durable(true).build();
    }

    // 订单队列
    @Bean
    public Queue orderCreateQueue(){
        return QueueBuilder.durable(MQConstant.ORDER_CREATE_QUEUE).build();
    }

    // 绑定关系
    @Bean
    public Binding orderBinding(){
        return BindingBuilder.bind(orderCreateQueue())
                .to(orderTopicExchange())
                .with(MQConstant.ORDER_CREATE_ROUTING_KEY)
                .noargs();
    }
}