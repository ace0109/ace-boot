package com.aceboot.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.aceboot.common.web.RequestLogInterceptor;
import com.aceboot.common.web.RequestLogProperties;

@Configuration
@EnableConfigurationProperties(RequestLogProperties.class)
public class RequestLoggingConfig implements WebMvcConfigurer {

    private final RequestLogProperties properties;
    private final RequestLogInterceptor interceptor;

    public RequestLoggingConfig(RequestLogProperties properties) {
        this.properties = properties;
        this.interceptor = new RequestLogInterceptor(properties);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (properties.isEnabled()) {
            registry.addInterceptor(interceptor);
        }
    }
}
