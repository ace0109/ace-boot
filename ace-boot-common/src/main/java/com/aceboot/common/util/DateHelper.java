package com.aceboot.common.util;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.time.DateUtils;

/**
 * 日期时间工具，基于 java.time 并桥接 commons-lang3 DateUtils。
 */
public final class DateHelper {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private DateHelper() {
    }

    public static String formatIso(LocalDateTime time) {
        return ISO_FORMATTER.format(time);
    }

    public static LocalDateTime fromInstant(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static String format(LocalDateTime time, String pattern) {
        Objects.requireNonNull(time, "time must not be null");
        Objects.requireNonNull(pattern, "pattern must not be null");
        return DateTimeFormatter.ofPattern(pattern).format(time);
    }

    public static LocalDateTime parse(String text, String pattern) {
        Objects.requireNonNull(text, "text must not be null");
        Objects.requireNonNull(pattern, "pattern must not be null");
        return LocalDateTime.parse(text, DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDateTime parseFlexible(String text, String... patterns) {
        Objects.requireNonNull(text, "text must not be null");
        if (patterns == null || patterns.length == 0) {
            throw new IllegalArgumentException("patterns must not be empty");
        }
        try {
            Date parsed = DateUtils.parseDate(text, patterns);
            return LocalDateTime.ofInstant(parsed.toInstant(), ZoneId.systemDefault());
        }
        catch (ParseException e) {
            throw new IllegalArgumentException("Date parse error", e);
        }
    }
}
