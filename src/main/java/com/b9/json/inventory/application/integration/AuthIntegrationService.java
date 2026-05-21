package com.b9.json.inventory.application.integration;

import com.b9.json.inventory.application.dto.UserDto;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class AuthIntegrationService {

    private final RestClient restClient;

    public AuthIntegrationService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("${AUTH_SERVICE_URL}").build();
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