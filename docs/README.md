# JavaWeb

> JavaWeb 开发之路经验总结。
>
> - :repeat: 项目同步维护：[Github](https://github.com/dunwu/javaweb/) | [Gitee](https://gitee.com/turnon/javaweb/)
> - :book: 电子书阅读：[Github Pages](https://dunwu.github.io/javaweb/) | [Gitee Pages](http://turnon.gitee.io/javaweb/)

|        🔰         |            🏗            |          ☕          |            ⭐️            |              ✨              |            🕸            |          💻          |
| :---------------: | :---------------------: | :------------------: | :-----------------------: | :--------------------------: | :---------------------: | :------------------: |
| [准备](#🔰️-准备) | [架构设计](#🏗-架构设计) | [JavaEE](#☕-javaee) | [主流技术](#⭐️-主流技术) | [分布式技术](#✨-分布式技术) | [网络通信](#🕸-网络通信) | [服务器](#💻-服务器) |

## 🔰️ 准备

作为 Java Web 工程师，你应该多多少少掌握一些的知识：

- [JavaCore](https://dunwu.github.io/javacore/) - Java 核心技术
- [前端技术指南](https://github.com/dunwu/frontend-tutorial) - 即使是后端工程师，也难免会接触到前端技术。前端技术五花八门，如：React、Vue、Angular、Webpack、ES6、Babel、Node.js 等等。不说掌握，至少也应该知道这些技术是什么。

## 🏗 架构设计

> [架构设计](architecture/) 整理架构设计方面的一些学习总结和心得。

- [大型网站架构概述](architecture/大型网站架构概述.md)
- [网站的高性能架构](architecture/网站的高性能架构.md)
- [网站的高可用架构](architecture/网站的高可用架构.md)
- [网站的伸缩性架构](architecture/网站的伸缩性架构.md)
- [网站的可扩展架构](architecture/网站的可扩展架构.md)
- [网站的安全架构](architecture/网站的安全架构.md) - 关键词：XSS、CSRF、SQL 注入、DoS、消息摘要、加密算法、证书
- [网站典型故障](architecture/网站典型故障.md)

## ☕ JavaEE

> [JavaEE](javaee/) 技术——Java Web 的基石

- [JavaEE 面经](javaee/javaee-interview.md)
- [JavaEE 之 Servlet 指南](javaee/javaee-servlet.md)
- [JavaEE 之 Jsp 指南](javaee/javaee-jsp.md)
- [JavaEE 之 Filter 和 Listener](javaee/javaee-filter-listener.md)
- [JavaEE 之 Cookie 和 Session](javaee/javaee-cookie-sesion.md)

## ⭐️ 主流技术

> [主流技术](standalone/)，典型的技术如：SSM 框架、SSH 框架。

- MVC
  - [spring-tutorial](https://dunwu.gitbooks.io/spring-tutorial/) - Spring 教程
  - [spring-boot-tutorial](https://dunwu.github.io/spring-boot-tutorial/) - Spring Boot 教程
- ORM
  - [Mybatis](standalone/orm/mybatis.md) - 一个支持普通 SQL 查询，存储过程和高级映射的优秀持久层框架。
- Security
  - [Shiro](standalone/security/shiro.md) - 安全框架，具有认证、授权、加密、会话管理功能。

## ✨ 分布式技术

> [分布式技术（Distributed）](distributed/)，典型的技术如：分布式缓存、分布式消息队列、分布式服务、分布式搜索引擎等。

- [分布式技术面试题](distributed/分布式技术面试题.md)

- [分布式缓存（CACHE）](distributed/cache)
  - [分布式缓存](distributed/cache/分布式缓存.md)
  - [Redis](distributed/cache/redis.md)
  - Memcached
- [分布式服务（RPC）](distributed/rpc)
  - [Dubbo](distributed/rpc/dubbo.md) - 基于 Java 开发的高性能 RPC 框架。
  - [ZooKeeper 实战篇](distributed/rpc/zookeeper-basics.md)
  - [ZooKeeper 原理篇](distributed/rpc/zookeeper-advanced.md)
- [分布式消息队列（MQ）](distributed/mq)

  - 原理
    - [分布式消息队列](distributed/mq/分布式消息队列.md)
  - Kafka
    - [Kafka 实战篇](distributed/mq/kafka/kafka-basics.md)
    - [Kafka 原理篇](distributed/mq/kafka/kafka-advanced.md)
    - [Kafka Cheat Sheet](distributed/mq/kafka/kafka-cheat-sheet.md)
  - [RocketMQ 实战篇](distributed/mq/rocketmq-basics.md)
  - [RocketMQ 原理篇](distributed/mq/rocketmq-basics.md)
  - [ActiveMQ 实战篇](distributed/mq/ActiveMQ.md)
  - RabbitMQ - 待补充。。。

## 🕸 网络通信

- **网络分层**
  - [计算机网络概述](network/network-guide.md)
  - [计算机网络之物理层](network/network-physical.md)
  - [计算机网络之链路层](network/network-data-link.md)
  - [计算机网络之网络层](network/network-network.md)
  - [计算机网络之传输层](network/network-transport.md)
  - [计算机网络之应用层](network/network-application.md)
- **重要协议**
  - [网络协议之 HTTP](network/http.md)
  - [网络协议之 DNS](network/dns.md)
  - [网络协议之 ICMP](network/icmp.md)
- **网络技术**
  - [CDN 详解](network/cdn.md)

## 💻 服务器

> [服务器](server/) 章节总结 Java Web 领域主流服务器技术。

- [Nginx 简易教程](https://github.com/dunwu/nginx-tutorial) - 轻量级的 Web 服务器、反向代理服务器及电子邮件（IMAP/POP3）代理服务器，支持负载均衡。
- [Tomcat 应用指南](server/tomcat.md) - 轻量级的应用服务器
- [Jetty 应用指南](server/jetty.md) - 比 Tomcat 更轻量级的应用服务器
