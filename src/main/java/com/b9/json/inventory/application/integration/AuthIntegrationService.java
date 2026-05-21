package com.b9.json.inventory.application.integration;

import com.b9.json.inventory.application.dto.UserDto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class AuthIntegrationService {

    private final RestClient restClient;

    public AuthIntegrationService(RestClient.Builder restClientBuilder, @Value("${AUTH_SERVICE_URL:http://localhost:8083}") String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    public UserDto getUserByUsername(String username) {
        try {
            return restClient.get()
                    .uri("/auth/internal/user?username={username}", username)
                    .retrieve()
                    .body(UserDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}