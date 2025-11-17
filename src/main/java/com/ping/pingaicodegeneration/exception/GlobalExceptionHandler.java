package com.ping.pingaicodegeneration.exception;

import com.ping.pingaicodegeneration.common.BaseResponse;
import com.ping.pingaicodegeneration.common.ResultUtils;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.ping.pingaicodegeneration.exception.ErrorCode.SYSTEM_ERROR;

/**
 * 全局异常处理器
 */
@Hidden // swagger 隐藏该类避免不兼容
@RestControllerAdvice // 全局异常处理器
@Slf4j
public class GlobalExceptionHandler {

    // 切点
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("业务异常 BusinessException", e);
        return ResultUtils.error(e.getCode(),e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("运行时异常 RuntimeException", e);
        return ResultUtils.error(SYSTEM_ERROR,SYSTEM_ERROR.getMessage());
    }

    /**
     * 处理所有其他异常（可选添加）
     */
    @ExceptionHandler(Exception.class)
    public BaseResponse<?> exceptionHandler(Exception e) {
        log.error("系统异常 Exception", e);
        return ResultUtils.error(SYSTEM_ERROR, "系统繁忙，请稍后重试");
    }
}
