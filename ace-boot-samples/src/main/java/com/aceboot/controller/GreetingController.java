package com.aceboot.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aceboot.common.Result;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/greetings")
public class GreetingController {

    @PostMapping
    public Result<String> createGreeting(@Valid @RequestBody GreetingRequest request) {
        String template = "hello".equals(request.getType()) ? "Hello, %s!" : "Hi, %s!";
        return Result.success(String.format(template, request.getName()));
    }
}
