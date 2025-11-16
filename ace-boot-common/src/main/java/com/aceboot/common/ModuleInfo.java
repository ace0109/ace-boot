package com.aceboot.common;

import java.time.Instant;

/**
 * 提供 common 模块的元数据，方便在样例服务中引用。
 */
public class ModuleInfo {

    private ModuleInfo() {
    }

    public static String name() {
        return "ace-boot-common";
    }

    public static Instant loadedAt() {
        return Instant.EPOCH;
    }
}
