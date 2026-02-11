package com.restaurant.apigateway.core.service.blacklist;

import com.restaurant.apigateway.config.ApiGatewayProperties;
import com.restaurant.apigateway.core.service.jwt.JwtServiceImpl;
import com.restaurant.commons.constant.Constant;
import com.restaurant.commons.core.interfaces.ICacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class BlackListServiceImpl implements IBackListService {
    private final String blacklistPrefix;
    private final ICacheService cacheService;
    private final JwtServiceImpl jwtService;

    private final Logger _log = LoggerFactory.getLogger(BlackListServiceImpl.class);

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

        UUID userId = jwtService.extractClaim(token, Constant.USER_ID, UUID.class);
        long ttl = jwtService.getTokenTtlSeconds(token);
        Date expiration = jwtService.extractClaim(token, Constant.EXPIRATION, Date.class);

        _log.info(
                "JWT added to blacklist. userId={}, expiresAt={}, ttlSeconds={}",
                userId,
                expiration,
                ttl
        );
    }
}
