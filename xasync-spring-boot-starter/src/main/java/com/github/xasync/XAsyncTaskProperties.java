package com.github.xasync;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.github.xasync.XAsyncContent.X_ASYNC_CONFIG;
import static com.github.xasync.strategy.memory.MemoryXAsyncTaskConfiguration.MEMORY;

/**
 * @author X1993
 * @date 2023/2/8
 * @description
 */
@Data
@ConfigurationProperties(X_ASYNC_CONFIG)
public class XAsyncTaskProperties {

    /**
     * 全局默认策略
     */
    private String defaultStrategy = MEMORY;

}
