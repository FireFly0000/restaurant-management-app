package com.restaurant.businessservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.business")
@Data
public class BusinessProperties {
    private Cloudflare cloudflare;
    private Outbox outbox;

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
    public static class Cloudflare {
        private R2 r2;

        @Data
        public static class R2 {
            private String publicBucket;
            private String privateBucket;
        }
    }
}
