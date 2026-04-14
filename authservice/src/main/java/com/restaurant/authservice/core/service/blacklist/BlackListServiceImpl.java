package com.restaurant.authservice.core.service.blacklist;

import com.restaurant.authservice.config.AuthServiceProperties;
import com.restaurant.authservice.core.service.jwt.IJwtService;
import com.restaurant.authservice.core.service.jwt.JwtServiceImpl;
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
    private final String _blacklistPrefix;
    private final ICacheService _cacheService;
    private final IJwtService _jwtService;

    private final Logger _log = LoggerFactory.getLogger(BlackListServiceImpl.class);

    public BlackListServiceImpl(
            @Qualifier("redisService") ICacheService cacheService,
            AuthServiceProperties _properties,
            JwtServiceImpl jwtService
    ) {
        this._blacklistPrefix = _properties.getBlacklist().getPrefix();
        this._cacheService = cacheService;
        this._jwtService = jwtService;
    }

    @Override
    public boolean isBlacklisted(String token) {
        return _cacheService.hasKey(this._blacklistPrefix + token);
    }

    @Override
    public void blacklistToken(String token) {
        _cacheService.setWithTTL(
                this._blacklistPrefix + token,
                true,
                _jwtService.getTokenTtlSeconds(token),
                TimeUnit.SECONDS
        );

        UUID userId = _jwtService.extractClaim(token, Constant.USER_ID, UUID.class);
        long ttl = _jwtService.getTokenTtlSeconds(token);
        Date expiration = _jwtService.extractClaim(token, Constant.EXPIRATION, Date.class);

        _log.info(
                "JWT added to blacklist. userId={}, expiresAt={}, ttlSeconds={}",
                userId,
                expiration,
                ttl
        );
    }
}
