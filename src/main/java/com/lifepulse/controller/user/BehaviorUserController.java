package com.lifepulse.controller.user;

import com.lifepulse.common.Result;
import com.lifepulse.dto.UserBehaviorRecordDTO;
import com.lifepulse.service.UserBehaviorService;
import com.lifepulse.util.UserContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/behavior")
public class BehaviorUserController {

    private final UserBehaviorService behaviorService;

    public BehaviorUserController(UserBehaviorService behaviorService) {
        this.behaviorService = behaviorService;
    }

    // 提交用户行为（浏览、收藏、点击等）
    @PostMapping("/record")
    public Result<Void> recordBehavior(@Validated @RequestBody UserBehaviorRecordDTO dto) {
        Long userId = UserContext.getUserId();
        behaviorService.asyncRecordBehavior(dto,userId);
        return Result.success();
    }
}