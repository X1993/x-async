package com.github.xasync.strategy.rocketmq;

import com.alibaba.fastjson.JSON;
import com.github.xasync.TaskMateData;
import com.github.xasync.XAsyncTaskStorage;
import com.github.xasync.exception.TaskSaveException;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.Objects;

import static com.github.xasync.strategy.rocketmq.RocketMqXAsyncTaskConfiguration.SERIALIZABLE_CHARSET;

/**
 * 将异步任务保存到Mq
 * @author X1993
 * @date 2022/8/8
 * @description
 */
@Slf4j
public class RocketMqXAsyncTaskStorage implements XAsyncTaskStorage {

    private RocketMQTemplate rocketMQTemplate;

    public RocketMqXAsyncTaskStorage(RocketMQTemplate rocketMQTemplate)
    {
        Objects.requireNonNull(rocketMQTemplate);
        this.rocketMQTemplate = rocketMQTemplate;
    }

    @Override
    public void save(TaskMateData taskMateData) throws TaskSaveException
    {
        String messageContent = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(byteArrayOutputStream)){
            //之所以使用java原生序列化是考虑到可以兼容泛型参数,例如 参数类型为 List<User>
            out.writeObject(taskMateData);
            //必须是ISO-8859-1
            messageContent = byteArrayOutputStream.toString(SERIALIZABLE_CHARSET);
        } catch (IOException e) {
            throw new TaskSaveException(MessageFormat.format("异常任务序列化失败,{0}" ,taskMateData) ,e);
        }

        SendResult sendResult = rocketMQTemplate.syncSend(RocketMqXAsyncTaskProperties.TOPIC
                + ":" + RocketMqXAsyncTaskProperties.TAG, messageContent);

        if (sendResult.getSendStatus() == SendStatus.SEND_OK){
            log.debug("推送异步任务信息到mq,topic:【{}】,tag:【{}】,{}" , RocketMqXAsyncTaskProperties.TOPIC ,
                    RocketMqXAsyncTaskProperties.TAG ,JSON.toJSON(taskMateData));
        }else {
            throw new TaskSaveException(MessageFormat.format("异步任务消息推送失败,{0}" ,taskMateData));
        }
    }

    @Override
    public void remove(String taskId) {
        //消费时不报错即可
    }

}
