package com.github.xasync.strategy.memory;

import com.github.xasync.TaskMateData;
import com.github.xasync.XAsyncTaskExecutor;
import com.github.xasync.XAsyncTaskStorage;
import com.github.xasync.XAsyncTaskTrigger;
import com.github.xasync.exception.IllegalTaskException;
import com.github.xasync.exception.TaskExecuteException;
import com.github.xasync.exception.TaskSaveException;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author X1993
 * @date 2022/8/8
 * @description
 */
@Slf4j
public class MemoryXAsyncTaskManager implements XAsyncTaskStorage, XAsyncTaskTrigger
{
    private final Map<String ,TaskContent> taskStateMap = new ConcurrentHashMap<>();

    private final XAsyncTaskExecutor xAsyncTaskExecutor;

    private final ScheduledExecutorService scheduledExecutorService;

    private final MemoryXAsyncTaskProperties memoryXAsyncTaskProperties;

    public MemoryXAsyncTaskManager(XAsyncTaskExecutor xAsyncTaskExecutor,
                                   ScheduledExecutorService scheduledExecutorService,
                                   MemoryXAsyncTaskProperties memoryXAsyncTaskProperties)
    {
        this.xAsyncTaskExecutor = xAsyncTaskExecutor;
        this.scheduledExecutorService = scheduledExecutorService;
        this.memoryXAsyncTaskProperties = memoryXAsyncTaskProperties;
    }

    @Override
    public String code() {
        return MemoryXAsyncTaskConfiguration.MEMORY;
    }

    @Override
    public void save(TaskMateData taskMateData) throws TaskSaveException
    {
        String taskId = taskMateData.getTaskId();
        TaskContent trySaveTaskState = new TaskContent(taskMateData);
        TaskContent savedTaskState = taskStateMap.computeIfAbsent(taskId, id -> trySaveTaskState);

        if (savedTaskState != trySaveTaskState){
            //重复保存
            log.warn("async task already save" ,taskMateData.getTaskId());
            return;
        }

        ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(
                () -> run(taskMateData),
                memoryXAsyncTaskProperties.getInitialDelayMS(),
                memoryXAsyncTaskProperties.getPeriodMS(),
                TimeUnit.MILLISECONDS);

        savedTaskState.setScheduledFuture(scheduledFuture);
        log.debug("为异步任务{}注册定时任务,初始延迟{}毫秒,重试间隔{}毫秒，当前有{}个未完成任务" ,
                taskMateData ,
                memoryXAsyncTaskProperties.getInitialDelayMS() ,
                memoryXAsyncTaskProperties.getPeriodMS() ,
                taskStateMap.size());
    }

    @Override
    public void remove(String taskId)
    {
        TaskContent taskState = taskStateMap.remove(taskId);
        if (taskState != null && taskState.getScheduledFuture() != null){
            taskState.getScheduledFuture().cancel(false);
            log.debug("移除异步任务,{},任务总耗时{}毫秒" ,taskId ,System.currentTimeMillis() - taskState.getCreateTimestamp());
        }
    }

    private void run(TaskMateData taskMateData)
    {
        String taskId = taskMateData.getTaskId();
        try {
            xAsyncTaskExecutor.execute(taskMateData);
        } catch (IllegalTaskException e){
            //无法执行的任务，没有重试的必要
            log.error("非法的任务定义，任务无法执行,{}" ,taskMateData ,e);
            remove(taskId);
        } catch (TaskExecuteException e) {
            TaskContent taskContent = taskStateMap.get(taskId);
            if (taskContent != null){
                int currentFailCount = taskContent.getFailCount().incrementAndGet();
                log.warn("任务{}异步执行失败{}次" ,taskId ,currentFailCount);
                if (currentFailCount >= memoryXAsyncTaskProperties.getMaxExecuteCount()){
                    log.warn("任务{}异步执行次数达到最大值{}，取消" ,taskId , memoryXAsyncTaskProperties.getMaxExecuteCount());
                    remove(taskId);
                }
            }
        } catch (Exception e){
            log.error("任务执行失败");
            remove(taskId);
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Data
    static class TaskContent {

        final TaskMateData taskMateData;

        ScheduledFuture<?> scheduledFuture;
        
        final AtomicInteger failCount = new AtomicInteger(0);

        final long createTimestamp = System.currentTimeMillis();

        public TaskContent(TaskMateData taskMateData) {
            this.taskMateData = taskMateData;
        }
    }

}
