package com.github.xasync.exception.handler;

import com.github.xasync.TaskMateData;

/**
 * 任务执行异常处理器
 * @author X1993
 * @date 2022/8/8
 * @description
 */
public interface TaskExecuteExceptionHandler
{
    /**
     * 异常处理
     * @param message
     */
    void handle(TaskMateData taskMateData , String message);

    /**
     * 异常处理
     * @param message
     * @param throwable
     */
    void handle(TaskMateData taskMateData ,String message, Throwable throwable);

}
