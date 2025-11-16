package com.aceboot.common.exception;

import com.aceboot.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理，统一包装为 Result 格式。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BaseException.class)
    public Result<Void> handleBaseException(BaseException exception) {
        log.warn("Business exception: {} - {}", exception.getCode(), exception.getMessage());
        return Result.failure(exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class,
            HttpMessageNotReadableException.class})
    public Result<Void> handleValidationException(Exception exception) {
        log.warn("Validation exception", exception);
        return Result.failure("VALIDATION_ERROR", "请求参数校验失败");
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleOtherExceptions(Exception exception) {
        log.error("Unexpected exception", exception);
        return Result.failure("INTERNAL_ERROR", "系统繁忙，请稍后再试");
    }
}
