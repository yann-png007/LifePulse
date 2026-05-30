package com.lifepulse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名，现在作为昵称使用
     */
    private String username;

    private String password;

    private String realName;

    /**
     * 手机号，作为用户的唯一登录标识
     */
    private String phone;

    private String gender;

    private Integer status;

    private String role;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}