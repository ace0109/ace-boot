package com.aceboot.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VersionedGreetingController.class)
class VersionedGreetingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldDispatchToVersionOne() throws Exception {
        mockMvc.perform(get("/api/v1/greetings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Hello from v1"));
    }

    @Test
    void shouldDispatchToVersionTwo() throws Exception {
        mockMvc.perform(get("/api/v2/greetings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Hi from v2"));
    }

    @Test
    void shouldFallbackToDefaultWhenNoVersion() throws Exception {
        mockMvc.perform(get("/api/greetings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Default greeting"));
    }
}
