package com.aceboot.common;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResultTest {

    @Test
    void success_shouldPopulateDefaultCodeAndMessage() {
        Result<String> result = Result.success("payload");

        assertThat(result.getCode()).isEqualTo("SUCCESS");
        assertThat(result.getMessage()).isEqualTo("OK");
        assertThat(result.getData()).isEqualTo("payload");
        assertThat(result.getTimestamp()).isNotNull();
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void success_withCustomMessageShouldUseProvidedMessage() {
        Result<Integer> result = Result.success("created", 1);

        assertThat(result.getCode()).isEqualTo("SUCCESS");
        assertThat(result.getMessage()).isEqualTo("created");
        assertThat(result.getData()).isEqualTo(1);
    }

    @Test
    void failure_shouldNotExposeData() {
        Result<Void> result = Result.failure("ERR", "boom");

        assertThat(result.getCode()).isEqualTo("ERR");
        assertThat(result.getMessage()).isEqualTo("boom");
        assertThat(result.getData()).isNull();
        assertThat(result.isSuccess()).isFalse();
    }

    @Test
    void constructorShouldRejectNullArguments() {
        assertThatThrownBy(() -> Result.failure(null, "msg"))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Result.failure("ERR", null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void timestampShouldBeImmutableSnapshot() {
        Result<String> result = Result.success("data");
        Instant first = result.getTimestamp();

        assertThat(result.getTimestamp()).isSameAs(first);
    }
}

