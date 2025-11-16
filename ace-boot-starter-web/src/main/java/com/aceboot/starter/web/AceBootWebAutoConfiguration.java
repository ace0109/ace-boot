package com.aceboot.starter.web;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.aceboot.common.web.RequestLogInterceptor;
import com.aceboot.common.web.RequestLogProperties;

/**
 * 自动配置 Web 层基础能力：启用请求日志拦截器并开放配置属性。
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(WebMvcConfigurer.class)
@EnableConfigurationProperties(RequestLogProperties.class)
public class AceBootWebAutoConfiguration {

    static final String REQUEST_LOG_CONFIGURER_BEAN_NAME = "aceBootRequestLogWebMvcConfigurer";

    @Bean
    @ConditionalOnMissingBean
    public RequestLogInterceptor requestLogInterceptor(RequestLogProperties properties) {
        return new RequestLogInterceptor(properties);
    }

    @Bean(name = REQUEST_LOG_CONFIGURER_BEAN_NAME)
    @ConditionalOnMissingBean(name = REQUEST_LOG_CONFIGURER_BEAN_NAME)
    @ConditionalOnProperty(prefix = "aceboot.request-log", name = "enabled", matchIfMissing = true)
    public WebMvcConfigurer aceBootRequestLogWebMvcConfigurer(RequestLogInterceptor requestLogInterceptor) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(requestLogInterceptor);
            }
        };
    }
}
