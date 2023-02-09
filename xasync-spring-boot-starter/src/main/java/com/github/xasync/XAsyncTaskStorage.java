package com.github.xasync;

import com.github.xasync.exception.TaskSaveException;

/**
 * 任务储存器
 * @author X1993
 * @date 2022/8/8
 * @description
 */
public interface XAsyncTaskStorage {

    /**
     * 唯一标识，多个存储器不能冲突
     * @return
     */
    String code();

    /**
     * 保存任务信息
     * @param taskMateData
     * @throws TaskSaveException
     */
    void save(TaskMateData taskMateData) throws TaskSaveException;

    /**
     * 移除任务信息
     * @param taskId
     */
    void remove(String taskId);

}
