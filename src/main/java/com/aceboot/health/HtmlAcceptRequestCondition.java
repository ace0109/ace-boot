package com.aceboot.health;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import java.util.Locale;

/**
 * 自定义请求条件：只有 Accept 包含 text/html 时才匹配。
 */
public class HtmlAcceptRequestCondition implements RequestCondition<HtmlAcceptRequestCondition> {

    @Override
    public HtmlAcceptRequestCondition combine(HtmlAcceptRequestCondition other) {
        return this;
    }

    @Override
    public HtmlAcceptRequestCondition getMatchingCondition(HttpServletRequest request) {
        String accept = request.getHeader(HttpHeaders.ACCEPT);
        if (accept != null && accept.toLowerCase(Locale.ROOT).contains(MediaType.TEXT_HTML_VALUE)) {
            return this;
        }
        return null;
    }

    @Override
    public int compareTo(HtmlAcceptRequestCondition other, HttpServletRequest request) {
        return 0;
    }
}
