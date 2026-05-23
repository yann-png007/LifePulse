package com.lifepulse.dto;

import com.lifepulse.enums.JwtErrorType;
import lombok.Getter;

@Getter
public class JwtValidationResult {

    private final boolean isValid;
    private final Long userId;
    private final String role;
    private final JwtErrorType errorType;

    // 校验成功时调用
    private JwtValidationResult(Long userId, String role) {
        this.isValid = true;
        this.userId = userId;
        this.role = role;
        this.errorType = JwtErrorType.VALID;
    }

    // 校验失败时调用
    private JwtValidationResult(JwtErrorType errorType) {
        this.isValid = false;
        this.userId = null;
        this.role = null;
        this.errorType = errorType;
    }

    /**
     * 创建一个表示校验成功的实例
     * @param userId 从令牌中解析出的用户ID
     * @param role 从令牌中解析出的用户角色
     * @return 成功的校验结果
     */
    public static JwtValidationResult success(Long userId, String role) {
        return new JwtValidationResult(userId, role);
    }

    /**
     * 创建一个表示校验成功的实例（兼容旧版，角色默认为null）
     * @param userId 从令牌中解析出的用户ID
     * @return 成功的校验结果
     */
    public static JwtValidationResult success(Long userId) {
        return new JwtValidationResult(userId, null);
    }

    /**
     * 创建一个表示校验失败的实例
     * @param errorType 具体的错误类型
     * @return 失败的校验结果
     */
    public static JwtValidationResult fail(JwtErrorType errorType) {
        return new JwtValidationResult(errorType);
    }
}