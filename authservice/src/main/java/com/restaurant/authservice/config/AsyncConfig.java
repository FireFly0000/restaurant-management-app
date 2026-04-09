package com.restaurant.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {
    private final AuthServiceProperties _authProperties;

    public AsyncConfig(
            AuthServiceProperties userProperties
    ){
        this._authProperties = userProperties;
    }

    @Bean("authOutboxExecutor")
    public ThreadPoolTaskExecutor userOutboxExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(_authProperties.getOutbox().getCorePoolSize());
        executor.setMaxPoolSize(_authProperties.getOutbox().getMaxPoolSize());
        executor.setQueueCapacity(_authProperties.getOutbox().getQueueCapacity());
        executor.setThreadNamePrefix(_authProperties.getOutbox().getThreadNamePrefix());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}

