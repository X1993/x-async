package com.github.xasync.demo.service.memory;

import com.github.xasync.demo.DemoApplication;
import com.github.xasync.demo.service.XAsyncService;
import com.github.xasync.strategy.memory.MemoryXAsyncTaskProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static com.github.xasync.XAsyncContent.X_ASYNC_STRATEGY;
import static com.github.xasync.strategy.memory.MemoryXAsyncTaskConfiguration.MEMORY;

/**
 * @author X1993
 * @date 2022/8/19
 * @description
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class ,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class MemoryXAsyncTaskManagerTest {

    @Autowired
    private XAsyncService xAsyncService;

    @Value("${" + X_ASYNC_STRATEGY + ":" + MEMORY + "}")
    private String xAsyncStrategy;

    @Autowired(required = false)
    private MemoryXAsyncTaskProperties xAsyncTaskProperties;

    @Test
    public void maxRetryTest() {
        if (MEMORY.equals(xAsyncStrategy)){
            int maxExecuteCount = xAsyncTaskProperties.getMaxExecuteCount();
            int executeCount = maxExecuteCount + 7;
            String taskId = "0001";
            xAsyncService.async1(taskId, executeCount);
            //测试达到最大执行次数
            xAsyncService.waitAndValidResult(taskId ,maxExecuteCount ,
                    xAsyncTaskProperties.getInitialDelayMS() + executeCount * xAsyncTaskProperties.getPeriodMS());
        }
    }

    @Test
    public void asyncTest()
    {
        if (MEMORY.equals(xAsyncStrategy)){
            int maxExecuteCount = xAsyncTaskProperties.getMaxExecuteCount();

            int executeCount2 = maxExecuteCount - 1;
            String taskId2 = "0002";
            xAsyncService.async1(taskId2, executeCount2);
            xAsyncService.waitAndValidResult(taskId2 ,executeCount2 ,
                    xAsyncTaskProperties.getInitialDelayMS() + maxExecuteCount * xAsyncTaskProperties.getPeriodMS());
        }
    }

}