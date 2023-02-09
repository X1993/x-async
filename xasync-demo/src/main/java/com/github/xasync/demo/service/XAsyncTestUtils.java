package com.github.xasync.demo.service;

import com.github.xasync.demo.model.TaskState;
import lombok.extern.slf4j.Slf4j;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author X1993
 * @date 2023/2/8
 * @description
 */
@Slf4j
public class XAsyncTestUtils {

    /**
     * key:任务id
     * value:执行次数
     */
    private static final Map<String ,TaskState> TASK_EXECUTE_RESULT_MAP = new ConcurrentHashMap<>();

    public static void clear(){
        TASK_EXECUTE_RESULT_MAP.clear();
    }

    public static void waitAndValidResult(String taskId ,int expectExecuteCount ,long timeout)
    {
        long startCurrentTimeMillis = System.currentTimeMillis();
        log.info("等待任务{}" ,taskId);
        while (timeout <= 0 || System.currentTimeMillis() - startCurrentTimeMillis < timeout) {
            TaskState taskState = TASK_EXECUTE_RESULT_MAP.get(taskId);
            if (taskState != null && taskState.isCompleted()){
                if (taskState.getCount() != expectExecuteCount){
                    throw new RuntimeException(MessageFormat.format("执行次数不符合预期 ,{0}, {1}" ,
                            taskState.getCount() ,expectExecuteCount));
                }
                return;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        log.debug("任务{}执行超时" ,taskId);
        TaskState taskState = TASK_EXECUTE_RESULT_MAP.get(taskId);
        if (taskState == null){
            throw new RuntimeException(MessageFormat.format(
                    "任务{0}没有执行记录" ,taskId));
        }
        if (taskState.getCount() != expectExecuteCount){
            throw new RuntimeException(MessageFormat.format(
                    "任务{0}执行次数不符合预期，实际执行次数：{1}，期望执行次数：{2}" ,
                    taskId ,taskState.getCount() ,expectExecuteCount));
        }
    }

    public static void run(String taskId ,int executeCount ,long taskConsumerMs) {
        TaskState taskState = TASK_EXECUTE_RESULT_MAP.computeIfAbsent(taskId, x -> {
            TaskState newTaskState = new TaskState();
            log.info("创建任务{}上下文" ,taskId);
            return newTaskState;
        });
        taskState.setCount(taskState.getCount() + 1);
        if (taskConsumerMs > 0){
            try {
                Thread.sleep(taskConsumerMs);
            } catch (InterruptedException e) {

            }
        }
        if (taskState.getCount() >= executeCount){
            log.info("任务执行成功，{}" ,taskState);
            taskState.setCompleted(true);
            taskState.setCompletedDateTime(LocalDateTime.now());
            return;
        }else {
            throw new RuntimeException(MessageFormat.format(
                    "任务{0}第{1}次执行失败（模拟）" ,taskId ,taskState.getCount()));
        }
    }

    public static void run(String taskId ,int executeCount){
        run(taskId ,executeCount ,-1);
    }

}
