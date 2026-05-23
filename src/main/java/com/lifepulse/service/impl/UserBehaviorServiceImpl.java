package com.lifepulse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifepulse.constant.CacheConstant;
import com.lifepulse.dto.UserBehaviorRecordDTO;
import com.lifepulse.entity.UserBehavior;
import com.lifepulse.mapper.UserBehaviorMapper;
import com.lifepulse.service.UserBehaviorService;
import com.lifepulse.util.RabbitMQProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UserBehaviorServiceImpl extends ServiceImpl<UserBehaviorMapper, UserBehavior> implements UserBehaviorService {

    private final ObjectMapper objectMapper;
    private final RabbitMQProducer rabbitMQProducer;
    private final StringRedisTemplate stringRedisTemplate;

    public UserBehaviorServiceImpl(ObjectMapper objectMapper, RabbitMQProducer rabbitMQProducer, StringRedisTemplate stringRedisTemplate) {
        this.objectMapper = objectMapper;
        this.rabbitMQProducer = rabbitMQProducer;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void asyncRecordBehavior(UserBehaviorRecordDTO dto, Long userId) {
        String repeatKey = CacheConstant.BEHAVIOR_REPEAT_PREFIX + userId + ":" + dto.getTargetId() + ":" + dto.getBehaviorType();
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(repeatKey))) {
            log.debug("用户行为重复上报，已忽略: userId={}, targetId={}, behaviorType={}", userId, dto.getTargetId(), dto.getBehaviorType());
            return;
        }

        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setTargetId(dto.getTargetId());
        behavior.setType(dto.getBehaviorType());
        behavior.setCreateTime(LocalDateTime.now());

        try {
            String jsonStr = objectMapper.writeValueAsString(behavior);
            rabbitMQProducer.sendLogMsg(jsonStr);
            stringRedisTemplate.opsForValue().set(repeatKey, "1", CacheConstant.BEHAVIOR_REPEAT_EXPIRE);
        } catch (JsonProcessingException e) {
            log.error("用户行为序列化失败，降级同步入库: userId={}, targetId={}, behaviorType={}", userId, dto.getTargetId(), dto.getBehaviorType(), e);
            save(behavior);
        } catch (Exception e) {
            log.error("用户行为异步投递失败，降级同步入库: userId={}, targetId={}, behaviorType={}", userId, dto.getTargetId(), dto.getBehaviorType(), e);
            save(behavior);
        }
    }
}