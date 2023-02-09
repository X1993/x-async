package com.github.xasync.exception;

/**
 * 任务保存异常
 * @author X1993
 * @date 2022/8/8
 * @description
 */
public class TaskSaveException extends RuntimeException{

    public TaskSaveException() {
    }

    public TaskSaveException(String message) {
        super(message);
    }

    public TaskSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskSaveException(Throwable cause) {
        super(cause);
    }

    public TaskSaveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
