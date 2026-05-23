package com.lifepulse.entity;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String gender;
    private Integer status;
    private String role;
    private LocalDateTime createTime;
    private LocalDateTime updateTime; // 创建时间


}