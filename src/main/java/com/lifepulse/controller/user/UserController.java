package com.lifepulse.controller.user;

import com.lifepulse.common.PageResult;
import com.lifepulse.common.Result;
import com.lifepulse.dto.LoginByCodeDTO;
import com.lifepulse.dto.LoginByPasswordDTO;
import com.lifepulse.entity.User;
import com.lifepulse.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    /**
     * @deprecated 已废弃，请使用 /login/password 或 /login/code 接口
     */


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

    /**
     * 发送手机登录验证码
     * @param payload 包含手机号的请求体, e.g., {"phone": "138xxxxxxxx"}
     * @return 统一响应结果
     */
    @PostMapping("/code")
    public Result<Void> sendLoginCode(@RequestBody Map<String, String> payload) {
        String phone = payload.get("phone");
        userService.sendLoginCode(phone);
        return Result.success();
    }

    /**
     * 手机号 + 密码登录
     * @param loginDTO 登录数据传输对象
     * @return 包含Token的统一响应结果
     */
    @PostMapping("/login/password")
    public Result<String> loginByPassword(@RequestBody LoginByPasswordDTO loginDTO) {
        String token = userService.loginByPassword(loginDTO);
        return Result.success(token);
    }

    /**
     * 手机号 + 验证码登录或注册
     * @param loginDTO 登录数据传输对象
     * @return 包含Token的统一响应结果
     */
    @PostMapping("/login/code")
    public Result<String> loginByCode(@RequestBody LoginByCodeDTO loginDTO) {
        String token = userService.loginByCode(loginDTO);
        return Result.success(token);
    }
}