package com.aceboot.common.util;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class StringHelperTest {

    @Test
    void shouldDelegateIsBlank() {
        assertThat(StringHelper.isBlank(" ")).isTrue();
        assertThat(StringHelper.isBlank("ace")).isFalse();
    }

    @Test
    void shouldAbbreviate() {
        final int width = 10;
        assertThat(StringHelper.abbreviate("abcdefghijklmnopqrstuvwxyz", width))
                .isEqualTo("abcdefg...");
    }
}
