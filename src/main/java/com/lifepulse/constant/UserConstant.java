package com.lifepulse.constant;

public class UserConstant {

    // 用户登录token前缀
    public static final String USER_LOGIN_TOKEN = "user:login:token:";

    // token过期时间 7天
    public static final long USER_TOKEN_EXPIRE = 7 * 86400L;

    // 用户状态
    public static final Integer USER_STATUS_NORMAL = 1;
    public static final Integer USER_STATUS_DISABLE = 0;
    // 用户角色常量
    public static final Integer ROLE_USER = 0;
    public static final Integer ROLE_ADMIN = 1;
}