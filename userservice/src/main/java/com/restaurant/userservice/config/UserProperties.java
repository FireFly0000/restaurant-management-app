package com.restaurant.userservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.user")
public class UserProperties {
    private Cache cache;
    private Outbox outbox;
    private Inbox inbox;

    @Data
    public static class Cache {
        private long ttlSeconds;
        private String prefix;
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

    @Data
    public static class Inbox {
        private int batchSize;
        private int maxRetries;
        private int leaseSeconds;
        private int corePoolSize;
        private int maxPoolSize;
        private int queueCapacity;
        private String threadNamePrefix;
    }
}
