package com.github.xasync.demo.service;

import com.github.xasync.aop.XAsync;
import org.springframework.stereotype.Service;

import static com.github.xasync.strategy.rocketmq.RocketMqXAsyncTaskConfiguration.ROCKETMQ;

/**
 * @author X1993
 * @date 2023/2/8
 * @description
 */
@Service
public class XAsyncRocketMqServiceImpl implements XAsyncRocketMqService {

    @Override
    public void testXAsyncRocketMq0(String taskId , int executeCount)
    {
        XAsyncTestUtils.run(taskId ,executeCount);
    }

    @Override
    @XAsync(trySync = false ,strategy = ROCKETMQ)
    public void testXAsyncRocketMq1(String taskId , int executeCount)
    {
        XAsyncTestUtils.run(taskId ,executeCount);
    }

    @Override
    @XAsync(strategy = ROCKETMQ)
    public void testXAsyncRocketMq2(String taskId , int executeCount)
    {
        XAsyncTestUtils.run(taskId ,executeCount);
    }

    @Override
    @XAsync(trySync = false ,strategy = ROCKETMQ)
    public void testXAsyncRocketMq3(String taskId , int executeCount , long timeConsumerMS)
    {
        XAsyncTestUtils.run(taskId ,executeCount ,timeConsumerMS);
    }

    @Override
    @XAsync(taskId = "#taskId" ,strategy = ROCKETMQ)
    public void testXAsyncRocketMq4(String taskId, int executeCount)
    {
        XAsyncTestUtils.run(taskId ,executeCount);
    }

}
