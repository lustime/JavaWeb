# JavaWeb

> Java Web 开发之路经验总结

| :one:                 | :two:                         | :three:                         | :four:             |
| --------------------- | ----------------------------- | ------------------------------- | ------------------ |
| [JavaEE](#one-javaee) | [单点式技术](#two-单点式技术) | [分布式技术](#three-分布式技术) | [工具](#four-工具) |

## :recycle: 架构设计

> [架构设计](docs/architecture/) 整理架构设计方面的一些学习总结和心得。

- [大型网站架构概述](docs/architecture/大型网站架构概述.md)

## :one: JavaEE

> [JavaEE](docs/javaee/) 技术——Java Web 的基石

## :two: 单点式技术

> [单点式技术（Standalone）](docs/standalone/)，典型的技术如：SSM 框架、SSH 框架。

- Platform
  - [Spring](https://github.com/dunwu/spring-notes) - JavaSE/JavaEE 一站式开发框架。
- ORM
  - [Mybatis](docs/standalone/orm/mybatis.md) - 一个支持普通 SQL 查询，存储过程和高级映射的优秀持久层框架。
  - Hibernate - 待补充。。。
- 安全
  - [Shiro](docs/standalone/security/shiro.md) - 安全框架，具有认证、授权、加密、会话管理功能。

## :three: 分布式技术

> [分布式技术（Distributed）](docs/distributed/)，典型的技术如：分布式缓存、分布式消息队列、分布式服务、分布式搜索引擎等。

- [分布式技术面试题](docs/distributed/分布式技术面试题.md)

### [分布式缓存（CACHE）](docs/distributed/cache)

- [分布式缓存](docs/distributed/cache/分布式缓存.md)
- [Redis](docs/distributed/cache/redis.md)
- Memcached

### [分布式服务（RPC）](docs/distributed/rpc)

- [Dubbo](docs/distributed/rpc/dubbo.md) - 基于 Java 开发的高性能 RPC 框架。
- [ZooKeeper 实战篇](docs/distributed/rpc/zookeeper-basics.md)
- [ZooKeeper 原理篇](docs/distributed/rpc/zookeeper-advanced.md)

### [分布式消息队列（MQ）](docs/distributed/mq)

- [分布式消息队列](docs/distributed/mq/分布式消息队列.md)
- [Kafka 实战篇](docs/distributed/mq/kafka-basics.md)
- [Kafka 原理篇](docs/distributed/mq/kafka-advanced.md)
- [RocketMQ 实战篇](docs/distributed/mq/rocketmq-basics.md)
- [RocketMQ 原理篇](docs/distributed/mq/rocketmq-basics.md)
- [ActiveMQ 实战篇](docs/distributed/mq/ActiveMQ.md)
- RabbitMQ - 待补充。。。

### 分布式搜索引擎

- ElasticSearch - 待补充。。。

## :four: 工具

> [工具](docs/tools/) 整理了 Java Web 领域常用软件。

- [Nginx](https://github.com/dunwu/Nginx) - 轻量级的 Web 服务器、反向代理服务器及电子邮件（IMAP/POP3）代理服务器，支持负载均衡。
- [Tomcat](docs/tools/tomcat.md) - 轻量级的应用服务器
- [Jetty](docs/tools/jetty.md) - 比 Tomcat 更轻量级的应用服务器
