package com.b9.json.inventory.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = SecurityConfigTest.DummyController.class,
        properties = "FRONTEND_URL=http://frontend-palsu-untuk-test.lokal"
)
@Import({SecurityConfig.class, SecurityConfigTest.DummyController.class})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

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
    void corsConfigurationSource_ContainsCorrectValues() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test");
        CorsConfiguration config = corsConfigurationSource.getCorsConfiguration(request);

        assertNotNull(config);

        assertEquals(List.of("http://localhost:3000", "http://frontend-palsu-untuk-test.lokal"), config.getAllowedOrigins());

        assertEquals(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"), config.getAllowedMethods());
        assertEquals(List.of("*"), config.getAllowedHeaders());
        assertTrue(config.getAllowCredentials());
    }

    @Test
    void corsPreflight_ValidOrigin_ReturnsOk() throws Exception {
        mockMvc.perform(options("/test")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    void corsPreflight_InvalidOrigin_ReturnsForbidden() throws Exception {
        mockMvc.perform(options("/test")
                        .header("Origin", "http://malicious.com")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden());
    }

    @Test
    void request_WithoutCsrf_IsAllowed() throws Exception {
        mockMvc.perform(post("/test")
                        .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk());
    }

    @Test
    void request_PermitAll_IsAllowed() throws Exception {
        mockMvc.perform(get("/test"))
                .andExpect(status().isOk());
    }
}