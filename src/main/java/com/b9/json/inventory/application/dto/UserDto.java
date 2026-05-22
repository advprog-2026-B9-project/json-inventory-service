package com.b9.json.inventory.application.dto;

import java.util.UUID;

public record UserDto(
        UUID id,
        String username,
        String fullName,
        String phoneNumber
) {}