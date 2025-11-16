package com.aceboot.common.web;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 记录请求耗时与参数的拦截器。
 */
public class RequestLogInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLogInterceptor.class);
    private static final String START_TIME_ATTRIBUTE = RequestLogInterceptor.class.getName() + ".START";

    private final RequestLogProperties properties;

    public RequestLogInterceptor(RequestLogProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (properties.isEnabled()) {
            request.setAttribute(START_TIME_ATTRIBUTE, System.nanoTime());
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (!properties.isEnabled()) {
            return;
        }
        Object startAttr = request.getAttribute(START_TIME_ATTRIBUTE);
        if (!(startAttr instanceof Long startNanos)) {
            return;
        }
        long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
        String query = request.getQueryString();
        LOGGER.info(
                "HTTP {} {} status={} duration={}ms params={} remote={}",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                duration,
                query == null ? "-" : query,
                request.getRemoteAddr());
    }
}
