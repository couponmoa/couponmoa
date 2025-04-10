package com.couponmoa.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    // 쓰레드 풀 이름
    @Bean(name = "threadPoolTaskExecutor")
    public Executor getAsyncExecutor() {
        // 내 PC의 Processor 개수
        int processors = Runtime.getRuntime().availableProcessors();
        // TaskExecutor를 사용하여 비동기 작업을 스케줄링 (ThreadPoolTaskExecutor)
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 기본적으로 실행 대기 중인 스레드 개수
        executor.setCorePoolSize(processors);
        // 동시에 동작하는 최대 스레드 개수
        executor.setMaxPoolSize(processors * 2);
        // CorePool의 크기를 넘어서면 큐에 저장하는데, 그 큐의 최대 용량
        executor.setQueueCapacity(50); // 대기를 위한 Queue 크기
        executor.setKeepAliveSeconds(60);  // 스레드 재사용 시간
        executor.setThreadNamePrefix("AsyncExecutor-"); // 스레드 이름 prefix
        executor.initialize(); // ThreadPoolExecutor 생성

        return executor;
    }
}