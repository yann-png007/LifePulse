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

    // --- 新增：自动注册相关常量 ---
    /**
     * 自动注册用户的默认用户名前缀
     */
    public static final String DEFAULT_USERNAME_PREFIX = "user_";

    /**
     * 自动注册用户的默认密码
     */
    public static final String DEFAULT_PASSWORD = "123456";

    /**
     * 自动注册用户的默认角色 (普通用户)
     */
    public static final String DEFAULT_ROLE = "USER";
}