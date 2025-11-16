package com.aceboot.common.util;

import java.time.Instant;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class DateHelperTest {

    @Test
    void shouldFormatIso() {
        final int year = 2025;
        final int month = 1;
        final int day = 2;
        final int hour = 3;
        final int minute = 4;
        final int second = 5;
        LocalDateTime time = LocalDateTime.of(year, month, day, hour, minute, second);
        assertThat(DateHelper.formatIso(time)).isEqualTo("2025-01-02T03:04:05");
    }

    @Test
    void shouldConvertFromInstant() {
        Instant instant = Instant.parse("2025-01-02T03:04:05Z");
        assertThat(DateHelper.fromInstant(instant)).isNotNull();
    }

    @Test
    void shouldFormatWithPattern() {
        LocalDateTime time = LocalDateTime.of(2025, 2, 3, 4, 5, 6);
        assertThat(DateHelper.format(time, "yyyy/MM/dd HH:mm:ss"))
                .isEqualTo("2025/02/03 04:05:06");
    }

    @Test
    void shouldParseWithPattern() {
        LocalDateTime parsed = DateHelper.parse("2025-02-03 04:05:06", "yyyy-MM-dd HH:mm:ss");
        assertThat(parsed.getYear()).isEqualTo(2025);
    }

    @Test
    void shouldParseFlexiblePatterns() {
        LocalDateTime parsed = DateHelper.parseFlexible("2025/02/03", "yyyyMMdd", "yyyy/MM/dd");
        assertThat(parsed.getMonthValue()).isEqualTo(2);
    }
}
