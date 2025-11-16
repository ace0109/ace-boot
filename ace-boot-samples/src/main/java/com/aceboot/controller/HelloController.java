package com.aceboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aceboot.common.Result;

@RestController
public class HelloController {

    @GetMapping("/")
    public Result<String> hello() {
        return Result.success("🚀 Welcome to Ace Boot!");
    }
}
