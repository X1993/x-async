package com.github.xasync;

/**
 * @author X1993
 * @date 2022/8/8
 * @description
 */
public class XAsyncContent {

    public final static String X_ASYNC = "x-async";

    public final static String X_ASYNC_STRATEGY = X_ASYNC + ".strategy";

    public final static String X_ASYNC_CONFIG = X_ASYNC + ".config";

    /**
     * 异步方法是否处于执行阶段
     */
    private final static ThreadLocal<Boolean> EXECUTING = new ThreadLocal<>();

    public static void executing(){
        EXECUTING.set(Boolean.TRUE);
    }

    public static boolean isExecuting(){
        return Boolean.TRUE.equals(EXECUTING.get());
    }

    public static void complete(){
        EXECUTING.remove();
    }

}
