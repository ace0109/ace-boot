package com.aceboot.health;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthHtmlEndpoint.class)
class HealthHtmlEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HealthEndpoint healthEndpoint;

    @Test
    void shouldRenderHtmlHealthReport() throws Exception {
        Health health = Health.up()
                .withDetail("application", "ace-boot")
                .withDetail("version", "1.0.0-SNAPSHOT")
                .build();
        given(healthEndpoint.health()).willReturn(health);

        mockMvc.perform(get("/actuator/health").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(containsString("ACE-Boot 健康检查")))
                .andExpect(content().string(containsString("ace-boot")))
                .andExpect(content().string(containsString("1.0.0-SNAPSHOT")));
    }
}
