package com.github.xasync.strategy.rocketmq;

import com.github.xasync.XAsyncTaskExecutor;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static com.github.xasync.XAsyncContent.X_ASYNC_STRATEGY;
import static com.github.xasync.strategy.rocketmq.RocketMqXAsyncTaskConfiguration.ROCKETMQ;

/**
 * @author X1993
 * @date 2022/8/8
 * @description
 */
@ConditionalOnProperty(value = X_ASYNC_STRATEGY ,havingValue = ROCKETMQ)
@EnableConfigurationProperties(RocketMqXAsyncTaskProperties.class)
@Configuration
public class RocketMqXAsyncTaskConfiguration {

    //JAVA原生序列化必须使用ISO-8859-1
    public final static String SERIALIZABLE_CHARSET = "ISO-8859-1";

    public static final String ROCKETMQ = "rocketmq";

    public static final String X_ASYNC_ROCKET_STRATEGY = X_ASYNC_STRATEGY + "-" + ROCKETMQ;

    public static final String X_ASYNC_ROCKET_STRATEGY_CONFIG = X_ASYNC_ROCKET_STRATEGY +  ".config";

    @Bean
    public RocketMqXAsyncTaskStorage rocketMqXAsyncTaskStorage(RocketMQTemplate rocketMQTemplate){
        return new RocketMqXAsyncTaskStorage(rocketMQTemplate);
    }

    @Bean
    public RocketMqXAsyncTaskTrigger rocketMqXAsyncTaskTrigger(XAsyncTaskExecutor xAsyncTaskExecutor){
        return new RocketMqXAsyncTaskTrigger(xAsyncTaskExecutor);
    }

}
