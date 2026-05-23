package com.lifepulse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lifepulse.entity.User;
import com.lifepulse.common.PageResult;

public interface UserService extends IService<User> {

    // 新增用户-自动加密密码
    void addUser(User user);

    // 登录业务
    String login(String username, String password);

    // 分页查询用户
    PageResult<User> getUserPage(Integer pageNum, Integer pageSize);

    // 修改密码
    void modifyPwd(Long userId, String oldPwd, String newPwd);

    // 退出登录
    void logout(String token);

    // 根据ID查询用户
    User getUserById(Long id);

    // 编辑用户
    void editUser(User user);

    // 删除用户
    void removeUser(Long id);
}