package com.github.xasync.demo.service;

import com.github.xasync.aop.XAsync;
import org.springframework.stereotype.Service;

import static com.github.xasync.strategy.memory.MemoryXAsyncTaskConfiguration.MEMORY;

/**
 * @author X1993
 * @date 2023/2/8
 * @description
 */
@Service
public class XAsyncMemoryServiceImpl implements XAsyncMemoryService{

    @Override
    public void testXAsyncMemory0(String taskId , int executeCount)
    {
        XAsyncTestUtils.run(taskId ,executeCount);
    }

    @Override
    @XAsync(trySync = false ,strategy = MEMORY)
    public void testXAsyncMemory1(String taskId , int executeCount)
    {
        XAsyncTestUtils.run(taskId ,executeCount);
    }

    @Override
    @XAsync(strategy = MEMORY)
    public void testXAsyncMemory2(String taskId , int executeCount)
    {
        XAsyncTestUtils.run(taskId ,executeCount);
    }

    @Override
    @XAsync(trySync = false ,strategy = MEMORY)
    public void testXAsyncMemory3(String taskId , int executeCount , long timeConsumerMS)
    {
        XAsyncTestUtils.run(taskId ,executeCount ,timeConsumerMS);
    }

    @Override
    @XAsync(taskId = "#taskId" ,strategy = MEMORY)
    public void testXAsyncMemory4(String taskId, int executeCount)
    {
        XAsyncTestUtils.run(taskId ,executeCount);
    }

}
