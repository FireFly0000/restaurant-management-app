package com.restaurant.storageservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.storage")
@Data
public class StorageProperties {
    private Kafka kafka;
    private Cache cache;
    private Outbox outbox;
    private Cloudflare cloudflare;

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

    @Data
    public static class Cloudflare {
        private R2 r2;

        @Data
        public static class R2 {
            private String accountId;
            private String accessKey;
            private String secretKey;
            private String url;
            private String publicBucket;
            private String privateBucket;
            private String objectPublicUrl;
        }
    }
}
