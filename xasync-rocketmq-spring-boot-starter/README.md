### 开发背景:
在项目开发的过程中，某些场景要求异步任务支持重试且节点重启不丢失，Spring自带的申明式异步@Async不满足，
基此开发了x-async组件，支持springboot集成，相比原生的Async框架，该组件支持以下特征

* [x] 支持自定义重试策略
* [x] 支持自定义持久化，节点宕机任务不丢失

### 简单入门实例
##### 1. 添加maven依赖

```xml
<dependency>
    <groupId>com.github.X1993</groupId>
    <artifactId>xasync-rocketmq-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

##### 2. 添加配置

```yaml
x-async:
  #使用内存保存任务
  strategy-memory:
    #开关，默认值：true
    enable: true
    config:
      #异步任务执行线程池核心线程数，有默认值
      core-pool-size: 3
      #最大执行次数，有默认值
      max-execute-count: 3
      #异步重试间隔，单位毫秒，有默认值
      period-ms: 1000
      #异步任务提交后延迟执行时间，单位毫秒，有默认值
      initial_delay_ms: 100
  #使用rocketmq保存任务
  strategy-rocketmq:
    #开关，默认开启
    enable: false
    config:
      #保存任务的主题,有默认值
      topic: x-async-task-test0
      #消息的tag，有默认值
      tag: all
      #消费者组，有默认值
      consumer-group: ${spring.application.name}
spring:
  profiles:
    active: ${ENVIRONMENT:dev}
  application:
    name: xasync-demo

logging:
  level:
    root: debug

x-async:
  #使用内存保存任务
  strategy-memory:
    #开关，默认值：true
    enable: true
    config:
      #异步任务执行线程池核心线程数，有默认值
      core-pool-size: 3
      #最大执行次数，有默认值
      max-execute-count: 3
      #异步重试间隔，单位毫秒，有默认值
      period-ms: 1000
      #异步任务提交后延迟执行时间，单位毫秒，有默认值
      initial_delay_ms: 100
  #使用rocketmq保存任务
  strategy-rocketmq:
    #开关，默认开启
    enable: false
    config:
      #保存任务的主题,有默认值
      topic: x-async-task-test0
      #消息的tag，有默认值
      tag: all
      #消费者组，有默认值
      consumer-group: ${spring.application.name}

#以下为rocketMq原生配置，x-async.strategy-rocketmq.enable:true 时添加
rocketmq:
  name-server: 192.168.99.88:9876;192.168.99.74:9876
  #生产者
  producer:
    #生产者组名，规定在一个应用里面必须唯一
    group: ${spring.application.name}
    #消息发送的超时时间 默认3000ms
    send-message-timeout: 3000
    #消息达到4096字节的时候，消息就会被压缩。默认 4096
    compress-message-body-threshold: 4096
    #最大的消息限制，默认为128K
    max-message-size: 4194304
    #同步消息发送失败重试次数
    retry-times-when-send-failed: 3
    #在内部发送失败时是否重试其他代理，这个参数在有多个broker时才生效
    retry-next-server: true
    #异步消息发送失败重试的次数
    retry-times-when-send-async-failed: 3

```

##### 3. 在方法上添加注解

```java
    import com.github.xasync.aop.XAsync;
    import org.springframework.stereotype.Component;

    public interface Service {
    
        @XAsync(strategy = MEMORY)
        void method0(String param0, int param1);
    
        void method1(String param0, int param1);
    
    }
    
    @Component
    public class ServiceImpl implements Service {
    
        void method0(String param0, int param1);
    
        @XAsync(strategy = MEMORY)
        void method1(String param0, int param1);
    
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Inherited
    public @interface XAsync {
    
        /**
         * 是否尝试同步执行一次，失败再异步重试
         * @return
         */
        boolean trySync() default true;
    
        /**
         * 任务存储策略
         * @return
         */
        String strategy() default "";
    
        /**
         * 异步任务id
         * 支持SPEL表达式
         * @return
         */
        String taskId() default "";
    
    }
```


  
