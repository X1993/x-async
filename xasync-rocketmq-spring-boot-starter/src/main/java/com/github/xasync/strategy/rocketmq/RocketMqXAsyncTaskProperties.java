package com.github.xasync.strategy.rocketmq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import static com.github.xasync.strategy.rocketmq.RocketMqXAsyncTaskConfiguration.X_ASYNC_ROCKET_STRATEGY_CONFIG;

/**
 * @author X1993
 * @date 2022/6/16
 * @description
 */
@Data
@ConfigurationProperties(X_ASYNC_ROCKET_STRATEGY_CONFIG)
public class RocketMqXAsyncTaskProperties {

    public final static String TOPIC = "x-async-task-test0";

    public final static String TAG = "all";

    public final static String CONSUMER_GROUP = "common-extend-demo";

}
