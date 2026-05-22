package com.b9.json.inventory.application.integration;

import com.b9.json.inventory.application.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.UUID;

import static org.hamcrest.Matchers.endsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class AuthIntegrationServiceTest {

    private AuthIntegrationService authIntegrationService;
    private MockRestServiceServer mockServer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        authIntegrationService = new AuthIntegrationService(builder, "http://localhost:8083");
    }

    @Test
    void getUserById_ReturnsUserDto() throws Exception {
        UUID userId = UUID.randomUUID();
        UserDto mockUser = new UserDto(userId, "testuser", "Test User", "08123456789");
        String jsonResponse = objectMapper.writeValueAsString(mockUser);

        mockServer.expect(requestTo(endsWith("/auth/internal/user?id=" + userId)))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        UserDto result = authIntegrationService.getUserById(userId);

        assertEquals(userId, result.id());
        assertEquals("testuser", result.username());

        mockServer.verify();
    }

    @Test
    void getUserById_WhenNotFound_ReturnsNull() {
        UUID userId = UUID.randomUUID();
        mockServer.expect(requestTo(endsWith("/auth/internal/user?id=" + userId)))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        UserDto result = authIntegrationService.getUserById(userId);

        assertNull(result);
        mockServer.verify();
    }

    @Test
    void getUserById_WhenServerError_ReturnsNull() {
        UUID userId = UUID.randomUUID();
        mockServer.expect(requestTo(endsWith("/auth/internal/user?id=" + userId)))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        UserDto result = authIntegrationService.getUserById(userId);

        assertNull(result);
        mockServer.verify();
    }
}