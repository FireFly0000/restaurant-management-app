package com.restaurant.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {
    private final UserProperties _userProperties;

    public AsyncConfig(
            UserProperties userProperties
    ){
        this._userProperties = userProperties;
    }

    @Bean("userOutboxExecutor")
    public ThreadPoolTaskExecutor userOutboxExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(_userProperties.getOutbox().getCorePoolSize());
        executor.setMaxPoolSize(_userProperties.getOutbox().getMaxPoolSize());
        executor.setQueueCapacity(_userProperties.getOutbox().getQueueCapacity());
        executor.setThreadNamePrefix(_userProperties.getOutbox().getThreadNamePrefix());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }

    @Bean("userInboxExecutor")
    public ThreadPoolTaskExecutor userInboxExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(_userProperties.getInbox().getCorePoolSize());
        executor.setMaxPoolSize(_userProperties.getInbox().getMaxPoolSize());
        executor.setQueueCapacity(_userProperties.getInbox().getQueueCapacity());
        executor.setThreadNamePrefix(_userProperties.getInbox().getThreadNamePrefix());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
