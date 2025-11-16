package com.aceboot.common.exception;

/**
 * 默认的业务异常实现。
 */
public class BusinessException extends BaseException {

    public BusinessException(String code, String message) {
        super(code, message);
    }

    public static BusinessException of(String message) {
        return new BusinessException("BIZ_ERROR", message);
    }
}
