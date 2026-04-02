package com.restaurant.authservice.core.service.jwt;

import com.restaurant.authservice.config.AuthServiceProperties;
import com.restaurant.commons.utils.StringUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements IJwtService {
    private final SecretKey _secretKey;
    private final long _accessTokenExpiration;   // e.g. 900000 = 15 minutes
    private final long _refreshTokenExpiration;  // e.g. 604800000 = 7 days
    private final long _verifyAccountTokenExpiration; //15 mins
    private static final Logger _log = LoggerFactory.getLogger(JwtServiceImpl.class);

    public JwtServiceImpl(AuthServiceProperties properties){
        this._secretKey = getSecretKey(properties.getJwt().getSecret());
        this._accessTokenExpiration = properties.getJwt().getAccessTokenExpiration();
        this._refreshTokenExpiration = properties.getJwt().getRefreshTokenExpiration();
        this._verifyAccountTokenExpiration = properties.getJwt().getVerifyAccountTokenExpiration();
    }

    @Override
    public String generateAccessToken(String userId, Map<String, Object> extraClaims) {

        return Jwts.builder()
                .subject(userId)
                .claims(extraClaims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + _accessTokenExpiration))
                .signWith(_secretKey)
                .compact();
    }

    @Override
    public String generateRefreshToken(String userId) {
        return Jwts.builder()
                .subject(userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + _refreshTokenExpiration))
                .signWith(_secretKey)
                .compact();
    }

    @Override
    public String generateRefreshToken(String userId, Map<String, Object> extraClaims) {
        return Jwts.builder()
                .subject(userId)
                .claims(extraClaims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + _refreshTokenExpiration))
                .signWith(_secretKey)
                .compact();
    }

    @Override
    public String generateVerifyAccountToken(String userId, Map<String, Object> extraClaims) {
        return Jwts.builder()
                .subject(userId)
                .claims(extraClaims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + _verifyAccountTokenExpiration ))
                .signWith(_secretKey)
                .compact();
    }

    @Override
    public boolean isValidToken(String token) {
        if(!isValidJwtTokenFormat(token)){
            return false;
        }
        return !isTokenExpired(token);
    }

    @Override
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String extractSubjectIgnoreExpiry(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (ExpiredJwtException e) {
            _log.error(e.getMessage(), e.getCause());
            return e.getClaims().getSubject();
        }
    }

    @Override
    public <T> T extractClaim(String token, String key, Class<T> type) {
        return Jwts
                .parser()
                .verifyWith(_secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(key, type);
    }

    // Add to JwtServiceImpl
    @Override
    public <T> T extractClaimIgnoreExpiry(String token, String key, Class<T> type) {
        try {
            return extractClaims(token).get(key, type);
        } catch (ExpiredJwtException e) {
            _log.error(e.getMessage(), e.getCause());
            return e.getClaims().get(key, type);
        }
    }

    @Override
    public long getTokenTtlSeconds(String token){
        try {
            Date expiration = extractClaim(token, Claims::getExpiration);
            long now = System.currentTimeMillis();
            long ttlMillis = expiration.getTime() - now;
    @Override
    public long getTokenTtlSeconds(String token){
        try {
            Date expiration = extractClaim(token, Claims::getExpiration);
            long now = System.currentTimeMillis();
            long ttlMillis = expiration.getTime() - now;

            long ttlSeconds = ttlMillis / 1000;
            return Math.max(ttlSeconds, 0);
        } catch (Exception e) {
            return 0;
        }
    }


            long ttlSeconds = ttlMillis / 1000;
            return Math.max(ttlSeconds, 0);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public boolean isValidTokenIgnoreExpiry(String token) {
        if (!isValidJwtTokenFormat(token)) {
            return false;
        }
        try {
            extractClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            _log.error(e.getMessage(), e.getCause());
            return false;
        }
    }

    @Override
    public boolean isTokenExpired(String token){
        try{
            return extractClaim(token, Claims::getExpiration).before(new Date());
        }catch(Exception e){
            _log.error(e.getMessage(), e.getCause());
            return true;
        }
    }

    private static boolean isValidJwtTokenFormat(String token) {
        boolean isValid = StringUtils.isStringNotEmpty(token) && token.split("\\.").length == 3;

        if(isValid){
            _log.info("Token is valid");
        }
        else{
            _log.warn("Invalid JWT Token received");
        }

        return isValid;
    }

    private Claims extractClaims(String token){
        return Jwts
                .parser()
                .verifyWith(_secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

    private SecretKey getSecretKey(String secret){
        byte[] encodedKey = Decoders.BASE64.decode(secret);
        _log.info("my secret: {}", encodedKey);
        return Keys.hmacShaKeyFor(encodedKey);
    }
}
