package com.lifepulse.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密与比对工具类
 * 采用BCrypt算法自动加盐，无需手动处理盐值
 */
public class PasswordUtil {

    // 全局单例BCrypt加密器
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 明文密码加密（注册时使用）
     * @param rawPassword 用户输入的明文密码
     * @return 加密后的密文
     */
    public static String encrypt(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /**
     * 密码比对校验（登录时使用）
     * @param rawPassword 用户输入的明文密码
     * @param encodedPassword 数据库中存储的密文密码
     * @return true=密码匹配，false=密码错误
     */
    public static boolean verify(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}