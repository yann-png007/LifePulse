package com.lifepulse.controller.admin;

import com.lifepulse.common.Result;
import com.lifepulse.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/user")
public class UserAdminController {

    private final UserService userService;

    public UserAdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(userService.list());
    }
}
