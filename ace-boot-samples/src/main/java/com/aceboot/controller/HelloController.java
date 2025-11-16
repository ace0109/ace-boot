package com.aceboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aceboot.common.Result;
import com.aceboot.common.exception.BusinessException;

@RestController
public class HelloController {

    @GetMapping("/")
    public Result<String> hello() {
        return Result.success("🚀 Welcome to Ace Boot!");
    }

    @GetMapping("/simulate-error")
    public Result<Void> simulateError() {
        throw BusinessException.of("演示异常");
    }
}
