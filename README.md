# JavaWeb

> Java Web 开发之路经验总结
> 
> 电子书阅读：[Github Pages](https://dunwu.github.io/javaweb/) | [Gitee Pages](https://turnon.gitee.io/javaweb/)

| :beginner:    | :recycle:             | :coffee:          | :eye:                 | :eyes:                   | :computer:        |
| ------------- | --------------------- | ----------------- | --------------------- | ------------------------- | ------------- |
| [准备](#准备) | [架构设计](#架构设计) | [JavaEE](#javaee) | [主流技术](#主流技术) | [分布式技术](#分布式技术) | [服务器](#服务器) |

<!-- TOC -->

## 准备

作为 Java Web 工程师，你应该多多少少掌握一些的知识：

- [JavaCore](https://dunwu.github.io/javacore/) - Java 核心技术
- [前端技术指南](https://github.com/dunwu/frontend-tutorial) - 即使是后端工程师，也难免会接触到前端技术。前端技术五花八门，如：React、Vue、Angular、Webpack、ES6、Babel、Node.js 等等。不说掌握，至少也应该知道这些技术是什么。

## 架构设计

> [架构设计](docs/architecture/) 整理架构设计方面的一些学习总结和心得。

- [大型网站架构概述](docs/architecture/大型网站架构概述.md)
- [网站的高性能架构](docs/architecture/网站的高性能架构.md)
- [网站的高可用架构](docs/architecture/网站的高可用架构.md)
- [网站的伸缩性架构](docs/architecture/网站的伸缩性架构.md)
- [网站的可扩展架构](docs/architecture/网站的可扩展架构.md)
- [网站的安全架构](docs/architecture/网站的安全架构.md) - 关键词：XSS、CSRF、SQL 注入、DoS、消息摘要、加密算法、证书
- [网站典型故障](docs/architecture/网站典型故障.md)

## JavaEE

> [JavaEE](docs/javaee/) 技术——Java Web 的基石

- [JavaEE 面经](docs/javaee/javaee-interview.md)
- [JavaEE 之 Servlet 指南](docs/javaee/javaee-servlet.md)
- [JavaEE 之 Jsp 指南](docs/javaee/javaee-jsp.md)
- [JavaEE 之 Filter 和 Listener](docs/javaee/javaee-filter-listener.md)
- [JavaEE 之 Cookie 和 Session](docs/javaee/javaee-cookie-sesion.md)

## 主流技术

> [主流技术](docs/standalone/)，典型的技术如：SSM 框架、SSH 框架。

- MVC
  - [spring-tutorial](https://dunwu.gitbooks.io/spring-tutorial/) - Spring 教程
  - [spring-boot-tutorial](https://dunwu.github.io/spring-boot-tutorial/) - Spring Boot 教程
- ORM
  - [Mybatis](docs/standalone/orm/mybatis.md) - 一个支持普通 SQL 查询，存储过程和高级映射的优秀持久层框架。
- Security
  - [Shiro](docs/standalone/security/shiro.md) - 安全框架，具有认证、授权、加密、会话管理功能。

## 分布式技术

> [分布式技术（Distributed）](docs/distributed/)，典型的技术如：分布式缓存、分布式消息队列、分布式服务、分布式搜索引擎等。

- [分布式技术面试题](docs/distributed/分布式技术面试题.md)

- [分布式缓存（CACHE）](docs/distributed/cache)
  - [分布式缓存](docs/distributed/cache/分布式缓存.md)
  - [Redis](docs/distributed/cache/redis.md)
  - Memcached
- [分布式服务（RPC）](docs/distributed/rpc)
  - [Dubbo](docs/distributed/rpc/dubbo.md) - 基于 Java 开发的高性能 RPC 框架。
  - [ZooKeeper 实战篇](docs/distributed/rpc/zookeeper-basics.md)
  - [ZooKeeper 原理篇](docs/distributed/rpc/zookeeper-advanced.md)
- [分布式消息队列（MQ）](docs/distributed/mq)
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

## 服务器

> [服务器](docs/server/) 章节总结 Java Web 领域主流服务器技术。

- [Nginx 简易教程](https://github.com/dunwu/nginx-tutorial) - 轻量级的 Web 服务器、反向代理服务器及电子邮件（IMAP/POP3）代理服务器，支持负载均衡。
- [Tomcat 应用指南](docs/server/tomcat.md) - 轻量级的应用服务器
- [Jetty 应用指南](docs/server/jetty.md) - 比 Tomcat 更轻量级的应用服务器
