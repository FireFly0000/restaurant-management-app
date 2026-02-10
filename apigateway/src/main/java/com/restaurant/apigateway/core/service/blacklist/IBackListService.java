package com.restaurant.apigateway.core.service.blacklist;

public interface IBackListService {
    boolean isBlacklisted(String token);
    void blacklistToken(String token);
}
