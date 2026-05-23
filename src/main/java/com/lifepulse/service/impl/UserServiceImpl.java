package com.lifepulse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lifepulse.common.PageResult;
import com.lifepulse.common.ResultCode;
import com.lifepulse.entity.User;
import com.lifepulse.exception.BizException;
import com.lifepulse.mapper.UserMapper;
import com.lifepulse.service.UserService;
import com.lifepulse.util.JwtUtil;
import com.lifepulse.util.PasswordUtil;
import com.lifepulse.util.RedisUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    public UserServiceImpl(UserMapper userMapper, JwtUtil jwtUtil, RedisUtil redisUtil) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.redisUtil = redisUtil;
    }

    // 新增用户-自动加密密码
    @Override
    @Transactional
    public void addUser(User user){
        String encode = PasswordUtil.encodePwd(user.getPassword());
        user.setPassword(encode);
        if(user.getStatus() == null){
            user.setStatus(1);
        }
        userMapper.insertUser(user);
    }

    // 登录业务
    @Override
    public String login(String username,String password){
        User user = userMapper.selectUserByUsername(username);
        if(user == null) return null;
        if(!PasswordUtil.matchPwd(password,user.getPassword())) return null;
        if(user.getStatus() == 0) return "disable";
        // 生成令牌
        String token = jwtUtil.createToken(user.getId(), user.getRole());
        // 存入Redis 2小时
        redisUtil.set("login:token:"+token,user,7200);
        return token;
    }

    // 分页查询用户
    @Override
    public PageResult<User> getUserPage(Integer pageNum,Integer pageSize){
        PageResult<User> page = new PageResult<>();
        Integer start = (pageNum-1)*pageSize;
        List<User> list = userMapper.selectUserPage(start,pageSize);
        Long total = userMapper.selectUserCount();
        page.setList(list);
        page.setTotal(total);
        return page;
    }

    // 修改密码
    @Override
    @Transactional
    public void modifyPwd(Long userId,String oldPwd,String newPwd){
        User user = userMapper.selectUserById(userId);
        if(!PasswordUtil.matchPwd(oldPwd,user.getPassword())){
            throw new BizException(ResultCode.OLD_PASSWORD_ERROR);
        }
        String newEncode = PasswordUtil.encodePwd(newPwd);
        userMapper.updatePwd(userId,newEncode);
    }

    // 退出登录
    @Override
    public void logout(String token){
        redisUtil.delete("login:token:"+token);
    }

    @Override
    public User getUserById(Long id){
        return userMapper.selectUserById(id);
    }

    @Override
    @Transactional
    public void editUser(User user){
        userMapper.updateUser(user);
    }

    @Override
    @Transactional
    public void removeUser(Long id){
        userMapper.deleteUser(id);
    }
}