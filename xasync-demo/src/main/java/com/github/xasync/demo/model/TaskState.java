package com.github.xasync.demo.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author X1993
 * @date 2022/8/16
 * @description
 */
@Data
public class TaskState {

    /**
     * 是否执行成功
     */
    private boolean completed = false;

    /**
     * 执行次数
     */
    private int count = 0;

    /**
     * 异步任务创建时间
     */
    private LocalDateTime startDateTime = LocalDateTime.now();

    /**
     * 异步任务完成时间
     */
    private LocalDateTime completedDateTime;

}
