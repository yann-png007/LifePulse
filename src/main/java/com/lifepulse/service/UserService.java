package com.lifepulse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lifepulse.common.PageResult;
import com.lifepulse.dto.LoginByCodeDTO;
import com.lifepulse.dto.LoginByPasswordDTO;
import com.lifepulse.entity.User;

public interface UserService extends IService<User> {

    // 新增用户-自动加密密码
    void addUser(User user);

    /**
     * 发送手机登录验证码
     * @param phone 手机号
     */
    void sendLoginCode(String phone);

    /**
     * 手机号+密码 登录
     * @param loginDTO 包含手机号和密码的DTO
     * @return JWT Token
     */
    String loginByPassword(LoginByPasswordDTO loginDTO);

    /**
     * 手机号+验证码 登录/注册
     * @param loginDTO 包含手机号和验证码的DTO
     * @return JWT Token
     */
    String loginByCode(LoginByCodeDTO loginDTO);

    /**
     * 分页查询用户列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
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