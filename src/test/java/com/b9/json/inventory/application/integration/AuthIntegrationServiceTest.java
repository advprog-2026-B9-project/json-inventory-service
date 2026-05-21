package com.b9.json.inventory.application.integration;

import com.b9.json.inventory.application.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

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
    void getUserByUsername_ReturnsUserDto() throws Exception {
        UserDto mockUser = new UserDto("testuser", "Test User", "08123456789");
        String jsonResponse = objectMapper.writeValueAsString(mockUser);

        mockServer.expect(requestTo(endsWith("/auth/internal/user?username=testuser")))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        UserDto result = authIntegrationService.getUserByUsername("testuser");

        assertEquals("testuser", result.username());
        assertEquals("Test User", result.fullName());
        assertEquals("08123456789", result.phoneNumber());

        mockServer.verify();
    }

    @Test
    void getUserByUsername_WhenNotFound_ReturnsNull() {
        mockServer.expect(requestTo(endsWith("/auth/internal/user?username=unknownuser")))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        UserDto result = authIntegrationService.getUserByUsername("unknownuser");

        assertNull(result);
        mockServer.verify();
    }

    @Test
    void getUserByUsername_WhenServerError_ReturnsNull() {
        mockServer.expect(requestTo(endsWith("/auth/internal/user?username=erroruser")))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        UserDto result = authIntegrationService.getUserByUsername("erroruser");

        assertNull(result);
        mockServer.verify();
    }
}