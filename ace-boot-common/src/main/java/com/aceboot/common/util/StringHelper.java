package com.aceboot.common.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符串工具封装，委托 commons-lang3。
 */
public final class StringHelper {

    private StringHelper() {
    }

    public static boolean isBlank(String input) {
        return StringUtils.isBlank(input);
    }

    public static String abbreviate(String input, int maxWidth) {
        return StringUtils.abbreviate(input, maxWidth);
    }
}
