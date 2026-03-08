package com.restaurant.authservice.core.service.auth;

import com.restaurant.authservice.config.AuthServiceProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService implements IJwtService{
    private final SecretKey _secretKey;
    private final long _accessTokenExpiration;   // e.g. 900000 = 15 minutes
    private final long _refreshTokenExpiration;  // e.g. 604800000 = 7 days

    public JwtService(AuthServiceProperties properties){
        this._secretKey = getSecretKey(properties.getSecret());
        this._accessTokenExpiration = properties.getAccessTokenExpiration();
        this._refreshTokenExpiration = properties.getRefreshTokenExpiration();
    }

    @Override
    public String generateAccessToken(String userId, String email, String userType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("userType", userType);

        return Jwts.builder()
                .subject(userId)
                .claims(claims)
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

    private SecretKey getSecretKey(String secret){
        byte[] encodedKey = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(encodedKey);
    }
}
