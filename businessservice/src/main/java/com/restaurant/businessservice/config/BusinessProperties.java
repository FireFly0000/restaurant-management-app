package com.restaurant.businessservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.business")
@Data
public class BusinessProperties {
    private Cloudflare cloudflare;

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
