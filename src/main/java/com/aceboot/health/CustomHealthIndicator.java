package com.aceboot.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 自定义健康检查指示器
 *
 * @author ace-boot
 * @since 1.0.0
 */
@Component
public class CustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        try {
            // 这里可以添加实际的健康检查逻辑
            // 例如：检查数据库连接、外部服务可用性等

            String currentTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            return Health.up()
                    .withDetail("application", "ace-boot")
                    .withDetail("version", "1.0.0-SNAPSHOT")
                    .withDetail("timestamp", currentTime)
                    .withDetail("description", "ACE-Boot service is healthy")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}