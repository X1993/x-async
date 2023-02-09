package com.github.xasync.demo.service;

import com.github.xasync.demo.DemoApplication;
import com.github.xasync.strategy.memory.MemoryXAsyncTaskConfiguration;
import com.github.xasync.strategy.memory.MemoryXAsyncTaskProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.xasync.strategy.memory.MemoryXAsyncTaskConfiguration.X_ASYNC_MEMORY_STRATEGY_ENABLE;

/**
 * @author X1993
 * @date 2022/8/19
 * @description
 */
@ConditionalOnBean(MemoryXAsyncTaskConfiguration.class)
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class ,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class XAsyncMemoryServiceTest {

    @Autowired
    private XAsyncMemoryService xAsyncService;

    @Autowired
    private MemoryXAsyncTaskProperties xAsyncTaskProperties;

    @Value("${" + X_ASYNC_MEMORY_STRATEGY_ENABLE + "}")
    private Boolean enable;

    private boolean isSkip(){
        return enable != null && !Boolean.TRUE.equals(enable);
    }

    @Test
    public void maxRetryTest() {
        if (isSkip()){
            return;
        }
        XAsyncTestUtils.clear();
        int maxExecuteCount = xAsyncTaskProperties.getMaxExecuteCount();
        int executeCount = maxExecuteCount + 7;
        String taskId = "0001";
        xAsyncService.testXAsyncMemory1(taskId, executeCount);
        //测试达到最大执行次数
        XAsyncTestUtils.waitAndValidResult(taskId ,maxExecuteCount ,
                xAsyncTaskProperties.getInitialDelayMS() + executeCount * xAsyncTaskProperties.getPeriodMS());
    }

    @Test
    public void singleTest0()
    {
        if (isSkip()){
            return;
        }
        XAsyncTestUtils.clear();
        int maxExecuteCount = xAsyncTaskProperties.getMaxExecuteCount();

        int executeCount2 = maxExecuteCount - 1;
        String taskId2 = "0002";
        xAsyncService.testXAsyncMemory1(taskId2, executeCount2);
        XAsyncTestUtils.waitAndValidResult(taskId2 ,executeCount2 ,
                xAsyncTaskProperties.getInitialDelayMS() + maxExecuteCount * xAsyncTaskProperties.getPeriodMS());
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

        xAsyncService.testXAsyncMemory0(String.valueOf(taskNo++) ,executeCount);
        xAsyncService.testXAsyncMemory1(String.valueOf(taskNo++) ,executeCount);
        xAsyncService.testXAsyncMemory2(String.valueOf(taskNo++) ,executeCount);

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
        for (int i = 0; i < count; i++) {
            xAsyncService.testXAsyncMemory3(String.valueOf(taskNo++) ,2 ,1000L);
        }
        Thread.sleep(5L * 1000);
    }

    @Test
    public void spelTaskIdTest()
    {
        if (isSkip()){
            return;
        }
        XAsyncTestUtils.clear();
        xAsyncService.testXAsyncMemory4("A001" ,2);
    }

}