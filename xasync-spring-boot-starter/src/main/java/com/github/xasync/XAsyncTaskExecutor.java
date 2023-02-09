package com.github.xasync;

import com.github.xasync.exception.IllegalTaskException;
import com.github.xasync.exception.TaskExecuteException;

/**
 * 异常任务执行器
 * @author X1993
 * @date 2022/6/16
 * @description
 */
public interface XAsyncTaskExecutor {

    /**
     * 提交异步任务
     * @param taskMateData 任务元数据
     * @param trySync 是否尝试同步执行一次，失败再异步重试
     * @throws IllegalTaskException 非法的任务描述，无法执行
     */
    void submit(TaskMateData taskMateData, boolean trySync) throws IllegalTaskException;

    default void submit(TaskMateData taskMateData) throws IllegalTaskException {
        submit(taskMateData ,true);
    }

    /**
     * 执行任务,异步任务直接调用这个方法即可执行
     * @param taskMateData
     * @throws TaskExecuteException 任务执行失败，可重试
     * @throws IllegalTaskException 非法任务，无法执行
     */
    void execute(TaskMateData taskMateData) throws TaskExecuteException, IllegalTaskException;

}
