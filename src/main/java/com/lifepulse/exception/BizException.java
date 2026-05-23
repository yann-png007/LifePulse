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

    public BizException(Integer code,String msg){
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}
