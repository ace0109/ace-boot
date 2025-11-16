package com.aceboot.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.aceboot.common.Result;

/**
 * 全局异常处理，统一包装为 Result 格式。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BaseException.class)
    public Result<Void> handleBaseException(BaseException exception) {
        LOGGER.warn("Business exception: {} - {}", exception.getCode(), exception.getMessage());
        return Result.failure(exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class,
            HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidationException(Exception exception) {
        LOGGER.warn("Validation exception", exception);
        return Result.failure("VALIDATION_ERROR", resolveValidationMessage(exception));
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleOtherExceptions(Exception exception) {
        LOGGER.error("Unexpected exception", exception);
        return Result.failure("INTERNAL_ERROR", "系统繁忙，请稍后再试");
    }

    private String resolveValidationMessage(Exception exception) {
        if (exception instanceof MethodArgumentNotValidException bindingException) {
            return resolveFieldError(bindingException.getBindingResult());
        }
        if (exception instanceof BindException bindException) {
            return resolveFieldError(bindException.getBindingResult());
        }
        if (exception instanceof HttpMessageNotReadableException) {
            return "请求体解析失败";
        }
        return "请求参数校验失败";
    }

    private String resolveFieldError(BindingResult bindingResult) {
        FieldError fieldError = bindingResult.getFieldError();
        if (fieldError != null) {
            return fieldError.getDefaultMessage();
        }
        return "请求参数校验失败";
    }
}
