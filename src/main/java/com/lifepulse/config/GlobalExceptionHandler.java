package com.lifepulse.config;

import com.lifepulse.common.Result;
import com.lifepulse.common.ResultCode;
import com.lifepulse.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局统一异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    @ResponseBody
    public Result<?> handleBizException(BizException e) {
        log.warn("业务异常: {}", e.getMsg());
        return Result.error(e.getCode(), e.getMsg()); // 就用这一行
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常", e);
        return Result.error(ResultCode.FAIL);
    }

    @ExceptionHandler(Exception.class)
    public Result<String> handleAllException(Exception e) {
        log.error("系统异常", e);
        return Result.error(ResultCode.FAIL);
    }
}