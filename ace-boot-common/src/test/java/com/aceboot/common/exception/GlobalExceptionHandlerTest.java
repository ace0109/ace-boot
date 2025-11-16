package com.aceboot.common.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aceboot.common.Result;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new FailingController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldTranslateBusinessException() throws Exception {
        mockMvc.perform(get("/biz").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("BIZ"))
                .andExpect(jsonPath("$.message").value("boom"));
    }

    @Test
    void shouldHandleUnexpectedException() throws Exception {
        mockMvc.perform(get("/unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"));
    }

    @RestController
    static class FailingController {
        @GetMapping("/biz")
        public Result<Void> biz() {
            throw new BusinessException("BIZ", "boom");
        }

        @GetMapping("/unknown")
        public Result<Void> unknown() {
            throw new IllegalStateException("nope");
        }
    }
}
