package com.aceboot.starter.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import static org.assertj.core.api.Assertions.assertThat;

import com.aceboot.common.web.RequestLogInterceptor;

class AceBootWebAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AceBootWebAutoConfiguration.class));

    @Test
    void shouldRegisterRequestLogBeansByDefault() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(RequestLogInterceptor.class);
            assertThat(context).hasBean(AceBootWebAutoConfiguration.REQUEST_LOG_CONFIGURER_BEAN_NAME);
        });
    }

    @Test
    void shouldBackOffConfigurerWhenDisabled() {
        contextRunner.withPropertyValues("aceboot.request-log.enabled=false").run(context -> {
            assertThat(context).doesNotHaveBean(AceBootWebAutoConfiguration.REQUEST_LOG_CONFIGURER_BEAN_NAME);
            assertThat(context).hasSingleBean(RequestLogInterceptor.class);
        });
    }
}
