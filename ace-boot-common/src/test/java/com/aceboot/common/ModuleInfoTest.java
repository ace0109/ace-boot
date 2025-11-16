package com.aceboot.common;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ModuleInfoTest {

    @Test
    void shouldExposeModuleMetadata() {
        assertThat(ModuleInfo.name()).isEqualTo("ace-boot-common");
        assertThat(ModuleInfo.loadedAt()).isEqualTo(Instant.EPOCH);
    }
}
