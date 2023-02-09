package com.github.xasync.demo.service;

import com.github.xasync.aop.XAsync;
import com.github.xasync.demo.model.TaskState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author X1993
 * @date 2022/8/16
 * @description
 */
@Slf4j
@Service
public class XAsyncServiceImpl implements XAsyncService {

    /**
     * key:任务id
     * value:执行次数
     */
    private static final Map<String , TaskState> TASK_EXECUTE_RESULT_MAP = new ConcurrentHashMap<>();

    @Override
    public void async0(String taskId ,int executeCount)
    {
        run(taskId ,executeCount);
    }

    @Override
    @XAsync(trySync = false)
    public void async1(String taskId ,int executeCount)
    {
        run(taskId ,executeCount);
    }

    @Override
    @XAsync
    public void async2(String taskId ,int executeCount)
    {
        run(taskId ,executeCount);
    }

    @Override
    @XAsync(trySync = false)
    public void async3(String taskId ,int executeCount ,long timeConsumerMS)
    {
        run(taskId ,executeCount ,timeConsumerMS);
    }

    @Override
    @XAsync(taskId = "#taskId")
    public void async4(String taskId, int executeCount)
    {
        run(taskId ,executeCount);
    }

    @Override
    public void waitAndValidResult(String taskId ,int executeCount ,long timeout)
    {
        long startCurrentTimeMillis = System.currentTimeMillis();
        log.info("等待任务{}" ,taskId);
        while (timeout <= 0 || System.currentTimeMillis() - startCurrentTimeMillis < timeout) {
            TaskState taskState = TASK_EXECUTE_RESULT_MAP.get(taskId);
            if (taskState != null && taskState.isCompleted()){
                if (taskState.getCount() != executeCount){
                    throw new RuntimeException(MessageFormat.format("执行次数不符合预期 ,{0}, {1}" ,
                            taskState.getCount() ,executeCount));
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
        if (taskState.getCount() != executeCount){
            throw new RuntimeException(MessageFormat.format(
                    "任务{0}执行次数不符合预期，实际执行次数：{1}，期望执行次数：{2}" ,
                    taskId ,taskState.getCount() ,executeCount));
        }
    }

    private void run(String taskId ,int executeCount ,long taskConsumerMs) {
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
            throw new RuntimeException(MessageFormat.format("任务{0}第{1}次执行失败" ,taskId ,taskState.getCount()));
        }
    }

    private void run(String taskId ,int executeCount){
        run(taskId ,executeCount ,-1);
    }

}
