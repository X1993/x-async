package com.github.xasync;

import com.github.xasync.aop.XAsyncTaskAdvisor;
import com.github.xasync.exception.handler.DoNothingTaskExecuteExceptionHandler;
import com.github.xasync.exception.handler.TaskExecuteExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author X1993
 * @date 2022/8/8
 * @description
 */
@EnableConfigurationProperties(XAsyncTaskProperties.class)
@Configuration
public class XAsyncTaskConfiguration {

    @ConditionalOnMissingBean(TaskExecuteExceptionHandler.class)
    @Bean
    public DoNothingTaskExecuteExceptionHandler doNothingTaskExecuteExceptionHandler(){
        return new DoNothingTaskExecuteExceptionHandler();
    }

    @Bean
    public XAsyncTaskExecutorImpl xAsyncTaskExecutorImpl()
    {
        return new XAsyncTaskExecutorImpl();
    }

    @Bean
    public XAsyncTaskAdvisor xAsyncTaskAdvisor(){
        return new XAsyncTaskAdvisor();
    }

}
