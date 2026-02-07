package com.restaurant.apigateway.core.service.blacklist;

import com.restaurant.apigateway.core.service.redis.IRedisService;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class BlackListServiceImpl implements IBackListService {
    private static final String PREFIX = "blacklist:jwt:";

    private final IRedisService redisService;

    public BlackListServiceImpl(IRedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public boolean isBlacklisted(String token) {
        return redisService.hasKey(PREFIX + token);
    }

    @Override
    public void blacklistToken(String token, long ttlSeconds) {
        redisService.setWithTTL(
                PREFIX + token,
                true,
                ttlSeconds,
                TimeUnit.SECONDS
        );
    }
}
