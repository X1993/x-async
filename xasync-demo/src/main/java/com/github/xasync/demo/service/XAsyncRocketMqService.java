package com.github.xasync.demo.service;

import com.github.xasync.aop.XAsync;

import static com.github.xasync.strategy.rocketmq.RocketMqXAsyncTaskConfiguration.ROCKETMQ;

/**
 * @author X1993
 * @date 2023/2/8
 * @description
 */
public interface XAsyncRocketMqService {

    @XAsync(strategy = ROCKETMQ)
    void testXAsyncRocketMq0(String taskId , int executeCount);

    void testXAsyncRocketMq1(String taskId , int executeCount);

    void testXAsyncRocketMq2(String taskId , int executeCount);

    @XAsync(trySync = false ,strategy = ROCKETMQ)
    void testXAsyncRocketMq3(String taskId, int executeCount, long timeConsumerMS);

    void testXAsyncRocketMq4(String taskId, int executeCount);

}
