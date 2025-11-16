package com.aceboot.starter.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.web.cors.CorsConfigurationSource;
import static org.assertj.core.api.Assertions.assertThat;

class CorsAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CorsAutoConfiguration.class));

    @Test
    void shouldBackOffWhenDisabled() {
        contextRunner.run(context -> assertThat(context).doesNotHaveBean(CorsConfigurationSource.class));
    }

    @Test
    void shouldRegisterCorsConfigurationWhenEnabled() {
        contextRunner.withPropertyValues("aceboot.cors.enabled=true",
                        "aceboot.cors.allowed-origins=https://foo.com,https://bar.com")
                .run(context -> {
                    assertThat(context).hasSingleBean(CorsConfigurationSource.class);
                    CorsProperties properties = context.getBean(CorsProperties.class);
                    assertThat(properties.getAllowedOrigins()).contains("https://foo.com");
                });
    }
}
