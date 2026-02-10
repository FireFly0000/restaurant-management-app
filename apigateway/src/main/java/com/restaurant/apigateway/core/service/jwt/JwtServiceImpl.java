package com.restaurant.apigateway.core.service.jwt;

import com.restaurant.apigateway.config.ApiGatewayProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements IJwtService {

    private final SecretKey secretKey;

    public JwtServiceImpl(ApiGatewayProperties _properties){
        this.secretKey = getSecretKey(_properties.getJwt().getSecret());
    }

    @Override
    public boolean isValidToken(String token) {
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
    public long getTokenTTLMillis(String token){
        try {
            Date expiration = extractClaim(token, Claims::getExpiration);
            long now = System.currentTimeMillis();
            long ttl = expiration.getTime() - now;

            return Math.max(ttl, 0);
        } catch (Exception e) {
            return 0;
        }
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
            return !extractClaim(token, Claims::getExpiration).before(new Date());
        }catch(Exception e){
            return true;
        }
    }

    private SecretKey getSecretKey(String secret){
        byte[] encodedKey = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(encodedKey);
    }
}
