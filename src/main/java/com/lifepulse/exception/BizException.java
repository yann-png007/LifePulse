package com.lifepulse.exception;

import com.lifepulse.common.ResultCode;
import lombok.Getter;

@Getter
public class BizException extends RuntimeException{
    private final Integer code;
    private final String msg;

    public BizException(ResultCode resultCode){
        super(resultCode.getMsg());
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
    }
    /**
     * 新增构造函数，允许在标准错误码的基础上，提供更具体的动态错误信息
     * @param resultCode 标准错误码
     * @param msg 具体的动态错误信息
     */
    public BizException(ResultCode resultCode, String msg) {
        super(msg);
        this.code = resultCode.getCode();
        this.msg = msg;
    }

    public BizException(Integer code,String msg){
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}
