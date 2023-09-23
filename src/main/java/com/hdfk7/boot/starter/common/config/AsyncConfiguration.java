package com.hdfk7.boot.starter.common.config;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ExecutorConfigurationSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
public class AsyncConfiguration implements DisposableBean {
    @Bean
    @Primary
    public TaskExecutor shortTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int core = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(core);
        executor.setMaxPoolSize(core * 2 + 1);
        executor.setKeepAliveSeconds(60);
        executor.setAwaitTerminationMillis(1000L * 60);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("short-task-executor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    @Bean
    public TaskExecutor longTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int core = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(core);
        executor.setMaxPoolSize(core * 10 + 1);
        executor.setKeepAliveSeconds(60);
        executor.setAwaitTerminationMillis(1000L * 60);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("long-task-executor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    @Override
    public void destroy() throws Exception {
        ThreadPoolTaskExecutor shortTaskExecutor = SpringUtil.getBean("shortTaskExecutor", ThreadPoolTaskExecutor.class);
        Optional.ofNullable(shortTaskExecutor).ifPresent(ExecutorConfigurationSupport::destroy);

        ThreadPoolTaskExecutor longTaskExecutor = SpringUtil.getBean("longTaskExecutor", ThreadPoolTaskExecutor.class);
        Optional.ofNullable(longTaskExecutor).ifPresent(ExecutorConfigurationSupport::destroy);
    }
}
