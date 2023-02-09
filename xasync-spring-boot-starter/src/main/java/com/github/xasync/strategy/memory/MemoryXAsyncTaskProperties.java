package com.github.xasync.strategy.memory;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.github.xasync.strategy.memory.MemoryXAsyncTaskConfiguration.X_ASYNC_MEMORY_STRATEGY_CONFIG;

/**
 * @author X1993
 * @date 2022/8/10
 * @description
 */
@Data
@ConfigurationProperties(X_ASYNC_MEMORY_STRATEGY_CONFIG)
@ConditionalOnBean(MemoryXAsyncTaskManager.class)
public class MemoryXAsyncTaskProperties {

    /**
     * 异步任务提交后延迟执行时间，单位毫秒
     */
    private long initialDelayMS = 100L;

    /**
     * 异步重试间隔，单位毫秒
     */
    private long periodMS = 5L * 60 * 1000;

    /**
     * 异步任务执行线程池核心线程数
     */
    private int corePoolSize = 3;

    /**
     * 最大执行次数
     */
    private int maxExecuteCount = 3;

}
