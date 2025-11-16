package com.aceboot.common.util;

import java.util.Map;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;

class JsonHelperTest {

    @Test
    void shouldSerializeAndDeserialize() {
        String json = JsonHelper.toJson(Map.of("key", "value"));
        assertThat(json).contains("\"key\":\"value\"");

        Map<String, String> restored = JsonHelper.fromJson(json, new TypeReference<>() { });
        assertThat(restored).containsEntry("key", "value");
    }

    @Test
    void mapperShouldBeReusable() {
        assertThat(JsonHelper.mapper()).isNotNull();
    }
}
