package com.restaurant.apigateway.core.service.blacklist;

import com.restaurant.apigateway.config.ApiGatewayProperties;
import com.restaurant.apigateway.core.service.jwt.JwtServiceImpl;
import com.restaurant.commons.core.interfaces.ICacheService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class BlackListServiceImpl implements IBackListService {
    private final String blacklistPrefix;
    private final ICacheService cacheService;
    private final JwtServiceImpl jwtService;

    public BlackListServiceImpl(
            @Qualifier("redisService") ICacheService cacheService,
            ApiGatewayProperties _properties,
            JwtServiceImpl jwtService
    ) {
        this.blacklistPrefix = _properties.getBlacklist().getPrefix();
        this.cacheService = cacheService;
        this.jwtService = jwtService;
    }

    @Override
    public boolean isBlacklisted(String token) {
        return cacheService.hasKey(this.blacklistPrefix + token);
    }

    @Override
    public void blacklistToken(String token) {
        cacheService.setWithTTL(
                this.blacklistPrefix + token,
                true,
                jwtService.getTokenTtlSeconds(token),
                TimeUnit.SECONDS
        );
    }
}
