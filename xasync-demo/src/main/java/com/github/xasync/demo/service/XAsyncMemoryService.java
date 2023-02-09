package com.github.xasync.demo.service;

import com.github.xasync.aop.XAsync;

import static com.github.xasync.strategy.memory.MemoryXAsyncTaskConfiguration.MEMORY;

/**
 * @author X1993
 * @date 2023/2/8
 * @description
 */
public interface XAsyncMemoryService {

    @XAsync(strategy = MEMORY)
    void testXAsyncMemory0(String taskId , int executeCount);

    void testXAsyncMemory1(String taskId , int executeCount);

    void testXAsyncMemory2(String taskId , int executeCount);

    @XAsync(trySync = false ,strategy = MEMORY)
    void testXAsyncMemory3(String taskId, int executeCount, long timeConsumerMS);

    void testXAsyncMemory4(String taskId, int executeCount);

}
