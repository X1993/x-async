package com.github.xasync.exception;

/**
 * 不合法的任务，没有重试的必要
 * @author X1993
 * @date 2022/6/17
 * @description
 */
public class IllegalTaskException extends Exception{

    public IllegalTaskException() {
    }

    public IllegalTaskException(String message) {
        super(message);
    }

    public IllegalTaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalTaskException(Throwable cause) {
        super(cause);
    }

    public IllegalTaskException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
