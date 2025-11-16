package com.aceboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aceboot.common.Result;
import com.aceboot.starter.web.ApiVersion;

@RestController
@RequestMapping("/api")
public class VersionedGreetingController {

    @GetMapping("/v{version}/greetings")
    @ApiVersion(1)
    public Result<String> greetingV1(@PathVariable String version) {
        return Result.success("Hello from v" + version);
    }

    @GetMapping("/v{version}/greetings")
    @ApiVersion(2)
    public Result<String> greetingV2(@PathVariable String version) {
        return Result.success("Hi from v" + version);
    }

    @GetMapping("/greetings")
    public Result<String> defaultGreeting() {
        return Result.success("Default greeting");
    }
}
