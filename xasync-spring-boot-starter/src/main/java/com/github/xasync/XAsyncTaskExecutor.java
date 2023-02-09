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
     * @param submitParam 选配参数
     * @throws IllegalTaskException 非法的任务描述，无法执行
     */
    void submit(TaskMateData taskMateData, TaskSubmitParam submitParam) throws IllegalTaskException;

    default void submit(TaskMateData taskMateData) throws IllegalTaskException {
        submit(taskMateData ,new TaskSubmitParam());
    }

    /**
     * 执行任务,异步任务直接调用这个方法即可执行
     * @param taskMateData 任务元数据
     * @param executeParam 选配参数
     * @throws TaskExecuteException 任务执行失败，可重试
     * @throws IllegalTaskException 非法任务，无法执行
     */
    void execute(TaskMateData taskMateData , TaskExecuteParam executeParam) throws TaskExecuteException, IllegalTaskException;

    default void execute(TaskMateData taskMateData) throws TaskExecuteException, IllegalTaskException {
        execute(taskMateData ,new TaskExecuteParam());
    }

}
