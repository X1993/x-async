package com.github.xasync.strategy.rocketmq;

import com.github.xasync.TaskMateData;
import com.github.xasync.XAsyncTaskExecutor;
import com.github.xasync.XAsyncTaskTrigger;
import com.github.xasync.exception.IllegalTaskException;
import com.github.xasync.exception.TaskExecuteException;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import static com.github.xasync.strategy.rocketmq.RocketMqXAsyncTaskConfiguration.SERIALIZABLE_CHARSET;

/**
 * 利用Mq消费者触发异步任务的执行
 * @author X1993
 * @date 2022/6/16
 * @description
 */
@RocketMQMessageListener(
        consumerGroup = RocketMqXAsyncTaskProperties.CONSUMER_GROUP_REF ,
        topic = RocketMqXAsyncTaskProperties.TOPIC_REF)
@Slf4j
public class RocketMqXAsyncTaskTrigger implements XAsyncTaskTrigger, RocketMQListener<String>{

    private final XAsyncTaskExecutor xAsyncTaskExecutor;

    public RocketMqXAsyncTaskTrigger(XAsyncTaskExecutor xAsyncTaskExecutor) {
        this.xAsyncTaskExecutor = xAsyncTaskExecutor;
    }

    @Override
    public void onMessage(String message)
    {
        TaskMateData taskMateData = null;
        try (ObjectInputStream objectInputStream = new ObjectInputStream(
                new ByteArrayInputStream(message.getBytes(SERIALIZABLE_CHARSET))))
        {
            Object o = objectInputStream.readObject();
            if (!(o instanceof TaskMateData)){
                log.warn("消息内容异常，无法处理异常任务，{},{}" ,message ,o);
                return;
            }
            taskMateData = (TaskMateData) o;
        } catch (IOException | ClassNotFoundException e) {
            log.warn("系统异常，无法处理异常任务,{}" ,message ,e);
        }
        log.debug("收到异步任务消息" ,taskMateData);

        try {
            xAsyncTaskExecutor.execute(taskMateData);
            log.debug("异步任务执行成功，{}" ,taskMateData);
        } catch (IllegalTaskException e){
            //无法执行的任务，没有重试的必要
        } catch (TaskExecuteException e) {
            //依赖mq的机制实现重试
            throw new RuntimeException(e);
        } catch (Exception e){
            log.warn("未知的异常，取消任务。{}" ,taskMateData ,e);
        }
    }
}
