package com.b9.json.inventory.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SecurityConfigTest.DummyController.class)
@Import({SecurityConfig.class, SecurityConfigTest.DummyController.class})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @RestController
    public static class DummyController {
        @GetMapping("/test")
        public String getTest() {
            return "OK";
        }

        @PostMapping("/test")
        public String postTest() {
            return "OK";
        }
    }

    @Test
    void request_WithoutCsrf_IsAllowed() throws Exception {
        mockMvc.perform(post("/test"))
                .andExpect(status().isOk());
    }

    @Test
    void request_PermitAll_IsAllowed() throws Exception {
        mockMvc.perform(get("/test"))
                .andExpect(status().isOk());
    }
}