package com.restaurant.authservice.core.service.jwt;

import java.util.Map;
import java.util.Optional;

public interface IJwtService {
    String generateAccessToken(String userId, Map<String, Object> extraClaims);
    String generateRefreshToken(String userId, Map<String, Object> extraClaims);
    String generateVerifyAccountToken(String userId, Map<String, Object> extraClaims);
    String generateRefreshToken(String userId);
    String generateResetPasswordToken(String userId, Map<String, Object> extraClaims);
    boolean isValidToken(String token);
    boolean isValidTokenIgnoreExpiry(String token);
    boolean isTokenExpired(String token);
    String extractSubject(String token);  // extract userId
    String extractSubjectIgnoreExpiry(String token);
    <T> T  extractClaim(String token, String key, Class<T> type);  // extract email, userType
    <T> T extractClaimIgnoreExpiry(String token, String key, Class<T> type);
    long getTokenTtlSeconds(String token);
}
