package com.github.xasync;

import com.github.xasync.aop.XAsyncTaskAdvisor;
import com.github.xasync.exception.handler.DoNothingTaskExecuteExceptionHandler;
import com.github.xasync.exception.handler.TaskExecuteExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author X1993
 * @date 2022/8/8
 * @description
 */
@Configuration
public class XAsyncTaskConfiguration {

    @Bean
    public XAsyncTaskExecutorImpl xAsyncTaskExecutorImpl()
    {
        return new XAsyncTaskExecutorImpl();
    }

    @ConditionalOnMissingBean(TaskExecuteExceptionHandler.class)
    @Bean
    public DoNothingTaskExecuteExceptionHandler doNothingTaskExecuteExceptionHandler(){
        return new DoNothingTaskExecuteExceptionHandler();
    }

    @Bean
    public XAsyncTaskAdvisor xAsyncTaskAdvisor(){
        return new XAsyncTaskAdvisor();
    }

}
