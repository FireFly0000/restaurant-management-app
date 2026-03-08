package com.restaurant.authservice.core.service.auth;

public interface IJwtService {
    String generateAccessToken(String userId, String email, String userType);
    String generateRefreshToken(String userId);
}
