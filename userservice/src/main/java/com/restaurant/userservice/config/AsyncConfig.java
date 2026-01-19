package com.restaurant.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    private final UserProperties _userProperties;

    public AsyncConfig(
            UserProperties userProperties
    ){
        this._userProperties = userProperties;
    }

    @Bean("userOutboxExecutor")
    public Executor userOutboxExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(_userProperties.getOutbox().getCorePoolSize());
        executor.setMaxPoolSize(_userProperties.getOutbox().getMaxPoolSize());
        executor.setQueueCapacity(_userProperties.getOutbox().getQueueCapacity());
        executor.setThreadNamePrefix(_userProperties.getOutbox().getThreadNamePrefix());
        executor.initialize();
        return executor;
    }
}
