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

    public final static String TOPIC_DEFAULT = "x-async-task-test0";

    public final static String TAG_DEFAULT = "all";

    public final static String CONSUMER_DEFAULT = "x-async-demo";

    public final static String TOPIC_REF = "${" + X_ASYNC_ROCKET_STRATEGY_CONFIG
            + ".topic:" + TOPIC_DEFAULT + "}";

    public final static String TAG_REF = "${" + X_ASYNC_ROCKET_STRATEGY_CONFIG
            + ".tag:" + TAG_DEFAULT + "}";

    public final static String CONSUMER_GROUP_REF = "${" + X_ASYNC_ROCKET_STRATEGY_CONFIG
            + ".consumer-group:" + CONSUMER_DEFAULT + "}";

    public String topic = TOPIC_DEFAULT;

    public String tag = TAG_DEFAULT;

    public String consumerGroup = CONSUMER_DEFAULT;

    public String getDestination(){
        return topic + ":" + tag;
    }

}
