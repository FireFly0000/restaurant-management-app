package com.restaurant.authservice.core.service.jwt;

public interface IJwtService {
    String generateAccessToken(String userId, String email, String userType);
    String generateRefreshToken(String userId);
    boolean isValidToken(String token);
    String extractSubject(String token);  // extract userId
    <T> T  extractClaim(String token, String key, Class<T> type);  // extract email, userType
}
