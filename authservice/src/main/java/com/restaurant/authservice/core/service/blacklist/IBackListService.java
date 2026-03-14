package com.restaurant.authservice.core.service.blacklist;

public interface IBackListService {
    boolean isBlacklisted(String token);
    void blacklistToken(String token);
}
