package com.aceboot.starter.web;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.mvc.condition.RequestCondition;

class ApiVersionRequestCondition implements RequestCondition<ApiVersionRequestCondition> {

    private static final Pattern VERSION_PATTERN = Pattern.compile("/v(\\d+)(/|$)");

    private final Set<Integer> versions;

    ApiVersionRequestCondition(int... versions) {
        this.versions = new TreeSet<>((a, b) -> b - a);
        Arrays.stream(versions).forEach(this.versions::add);
    }

    @Override
    public ApiVersionRequestCondition combine(ApiVersionRequestCondition other) {
        if (other == null) {
            return this;
        }
        Set<Integer> combined = new TreeSet<>((a, b) -> b - a);
        combined.addAll(this.versions);
        combined.addAll(other.versions);
        return new ApiVersionRequestCondition(combined.stream().mapToInt(Integer::intValue).toArray());
    }

    @Override
    public ApiVersionRequestCondition getMatchingCondition(HttpServletRequest request) {
        Integer version = extractVersion(request.getRequestURI());
        if (version == null) {
            version = 1; // 默认为 v1，保持兼容
        }
        return versions.contains(version) ? this : null;
    }

    @Override
    public int compareTo(ApiVersionRequestCondition other, HttpServletRequest request) {
        Integer thisVersion = versions.stream().findFirst().orElse(1);
        Integer otherVersion = other.versions.stream().findFirst().orElse(1);
        return otherVersion.compareTo(thisVersion);
    }

    private Integer extractVersion(String path) {
        Matcher matcher = VERSION_PATTERN.matcher(path);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return null;
    }
}
