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
     * @param rawPwd 用户输入的明文密码
     * @return 加密后的密文
     */
    public static String encodePwd(String rawPwd) {
        return encoder.encode(rawPwd);
    }

    /**
     * 密码比对校验（登录时使用）
     * @param rawPwd 用户输入的明文密码
     * @param encodePwd 数据库中存储的密文密码
     * @return true=密码匹配，false=密码错误
     */
    public static boolean matchPwd(String rawPwd, String encodePwd) {
        return encoder.matches(rawPwd, encodePwd);
    }
}