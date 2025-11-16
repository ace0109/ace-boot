package com.aceboot.controller;

import com.aceboot.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public Result<String> hello() {
        return Result.success("🚀 Welcome to Ace Boot!");
    }
}
