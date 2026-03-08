package com.restaurant.authservice.core.service.auth.dto;

import lombok.Builder;

@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
}
