package com.restaurant.businessservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableScheduling
public class AsyncConfig {
    private final BusinessProperties _businessProperties;

    public AsyncConfig(BusinessProperties businessProperties) {
        this._businessProperties = businessProperties;
    }

    @Bean("businessOutboxExecutor")
    public ThreadPoolTaskExecutor businessOutboxExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(_businessProperties.getOutbox().getCorePoolSize());
        executor.setMaxPoolSize(_businessProperties.getOutbox().getMaxPoolSize());
        executor.setQueueCapacity(_businessProperties.getOutbox().getQueueCapacity());
        executor.setThreadNamePrefix(_businessProperties.getOutbox().getThreadNamePrefix());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
