package com.example.drawandguess.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import static com.example.drawandguess.config.Constants.SCHEDULER_POOL_SIZE;
import static com.example.drawandguess.config.Constants.SCHEDULER_THREAD_PREFIX;

@Configuration
public class SchedulerConfig {

    @Bean
    @Primary
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(SCHEDULER_POOL_SIZE);
        scheduler.setThreadNamePrefix(SCHEDULER_THREAD_PREFIX);
        scheduler.initialize();
        return scheduler;
    }
}
