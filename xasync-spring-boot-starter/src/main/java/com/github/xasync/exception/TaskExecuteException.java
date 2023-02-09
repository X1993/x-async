package com.github.xasync.exception;

/**
 * 任务执行异常,配合重试
 * @author X1993
 * @date 2022/6/16
 * @description
 */
public class TaskExecuteException extends Exception{

    public TaskExecuteException() {
    }

    public TaskExecuteException(String message) {
        super(message);
    }

    public TaskExecuteException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskExecuteException(Throwable cause) {
        super(cause);
    }

    public TaskExecuteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
