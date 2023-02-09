package com.github.xasync.aop;

import com.github.xasync.XAsyncTaskStorage;

import java.lang.annotation.*;

/**
 * 标注异步方法
 * @author X1993
 * @date 2022/8/5
 * @description
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface XAsync {

    /**
     * 是否尝试同步执行一次，失败再异步重试
     * @return
     */
    boolean trySync() default true;

    /**
     * 任务保存策略
     * @see XAsyncTaskStorage#code() 匹配
     * @return
     */
    String strategy() default "";

    /**
     * 异步任务id
     * 支持SPEL表达式
     * @return
     */
    String taskId() default "";

}
