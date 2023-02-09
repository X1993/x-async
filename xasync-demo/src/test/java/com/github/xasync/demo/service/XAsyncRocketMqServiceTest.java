package com.github.xasync.demo.service;

import com.github.xasync.demo.DemoApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.xasync.strategy.rocketmq.RocketMqXAsyncTaskConfiguration.X_ASYNC_ROCKET_STRATEGY_ENABLE;


/**
 * @author X1993
 * @date 2022/8/16
 * @description
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class ,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class XAsyncRocketMqServiceTest {

    @Autowired
    private XAsyncRocketMqService xAsyncService;

    @Value("${" + X_ASYNC_ROCKET_STRATEGY_ENABLE + "}")
    private Boolean enable;

    private boolean isSkip(){
        return enable != null && !Boolean.TRUE.equals(enable);
    }

    @Test
    public void concurrencyTest()
    {
        if (isSkip()){
            return;
        }
        XAsyncTestUtils.clear();
        //如果有最大重试次数确保不要超过
        int executeCount = 2;

        int startNo = 1000;
        int taskNo = startNo;

        xAsyncService.testXAsyncRocketMq0(String.valueOf(taskNo++) ,executeCount);
        xAsyncService.testXAsyncRocketMq1(String.valueOf(taskNo++) ,executeCount);
        xAsyncService.testXAsyncRocketMq2(String.valueOf(taskNo++) ,executeCount);

        taskNo = startNo;
        long timeout = 10L * 1000;

        XAsyncTestUtils.waitAndValidResult(String.valueOf(taskNo++) ,executeCount ,timeout);
        XAsyncTestUtils.waitAndValidResult(String.valueOf(taskNo++) ,executeCount ,timeout);
        XAsyncTestUtils.waitAndValidResult(String.valueOf(taskNo++) ,executeCount ,timeout);
    }

    @Test
    public void highConcurrencyTest() throws InterruptedException {
        if (isSkip()){
            return;
        }
        XAsyncTestUtils.clear();
        int taskNo = 100;
        int count = 50;
        Long timeConsumerMS = 1000L;
        for (int i = 0; i < count; i++) {
            xAsyncService.testXAsyncRocketMq3(String.valueOf(taskNo++) ,2 ,timeConsumerMS);
        }
        Thread.sleep(5000L);
    }

    @Test
    public void spelTaskIdTest()
    {
        if (isSkip()){
            return;
        }
        XAsyncTestUtils.clear();
        xAsyncService.testXAsyncRocketMq4("A001" ,2);
    }

}