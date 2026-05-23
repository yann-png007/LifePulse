package com.lifepulse.enums;

import com.lifepulse.common.ResultCode;
import lombok.Getter;

/**
 * JWT 校验错误类型枚举
 */
@Getter
public enum JwtErrorType {
    VALID(ResultCode.SUCCESS),                  // 有效
    EXPIRED(ResultCode.NOT_LOGIN),                // 已过期
    INVALID_SIGNATURE(ResultCode.NOT_LOGIN),      // 签名无效
    MALFORMED(ResultCode.NOT_LOGIN),              // 格式错误
    UNSUPPORTED(ResultCode.NOT_LOGIN),            // 不支持的JWT
    ILLEGAL_ARGUMENT(ResultCode.NOT_LOGIN),       // 参数错误
    UNKNOWN_ERROR(ResultCode.FAIL);           // 未知错误

    private final ResultCode resultCode;

    JwtErrorType(ResultCode resultCode) {
        this.resultCode = resultCode;
    }
}