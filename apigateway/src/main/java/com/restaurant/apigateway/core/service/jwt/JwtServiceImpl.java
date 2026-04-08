package com.restaurant.apigateway.core.service.jwt;

import com.restaurant.apigateway.config.ApiGatewayProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;
import com.restaurant.commons.utils.StringUtils;

@Service
public class JwtServiceImpl implements IJwtService {

    private final SecretKey secretKey;
    private static final Logger _log = LoggerFactory.getLogger(JwtServiceImpl.class);

    public JwtServiceImpl(ApiGatewayProperties _properties){
        this.secretKey = getSecretKey(_properties.getJwt().getSecret());
    }

    @Override
    public boolean isValidToken(String token) {
        if(!isValidJwtTokenFormat(token)){
            System.out.println("INVALID TOKEN FORMAT");
            return false;
        }
        System.out.println("TOKEN EXPIRED");
        return !isTokenExpired(token);
    }

    @Override
    public <T> T extractClaim(String token, String key, Class<T> type) {
        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(key, type);
    }

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

    @Override
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public static boolean isValidJwtTokenFormat(String token) {
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
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

    private boolean isTokenExpired(String token){
        try{
            return extractClaim(token, Claims::getExpiration).before(new Date());
        }catch(Exception e){
            _log.error(e.getMessage(), e.getCause());
            return true;
        }
    }

    private SecretKey getSecretKey(String secret){
        byte[] encodedKey = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(encodedKey);
    }
}
