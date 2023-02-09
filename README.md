### 开发背景:
在项目开发的过程中，某些场景要求异步任务支持重试且节点重启不丢失，Spring自带的申明式异步@Async不满足，
基此开发了x-async组件，支持springboot集成，相比原生的Async框架，该组件支持以下特征

* [x] 支持自定义重试策略
* [x] 支持自定义持久化，节点宕机任务不丢失

## 目录结构
```bash
|-- xasync-spring-boot-starter             ➜ 核心模块,默认基于内存实现任务的非持久化和重试
|-- xasync-rocketmq-spring-boot-starter    ➜ 基于rocketmq实现任务的持久化和重试
|-- xasync-demo                            ➜ 演示服务
```

[xasync-spring-boot-starter快速开始](./xasync-spring-boot-starter)


[xasync-rocketmq-spring-boot-starter快速开始](./xasync-rocketmq-spring-boot-starter)