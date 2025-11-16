package com.aceboot.common.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BaseExceptionTest {

    @Test
    void shouldExposeCodeAndMessage() {
        BaseException exception = new BaseException("CODE", "message");

        assertThat(exception.getCode()).isEqualTo("CODE");
        assertThat(exception.getMessage()).isEqualTo("message");
    }
}
