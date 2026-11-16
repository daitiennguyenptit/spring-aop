package com.example.aop.audit.config;

import com.example.aop.audit.context.RequestContext;
import java.util.Map;
import java.util.concurrent.Executor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * ThreadLocal KHONG tu dong sang thread khac. Voi @Async ta dung TaskDecorator
 * de copy requestId (+ MDC) tu thread goi sang thread worker, va don dep sau khi chay xong.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setThreadNamePrefix("async-");
        executor.setTaskDecorator(requestContextTaskDecorator());
        executor.initialize();
        return executor;
    }

    @Bean
    public TaskDecorator requestContextTaskDecorator() {
        return runnable -> {
            // Chay tren thread GOI: chup lai context
            String requestId = RequestContext.getRequestId();
            Map<String, String> mdc = MDC.getCopyOfContextMap();
            return () -> {
                // Chay tren thread WORKER: khoi phuc context
                if (mdc != null) {
                    MDC.setContextMap(mdc);
                }
                if (requestId != null) {
                    RequestContext.setRequestId(requestId);
                }
                try {
                    runnable.run();
                } finally {
                    RequestContext.clear();
                    MDC.clear();
                }
            };
        };
    }
}
