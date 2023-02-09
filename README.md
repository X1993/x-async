### 开发背景:
在项目开发的过程中，某些场景要求异步任务支持重试且节点重启不丢失，Spring自带的申明式异步@Async不满足，
基此开发了xasync组件，支持springboot集成，相比原生的Async框架，该组件支持以下特征

* [x] 支持自定义重试策略
* [x] 支持自定义持久化，节点宕机任务不丢失

## 目录结构
```bash
|-- xasyc-spring-boot-starter             ➜ 核心模块,默认基于内存实现任务的非持久化和重试
|-- xasyc-rocketmq-spring-boot-starter    ➜ 基于rocketmq实现任务的持久化和重试
|-- xasyc-demo                            ➜ 演示服务
```

### 简单入门实例
#### 1. 添加maven依赖：

```java
<dependency>
    <groupId>com.maihaoche</groupId>
    <artifactId>spring-boot-starter-rocketmq</artifactId>
    <version>0.1.0</version>
</dependency>
```

##### 2. 添加配置：

```java
spring:
    rocketmq:
      name-server-address: 172.21.10.111:9876
      # 可选, 如果无需发送消息则忽略该配置
      producer-group: local_pufang_producer
      # 发送超时配置毫秒数, 可选, 默认3000
      send-msg-timeout: 5000
      # 追溯消息具体消费情况的开关，默认打开
      #trace-enabled: false
      # 是否启用VIP通道，默认打开
      #vip-channel-enabled: false
```
##### 3. 程序入口添加注解开启自动装配

在springboot应用主入口添加`@EnableMQConfiguration`注解开启自动装配：

```java
@SpringBootApplication
@EnableMQConfiguration
class DemoApplication {
}
```

##### 4. 构建消息体

通过我们提供的`Builder`类创建消息对象，详见[wiki](https://github.com/maihaoche/rocketmq-spring-boot-starter/wiki/构建消息体)


```java
MessageBuilder.of(new MSG_POJO()).topic("some-msg-topic").build();
```


##### 5. 创建发送方

详见[wiki](https://github.com/maihaoche/rocketmq-spring-boot-starter/wiki/%E6%9C%80%E4%BD%B3%E5%AE%9E%E8%B7%B5-Provider)：


```java
@MQProducer
public class DemoProducer extends AbstractMQProducer{
}
```

##### 6. 创建消费方

详见[wiki](https://github.com/maihaoche/rocketmq-spring-boot-starter/wiki/%E6%9C%80%E4%BD%B3%E5%AE%9E%E8%B7%B5-Consumer)：
**支持springEL风格配置项解析**，如存在`suclogger-test-cluster`配置项，会优先将topic解析为配置项对应的值。

```java
@MQConsumer(topic = "${suclogger-test-cluster}", consumerGroup = "local_sucloger_dev")
public class DemoConsumer extends AbstractMQPushConsumer {

    @Override
    public boolean process(Object message, Map extMap) {
        // extMap 中包含messageExt中的属性和message.properties中的属性
        System.out.println(message);
        return true;
    }
}
```

##### 7. 发送消息：

```java

// 注入发送者
@Autowired
private DemoProducer demoProducer;
    
...
    
// 发送
demoProducer.syncSend(msg)
    
```



------

### 发送事务消息###

> Since 0.1.0



##### 5.1 事务消息发送方#####

```java
@MQTransactionProducer(producerGroup = "${camaro.mq.transactionProducerGroup}")
public class DemoTransactionProducer extends AbstractMQTransactionProducer {

    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        // executeLocalTransaction
        return LocalTransactionState.UNKNOW;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        // LocalTransactionState.ROLLBACK_MESSAGE
        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
```





  
