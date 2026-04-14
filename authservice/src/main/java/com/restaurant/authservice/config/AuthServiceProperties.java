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
    private String frontendBaseUrl;
    private Jwt jwt;
    private Blacklist blacklist;
    private Redis redis;
    private Outbox outbox;

    @Data
    public static class Jwt {
        private String secret;
        private long accessTokenExpiration;
        private long refreshTokenExpiration;
        private long verifyAccountTokenExpiration;
        private long resetPasswordTokenExpiration;
    }

    @Data
    public static class Blacklist {
        private String prefix;
    }

    @Data
    public static class Redis {
        private String host;
        private int port;
    }

    @Data
    public static class Outbox {
        private int corePoolSize;
        private int maxPoolSize;
        private int queueCapacity;
        private String threadNamePrefix;
        private int batchSize;
        private int maxRetries;
        private int leaseSeconds;
        private int batchTimeoutSeconds;
    }
}
