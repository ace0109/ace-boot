package com.aceboot.common.web;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletResponse;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import static org.assertj.core.api.Assertions.assertThat;

class RequestLogInterceptorTest {

    private HandlerInterceptor interceptor;
    private RequestLogProperties properties;

    @BeforeEach
    void setUp() {
        properties = new RequestLogProperties();
        interceptor = new RequestLogInterceptor(properties);
    }

    @Test
    void shouldLogRequestWhenEnabled() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/hello");
        request.setQueryString("name=ace");
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(HttpServletResponse.SC_OK);

        Logger logger = (Logger) LoggerFactory.getLogger(RequestLogInterceptor.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try {
            interceptor.preHandle(request, response, new Object());
            interceptor.afterCompletion(request, response, new Object(), null);
        }
        finally {
            logger.detachAppender(appender);
        }

        List<ILoggingEvent> events = appender.list;
        assertThat(events).isNotEmpty();
        assertThat(events.get(0).getLevel()).isEqualTo(Level.INFO);
        assertThat(events.get(0).getFormattedMessage()).contains("GET", "/hello", "name=ace");
    }

    @Test
    void shouldSkipLoggingWhenDisabled() throws Exception {
        properties.setEnabled(false);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/hello");
        MockHttpServletResponse response = new MockHttpServletResponse();

        Logger logger = (Logger) LoggerFactory.getLogger(RequestLogInterceptor.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try {
            interceptor.preHandle(request, response, new Object());
            interceptor.afterCompletion(request, response, new Object(), null);
        }
        finally {
            logger.detachAppender(appender);
        }

        assertThat(appender.list).isEmpty();
    }
}
