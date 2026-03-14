package com.restaurant.authservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@ConfigurationProperties(prefix = "app.auth")
@Data
@Configuration
public class AuthServiceProperties {

    private Jwt jwt;
    private Blacklist blacklist;

    @Data
    public static class Jwt {
        private String secret;
        private long accessTokenExpiration;
        private long refreshTokenExpiration;
    }

    @Data
    public static class Blacklist {
        private String prefix;
    }
}
