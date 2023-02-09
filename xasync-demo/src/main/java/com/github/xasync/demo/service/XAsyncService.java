package com.github.xasync.demo.service;

import com.github.xasync.aop.XAsync;

/**
 * @author X1993
 * @date 2022/8/16
 * @description
 */
public interface XAsyncService {

    @XAsync
    void async0(String taskId ,int executeCount);

    void async1(String taskId ,int executeCount);

    void async2(String taskId ,int executeCount);

    @XAsync(trySync = false)
    void async3(String taskId, int executeCount, long timeConsumerMS);

    void async4(String taskId, int executeCount);

    void waitAndValidResult(String taskId, int executeCount, long timeout);

    default void waitAndValidResult(String taskId, int executeCount){
        waitAndValidResult(taskId, executeCount ,-1);
    }
}
