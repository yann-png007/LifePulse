package com.lifepulse.util;

/**
 * 线程上下文工具
 * 存放当前登录用户ID，全局随时获取
 */
public class UserContext {
    private static final ThreadLocal<Long> USER_ID_THREAD_LOCAL = new ThreadLocal<>();

    public static void setUserId(Long userId){
        USER_ID_THREAD_LOCAL.set(userId);
    }

    public static Long getUserId(){
        return USER_ID_THREAD_LOCAL.get();
    }

    public static void clear(){
        USER_ID_THREAD_LOCAL.remove();
    }
}