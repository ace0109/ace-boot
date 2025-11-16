package com.aceboot.common.exception;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aceboot.common.Result;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldTranslateBusinessException() {
        BusinessException exception = new BusinessException("BIZ", "boom");
        Result<Void> result = handler.handleBaseException(exception);

        assertNotNull(result);
        assertEquals("BIZ", result.getCode());
        assertEquals("boom", result.getMessage());
    }

    @Test
    void shouldHandleUnexpectedException() {
        IllegalStateException exception = new IllegalStateException("nope");
        Result<Void> result = handler.handleOtherExceptions(exception);

        assertNotNull(result);
        assertEquals("INTERNAL_ERROR", result.getCode());
        assertEquals("系统繁忙，请稍后再试", result.getMessage());
    }

    @Test
    void shouldReturnFieldMessageForValidationErrors() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "name", "昵称不能为空"));
        BindException exception = new BindException(bindingResult);

        Result<Void> result = handler.handleValidationException(exception);

        assertEquals("VALIDATION_ERROR", result.getCode());
        assertEquals("昵称不能为空", result.getMessage());
    }
}
