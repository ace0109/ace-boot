package com.aceboot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 健康检查端点集成测试
 *
 * @author ace-boot
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureMockMvc
class HealthCheckIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void actuatorHealth_shouldReturnUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.components.custom.status", is("UP")))
                .andExpect(jsonPath("$.components.custom.details.application", is("ace-boot")))
                .andExpect(jsonPath("$.components.custom.details.version", is("1.0.0-SNAPSHOT")));
    }

    @Test
    void actuatorHealth_shouldContainPingComponent() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.ping").exists())
                .andExpect(jsonPath("$.components.ping.status", is("UP")));
    }

    @Test
    void actuatorHealth_shouldContainCustomComponent() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.custom").exists())
                .andExpect(jsonPath("$.components.custom.details.description",
                        is("ACE-Boot service is healthy")));
    }

    @Test
    void actuatorHealth_shouldReturnHtml_whenAcceptIsTextHtml() throws Exception {
        mockMvc.perform(get("/actuator/health").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(containsString("ACE-Boot 健康检查")))
                .andExpect(content().string(containsString("health-report")));
    }
}
