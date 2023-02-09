package com.github.xasync.demo.service;

import com.github.xasync.demo.DemoApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author X1993
 * @date 2022/8/16
 * @description
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class ,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class XAsyncTaskExecutorImplTest {

    @Autowired
    private XAsyncService xAsyncService;

    @Test
    public void asyncTest()
    {
        //如果有最大重试次数确保不要超过
        int executeCount = 2;

        int startNo = 1000;
        int taskNo = startNo;

        xAsyncService.async0(String.valueOf(taskNo++) ,executeCount);
        xAsyncService.async1(String.valueOf(taskNo++) ,executeCount);
        xAsyncService.async2(String.valueOf(taskNo++) ,executeCount);

        taskNo = startNo;
        long timeout = 10L * 1000;

        xAsyncService.waitAndValidResult(String.valueOf(taskNo++) ,executeCount ,timeout);
        xAsyncService.waitAndValidResult(String.valueOf(taskNo++) ,executeCount ,timeout);
        xAsyncService.waitAndValidResult(String.valueOf(taskNo++) ,executeCount ,timeout);
    }

    @Test
    public void asyncTest1() throws InterruptedException {
        int taskNo = 100;
        int count = 50;
        for (int i = 0; i < count; i++) {
            xAsyncService.async3(String.valueOf(taskNo++) ,1 ,2000L);
        }
        Thread.sleep(60L * 1000);
    }

    @Test
    public void spelTaskIdTest()
    {
        xAsyncService.async4("A001" ,2);
    }

}