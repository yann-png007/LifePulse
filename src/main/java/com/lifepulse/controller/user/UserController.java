package com.lifepulse.controller.user;

import com.lifepulse.common.PageResult;
import com.lifepulse.common.Result;
import com.lifepulse.entity.User;
import com.lifepulse.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 新增用户
    @PostMapping("/add")
    public Result<Void> addUser(@RequestBody User user){
        userService.addUser(user);
        return Result.success();
    }

    // 登录
    @PostMapping("/login")
    public Result<String> login(String username,String password){
        String token = userService.login(username,password);
        return Result.success(token);
    }

    // 分页查询
    @GetMapping("/page")
    public Result<PageResult<User>> getUserPage(Integer pageNum,Integer pageSize){
        PageResult<User> page = userService.getUserPage(pageNum,pageSize);
        return Result.success(page);
    }

    // 根据id查询
    @GetMapping("/getById")
    public Result<User> getById(Long id){
        return Result.success(userService.getUserById(id));
    }

    // 修改用户信息
    @PutMapping("/update")
    public Result<Void> update(@RequestBody User user){
        userService.editUser(user);
        return Result.success();
    }

    // 删除用户
    @DeleteMapping("/delete")
    public Result<Void> delete(Long id){
        userService.removeUser(id);
        return Result.success();
    }

    // 修改密码
    @PostMapping("/modifyPwd")
    public Result<Void> modifyPwd(Long userId,String oldPwd,String newPwd){
        userService.modifyPwd(userId,oldPwd,newPwd);
        return Result.success();
    }

    // 退出登录
    @PostMapping("/logout")
    public Result<Void> logout(String token){
        userService.logout(token);
        return Result.success();
    }
}