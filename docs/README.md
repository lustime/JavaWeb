# JavaWeb

> Java Web 开发之路经验总结

| :beginner:             | :recycle:                     | :one:                 | :two:                         | :three:                         | :four:             |
| ---------------------- | ----------------------------- | --------------------- | ----------------------------- | ------------------------------- | ------------------ |
| [准备](#beginner-准备) | [架构设计](#recycle-架构设计) | [JavaEE](#one-javaee) | [单点式技术](#two-单点式技术) | [分布式技术](#three-分布式技术) | [工具](#four-工具) |

<!-- TOC -->

## :beginner: 准备

作为 Web 工程师，你应该多多少少掌握一些的知识：

- [网络通信](https://github.com/dunwu/notes/tree/master/网络通信) - 你需要掌握一些网络通信协议知识。至少，你应该知道 HTTP、DNS 协议的工作机制。
- [前端技术指南](https://github.com/dunwu/frontend-tutorial) - 即使是后端工程师，也难免会接触到前端技术。前端技术五花八门，如：React、Vue、Angular、Webpack、ES6、Babel、Node.js 等等。不说掌握，至少也应该知道这些技术是什么。

## :recycle: 架构设计

> [架构设计](docs/architecture/) 整理架构设计方面的一些学习总结和心得。

- [大型网站架构概述](docs/architecture/大型网站架构概述.md)
- [网站的高性能架构](docs/architecture/网站的高性能架构.md)
- [网站的高可用架构](docs/architecture/网站的高可用架构.md)
- [网站的伸缩性架构](docs/architecture/网站的伸缩性架构.md)
- [网站的可扩展架构](docs/architecture/网站的可扩展架构.md)
- [网站的安全架构](docs/architecture/网站的安全架构.md) - 关键词：XSS、CSRF、SQL 注入、DoS、消息摘要、加密算法、证书
- [网站典型故障](docs/architecture/网站典型故障.md)

## :one: JavaEE

> [JavaEE](docs/javaee/) 技术——Java Web 的基石

## :two: 单点式技术

> [单点式技术（Standalone）](docs/standalone/)，典型的技术如：SSM 框架、SSH 框架。

### Platform

- [Spring](https://github.com/dunwu/spring-notes) - JavaSE/JavaEE 一站式开发框架。
- [Spring Boot](https://github.com/dunwu/spring-boot-tutorial) - 简化*Spring*应用的初始搭建以及开发过程。

### ORM

- [Mybatis](docs/standalone/orm/mybatis.md) - 一个支持普通 SQL 查询，存储过程和高级映射的优秀持久层框架。
- Hibernate - 待补充。。。

### 安全

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

- 原理
  - [分布式消息队列](docs/distributed/mq/分布式消息队列.md)
- Kafka
  - [Kafka 实战篇](docs/distributed/mq/kafka/kafka-basics.md)
  - [Kafka 原理篇](docs/distributed/mq/kafka/kafka-advanced.md)
  - [Kafka Cheat Sheet](docs/distributed/mq/kafka/kafka-cheat-sheet.md)
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
