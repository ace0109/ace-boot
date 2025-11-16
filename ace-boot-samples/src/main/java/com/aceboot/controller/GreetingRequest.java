package com.aceboot.controller;

import com.aceboot.starter.web.validation.AllowedValues;

import jakarta.validation.constraints.NotBlank;

public class GreetingRequest {

    @NotBlank(message = "昵称不能为空")
    private String name;

    @NotBlank(message = "问候类型不能为空")
    @AllowedValues(value = {"hi", "hello"}, message = "问候类型仅支持 hi/hello")
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
