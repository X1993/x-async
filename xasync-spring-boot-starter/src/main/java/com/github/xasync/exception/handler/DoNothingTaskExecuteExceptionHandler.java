package com.github.xasync.exception.handler;

import com.github.xasync.TaskMateData;

/**
 * @author X1993
 * @date 2022/8/8
 * @description
 */
public class DoNothingTaskExecuteExceptionHandler implements TaskExecuteExceptionHandler {

    @Override
    public void handle(TaskMateData taskMateData, String message) {

    }

    @Override
    public void handle(TaskMateData taskMateData, String message, Throwable throwable) {

    }

}
