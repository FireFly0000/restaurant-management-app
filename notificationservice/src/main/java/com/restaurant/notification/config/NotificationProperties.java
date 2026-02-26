package com.restaurant.notification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("app.notification")
public class NotificationProperties {
    private Kafka kafka;
    private Cache cache;
    private Outbox outbox;

    @Data
    public static class Kafka {
        private Retry retry;
        private Consumer consumer;
        @Data
        public static class Retry {
            private int maxAttempts;
            private long intervalMs;
        }

        @Data
        public static class Consumer {
            private int concurency;
        }
    }

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
    }
}
