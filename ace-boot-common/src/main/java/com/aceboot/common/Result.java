package com.aceboot.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Objects;

/**
 * 通用的 API 响应包装类，提供统一的 code/message/data/timestamp 结构。
 *
 * @param <T> 负载数据类型
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class Result<T> {

    private static final String DEFAULT_SUCCESS_CODE = "SUCCESS";
    private static final String DEFAULT_SUCCESS_MESSAGE = "OK";

    private final String code;
    private final String message;
    private final T data;
    private final Instant timestamp;

    private Result(String code, String message, T data, Instant timestamp) {
        this.code = Objects.requireNonNull(code, "code must not be null");
        this.message = Objects.requireNonNull(message, "message must not be null");
        this.data = data;
        this.timestamp = Objects.requireNonNull(timestamp, "timestamp must not be null");
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MESSAGE, data, Instant.now());
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(DEFAULT_SUCCESS_CODE, message, data, Instant.now());
    }

    public static <T> Result<T> failure(String code, String message) {
        return new Result<>(code, message, null, Instant.now());
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public boolean isSuccess() {
        return DEFAULT_SUCCESS_CODE.equals(this.code);
    }
}

