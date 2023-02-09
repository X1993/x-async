package com.github.xasync.strategy.memory;

import com.github.xasync.XAsyncTaskExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import static com.github.xasync.XAsyncContent.X_ASYNC_STRATEGY;

/**
 * @author X1993
 * @date 2022/8/10
 * @description
 */
@Import(MemoryXAsyncTaskConfiguration.BeanRegister.class)
@EnableConfigurationProperties(MemoryXAsyncTaskProperties.class)
@Configuration
public class MemoryXAsyncTaskConfiguration {

    public static final String MEMORY = "memory";

    public static final String X_ASYNC_MEMORY_STRATEGY = X_ASYNC_STRATEGY + "-" + MEMORY;

    public static final String X_ASYNC_MEMORY_STRATEGY_ENABLE = X_ASYNC_MEMORY_STRATEGY + ".enable";

    public static final String X_ASYNC_MEMORY_STRATEGY_CONFIG = X_ASYNC_MEMORY_STRATEGY +  ".config";

    @ConditionalOnProperty(value = X_ASYNC_MEMORY_STRATEGY_ENABLE ,havingValue = "true" ,matchIfMissing = true)
    @Configuration
    static class BeanRegister{

        @Bean
        public MemoryXAsyncTaskManager memoryXAsyncTaskManager(XAsyncTaskExecutor xAsyncTaskExecutor,
                                                               MemoryXAsyncTaskProperties memoryXAsyncTaskProperties)
        {
            ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(
                    memoryXAsyncTaskProperties.getCorePoolSize(), new ThreadFactory() {

                AtomicInteger atomicInteger = new AtomicInteger();

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r ,"XAsync-Task-Memory-Executor-" + atomicInteger.getAndIncrement());
                }

            });

            return new MemoryXAsyncTaskManager(xAsyncTaskExecutor ,scheduledThreadPoolExecutor ,memoryXAsyncTaskProperties);
        }

    }

}
