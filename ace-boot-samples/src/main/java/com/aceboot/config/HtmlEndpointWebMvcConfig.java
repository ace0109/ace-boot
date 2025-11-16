package com.aceboot.config;

import com.aceboot.health.HtmlAcceptRequestCondition;
import com.aceboot.health.HtmlOnly;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * 将标准 MVC 映射的顺序提升到最高优先级，
 * 以便在内容协商时优先尝试控制器，从而允许 `/actuator/health`
 * 在 Accept 为 text/html 时由自定义控制器接管。
 */
@Configuration
public class HtmlEndpointWebMvcConfig implements WebMvcRegistrations {

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new RequestMappingHandlerMapping() {
            @Override
            public void afterPropertiesSet() {
                setOrder(Ordered.HIGHEST_PRECEDENCE);
                super.afterPropertiesSet();
            }

            @Override
            protected RequestCondition<?> getCustomMethodCondition(Method method) {
                if (AnnotatedElementUtils.hasAnnotation(method, HtmlOnly.class)) {
                    return new HtmlAcceptRequestCondition();
                }
                return super.getCustomMethodCondition(method);
            }

            @Override
            protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
                if (AnnotatedElementUtils.hasAnnotation(handlerType, HtmlOnly.class)) {
                    return new HtmlAcceptRequestCondition();
                }
                return super.getCustomTypeCondition(handlerType);
            }
        };
    }
}
