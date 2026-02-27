package com.restaurant.apigateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.api-gateway")
public class ApiGatewayProperties {
    private Jwt jwt;
    private Redis redis;
    private Blacklist blacklist;

    @Data
    public static class Blacklist {
        private String prefix;
    }

    @Data
    public static class Jwt {
        private String secret;
        private String accessTokenExpiration;
        private String refreshTokenExpiration;
    }

    @Data
    public static class Redis {
        private String host;
        private int port;
    }
}
