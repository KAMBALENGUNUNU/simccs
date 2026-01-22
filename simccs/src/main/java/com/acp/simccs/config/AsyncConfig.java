package com.acp.simccs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // Start with 5 threads
        executor.setMaxPoolSize(10); // Scale up to 10
        executor.setQueueCapacity(25); // Queue buffer
        executor.setThreadNamePrefix("SimccsAsync-");
        executor.initialize();
        return executor;
    }
}