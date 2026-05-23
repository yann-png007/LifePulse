package com.lifepulse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lifepulse.dto.UserBehaviorRecordDTO;
import com.lifepulse.entity.UserBehavior;

public interface UserBehaviorService extends IService<UserBehavior> {

    /**
     * 异步记录用户行为
     * @param dto 请求参数
     */
    void asyncRecordBehavior(UserBehaviorRecordDTO dto,Long userId);
}