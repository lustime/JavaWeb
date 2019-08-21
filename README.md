# JavaWeb

> JavaWeb 开发之路经验总结。
>
> - :repeat: 项目同步维护：[Github](https://github.com/dunwu/javaweb/) | [Gitee](https://gitee.com/turnon/javaweb/)
> - :book: 电子书阅读：[Github Pages](https://dunwu.github.io/javaweb/) | [Gitee Pages](http://turnon.gitee.io/javaweb/)

|        🔰         |            🏗            |            ✨            |             ⭐️             |          ☕          |            🕸            |
| :---------------: | :---------------------: | :----------------------: | :-------------------------: | :------------------: | :---------------------: |
| [准备](#🔰️-准备) | [架构设计](#🏗-架构设计) | [系统原理](#✨-系统原理) | [分布式技术](#⭐️-主流技术) | [JavaEE](#☕-javaee) | [网络通信](#🕸-网络通信) |

## 🔰️ 准备

作为 Java Web 工程师，你应该多多少少掌握一些的知识：

- [JavaCore](https://dunwu.github.io/javacore/) - Java 核心技术
- [前端技术指南](https://github.com/dunwu/frontend-tutorial) - 即使是后端工程师，也难免会接触到前端技术。前端技术五花八门，如：React、Vue、Angular、Webpack、ES6、Babel、Node.js 等等。不说掌握，至少也应该知道这些技术是什么。

## 🏗 架构设计

> [架构设计](docs/architecture) 整理架构设计方面的一些学习总结和心得。

- [大型网站架构概述](docs/architecture/大型网站架构概述.md)
- [网站的高性能架构](docs/architecture/网站的高性能架构.md)
- [网站的高可用架构](docs/architecture/网站的高可用架构.md)
- [网站的伸缩性架构](docs/architecture/网站的伸缩性架构.md)
- [网站的可扩展架构](docs/architecture/网站的可扩展架构.md)
- [网站的安全架构](docs/architecture/网站的安全架构.md) - 关键词：XSS、CSRF、SQL 注入、DoS、消息摘要、加密算法、证书
- [网站典型故障](docs/architecture/网站典型故障.md)

## ⭐️ 主流技术

### Web 框架

- [Spring 教程](https://dunwu.github.io/spring-tutorial/) 📚
- [Spring Boot 教程](https://dunwu.github.io/spring-boot-tutorial/) 📚

### 服务器

- [Tomcat 应用指南](docs/technology/server/tomcat.md)
- [Jetty 应用指南](docs/technology/server/jetty.md)
- [Nginx 简易教程](https://github.com/dunwu/nginx-tutorial) 📚

### 消息队列

- [消息队列原理](docs/technology/mq/mq-theory.md)
- Kafka
  - [Kafka 实战篇](docs/technology/mq/kafka/kafka-basics.md)
  - [Kafka 原理篇](docs/technology/mq/kafka/kafka-advanced.md)
  - [Kafka Cheat Sheet](docs/technology/mq/kafka/kafka-cheat-sheet.md)
- [RocketMQ 实战篇](docs/technology/mq/rocketmq-basics.md)
- [RocketMQ 原理篇](docs/technology/mq/rocketmq-basics.md)
- [ActiveMQ 实战篇](docs/technology/mq/ActiveMQ.md)

### 缓存

- [缓存面经](docs/technology/cache/cache-interview.md)
- [缓存原理](docs/technology/cache/cache-theory.md)
- [Redis](https://github.com/dunwu/db-tutorial/tree/master/docs/nosql/redis)
- Ehcache

### RPC

- [ZooKeeper 应用指南](docs/technology/rpc/zookeeper.md)
- [Dubbo 应用指南](docs/technology/rpc/dubbo.md)
- Spring Cloud

### 安全

- [系统安全原理](docs/technology/security/security-theory.md)
- [Shiro 应用指南](docs/technology/security/shiro.md)
- [Spring Security 应用指南](docs/technology/security/spring-security.md)

### ORM

- [Mybatis 应用指南](docs/technology/orm/mybatis.md)

## ☕ JavaEE

> [JavaEE](docs/javaee) 技术——Java Web 的基石

- [JavaEE 面经](docs/javaee/javaee-interview.md)
- [JavaEE 之 Servlet 指南](docs/javaee/javaee-servlet.md)
- [JavaEE 之 Jsp 指南](docs/javaee/javaee-jsp.md)
- [JavaEE 之 Filter 和 Listener](docs/javaee/javaee-filter-listener.md)
- [JavaEE 之 Cookie 和 Session](docs/javaee/javaee-cookie-sesion.md)

## 🕸 网络通信

- **面试**
  - [网络通信面经](docs/network/network-interview.md)
- **网络分层**
  - [计算机网络概述](docs/network/network-guide.md)
  - [计算机网络之物理层](docs/network/network-physical.md)
  - [计算机网络之链路层](docs/network/network-data-link.md)
  - [计算机网络之网络层](docs/network/network-network.md)
  - [计算机网络之传输层](docs/network/network-transport.md)
  - [计算机网络之应用层](docs/network/network-application.md)
- **重要技术**
  - [超文本传输协议 HTTP](docs/network/http.md)
  - [域名系统 DNS](docs/network/dns.md)
  - [传输控制协议 TCP](docs/network/tcp.md)
  - [用户数据报协议 UDP](docs/network/udp.md)
  - [网际控制报文协议 ICMP](docs/network/icmp.md)
  - [网络协议之 WebSocket](docs/network/websocket.md)
  - [CDN 详解](docs/network/cdn.md)
