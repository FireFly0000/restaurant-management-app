package com.restaurant.authservice.core.service.jwt;

import java.util.Map;
import java.util.Optional;

public interface IJwtService {
    String generateAccessToken(String userId, Map<String, Object> extraClaims);
    String generateRefreshToken(String userId, Map<String, Object> extraClaims);
    String generateRefreshToken(String userId);
    boolean isValidToken(String token);
    String extractSubject(String token);  // extract userId
    <T> T  extractClaim(String token, String key, Class<T> type);  // extract email, userType
    long getTokenTtlSeconds(String token);
}
