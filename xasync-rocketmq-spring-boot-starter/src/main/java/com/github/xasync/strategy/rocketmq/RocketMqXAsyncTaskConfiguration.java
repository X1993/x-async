package com.github.xasync.strategy.rocketmq;

import com.github.xasync.XAsyncTaskExecutor;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import static com.github.xasync.XAsyncContent.X_ASYNC_STRATEGY;

/**
 * @author X1993
 * @date 2022/8/8
 * @description
 */
@Import(RocketMqXAsyncTaskConfiguration.BeanRegister.class)
@EnableConfigurationProperties(RocketMqXAsyncTaskProperties.class)
@Configuration
public class RocketMqXAsyncTaskConfiguration {

    public final static String SERIALIZABLE_CHARSET = "ISO-8859-1";

    public static final String ROCKETMQ = "rocketmq";

    public static final String X_ASYNC_ROCKET_STRATEGY = X_ASYNC_STRATEGY + "-" + ROCKETMQ;

    public static final String X_ASYNC_ROCKET_STRATEGY_ENABLE = X_ASYNC_ROCKET_STRATEGY +  ".enable";

    public static final String X_ASYNC_ROCKET_STRATEGY_CONFIG = X_ASYNC_ROCKET_STRATEGY +  ".config";

    @ConditionalOnProperty(value = X_ASYNC_ROCKET_STRATEGY_ENABLE ,havingValue = "true" ,matchIfMissing = true)
    @Configuration
    static class BeanRegister{

        @Bean
        public RocketMqXAsyncTaskStorage rocketMqXAsyncTaskStorage(RocketMQTemplate rocketMQTemplate ,
                                                                   RocketMqXAsyncTaskProperties rocketMqXAsyncTaskProperties)
        {
            return new RocketMqXAsyncTaskStorage(rocketMQTemplate ,rocketMqXAsyncTaskProperties);
        }

        @Bean
        public RocketMqXAsyncTaskTrigger rocketMqXAsyncTaskTrigger(XAsyncTaskExecutor xAsyncTaskExecutor){
            return new RocketMqXAsyncTaskTrigger(xAsyncTaskExecutor);
        }
    }

}
