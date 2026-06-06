package com.litethinking.inventory.application.dto;

public record AuthResponse(
        String token,
        String email,
        String rol
) {}
