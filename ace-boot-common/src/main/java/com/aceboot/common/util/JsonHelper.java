package com.aceboot.common.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonHelper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonHelper() {
    }

    public static String toJson(Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        }
        catch (JsonProcessingException e) {
            throw new IllegalStateException("JSON serialize error", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> type) {
        try {
            return OBJECT_MAPPER.readValue(json, type);
        }
        catch (IOException e) {
            throw new IllegalStateException("JSON deserialize error", e);
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> type) {
        try {
            return OBJECT_MAPPER.readValue(json, type);
        }
        catch (IOException e) {
            throw new IllegalStateException("JSON deserialize error", e);
        }
    }

    public static ObjectMapper mapper() {
        return OBJECT_MAPPER;
    }
}
