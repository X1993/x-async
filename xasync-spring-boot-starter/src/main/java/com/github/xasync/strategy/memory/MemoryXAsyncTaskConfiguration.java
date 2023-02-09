package com.github.xasync.strategy.memory;

import com.github.xasync.XAsyncTaskExecutor;
import com.github.xasync.XAsyncContent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import static com.github.xasync.strategy.memory.MemoryXAsyncTaskConfiguration.MEMORY;

/**
 * @author X1993
 * @date 2022/8/10
 * @description
 */
@EnableConfigurationProperties(MemoryXAsyncTaskProperties.class)
@ConditionalOnProperty(value = XAsyncContent.X_ASYNC_STRATEGY ,havingValue = MEMORY ,matchIfMissing = true)
@Configuration
public class MemoryXAsyncTaskConfiguration {

    public static final String MEMORY = "memory";

    public static final String X_ASYNC_MEMORY_STRATEGY = XAsyncContent.X_ASYNC_STRATEGY + "-" + MEMORY;

    public static final String X_ASYNC_MEMORY_STRATEGY_CONFIG = X_ASYNC_MEMORY_STRATEGY +  ".config";

    @Bean
    public MemoryXAsyncTaskManager memoryXAsyncTaskManager(XAsyncTaskExecutor xAsyncTaskExecutor,
                                                           MemoryXAsyncTaskProperties properties)
    {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(properties.getCorePoolSize(), new ThreadFactory() {

            AtomicInteger atomicInteger = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r ,"XAsync-Task-Executor-" + atomicInteger.getAndIncrement());
            }

        });

        return new MemoryXAsyncTaskManager(xAsyncTaskExecutor ,scheduledThreadPoolExecutor ,properties);
    }

}
