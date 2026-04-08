package com.restaurant.apigateway.core.service.jwt;

public interface IJwtService {
    boolean isValidToken(String token);
    <T> T  extractClaim(String token, String key, Class<T> type);
    long getTokenTtlSeconds(String token);
    String extractSubject(String token);
}
