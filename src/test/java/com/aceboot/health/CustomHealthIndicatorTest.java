package com.aceboot.health;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CustomHealthIndicator 单元测试
 *
 * @author ace-boot
 * @since 1.0.0
 */
class CustomHealthIndicatorTest {

    private final CustomHealthIndicator healthIndicator = new CustomHealthIndicator();

    @Test
    void health_shouldReturnUp_whenServiceIsHealthy() {
        // when
        Health health = healthIndicator.health();

        // then
        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsKey("application");
        assertThat(health.getDetails()).containsKey("version");
        assertThat(health.getDetails()).containsKey("timestamp");
        assertThat(health.getDetails()).containsKey("description");
        assertThat(health.getDetails().get("application")).isEqualTo("ace-boot");
        assertThat(health.getDetails().get("version")).isEqualTo("1.0.0-SNAPSHOT");
        assertThat(health.getDetails().get("description")).isEqualTo("ACE-Boot service is healthy");
    }

    @Test
    void health_shouldContainTimestamp() {
        // when
        Health health = healthIndicator.health();

        // then
        assertThat(health.getDetails()).containsKey("timestamp");
        assertThat(health.getDetails().get("timestamp")).isNotNull();
        assertThat(health.getDetails().get("timestamp").toString()).isNotEmpty();
    }
}