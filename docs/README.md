# JavaWeb

> JavaWeb 开发之路经验总结。
>
> - 🔁 项目同步维护：[Github](https://github.com/dunwu/javaweb/) | [Gitee](https://gitee.com/turnon/javaweb/)
> - 📖 电子书阅读：[Github Pages](https://dunwu.github.io/javaweb/) | [Gitee Pages](http://turnon.gitee.io/javaweb/)

|        🔰         |            🎨            |            ✨            |           ⭐️            |          ☕          |
| :---------------: | :----------------------: | :----------------------: | :----------------------: | :------------------: |
| [准备](#🔰️-准备) | [架构设计](#🎨-架构设计) | [系统原理](#✨-系统原理) | [主流技术](#⭐-主流技术) | [JavaEE](#☕-JavaEE) |

## 🔰️ 准备

作为 Java Web 工程师，你应该多多少少掌握一些的知识：

- [JavaCore](https://dunwu.github.io/javacore/) - Java 核心技术
- [前端技术指南](https://github.com/dunwu/frontend-tutorial) - 即使是后端工程师，也难免会接触到前端技术。前端技术五花八门，如：React、Vue、Angular、Webpack、ES6、Babel、Node.js 等等。不说掌握，至少也应该知道这些技术是什么。

### 网络通信

- **面试**
  - [网络通信面经](https://github.com/dunwu/blog/blob/master/source/_posts/communication/network-interview.md)
- **网络分层**
  - [计算机网络指南](https://github.com/dunwu/blog/blob/master/source/_posts/communication/network-guide.md) - 关键词：核心概念、拓扑结构、作用范围、性能指标、体系结构
  - [计算机网络之物理层](https://github.com/dunwu/blog/blob/master/source/_posts/communication/network-physical.md) - 关键词：调制、解调、数字信号、模拟信号、通信媒介、信道复用
  - [计算机网络之链路层](https://github.com/dunwu/blog/blob/master/source/_posts/communication/network-data-link.md) - 关键词：点对点信道、广播信道、`PPP`、`CSMA/CD`、局域网、以太网、`MAC`、适配器、集线器、网桥、交换机
  - [计算机网络之网络层](https://github.com/dunwu/blog/blob/master/source/_posts/communication/network-network.md) - 关键词：`IP`、`ICMP`、`ARP`、路由
  - [计算机网络之传输层](https://github.com/dunwu/blog/blob/master/source/_posts/communication/network-transport.md) - 关键词：`UDP`、`TCP`、滑动窗口、拥塞控制、三次握手
  - [计算机网络之应用层](https://github.com/dunwu/blog/blob/master/source/_posts/communication/network-application.md) - 关键词：`HTTP`、`DNS`、`FTP`、`TELNET`、`DHCP`
- **重要技术**
  - [网络协议之 HTTP](https://github.com/dunwu/blog/blob/master/source/_posts/communication/http.md)
  - [网络协议之 DNS](https://github.com/dunwu/blog/blob/master/source/_posts/communication/dns.md)
  - [网络协议之 TCP](https://github.com/dunwu/blog/blob/master/source/_posts/communication/tcp.md)
  - [网络协议之 UDP](https://github.com/dunwu/blog/blob/master/source/_posts/communication/udp.md)
  - [网络协议之 ICMP](https://github.com/dunwu/blog/blob/master/source/_posts/communication/icmp.md)
  - [网络协议之 WebSocket](https://github.com/dunwu/blog/blob/master/source/_posts/communication/websocket.md)
  - [网络通信之 CDN](https://github.com/dunwu/blog/blob/master/source/_posts/communication/cdn.md)

## 🎨 架构设计

> [🎨架构设计](https://github.com/dunwu/blog/tree/master/source/_posts/design/architecture) 整理架构设计方面的一些学习总结和心得。

- [分布式技术面试题](https://github.com/dunwu/blog/blob/master/source/_posts/design/architecture/分布式技术面试题.md)
- [分布式技术实现](https://github.com/dunwu/blog/blob/master/source/_posts/design/architecture/分布式技术实现.md)
- [分布式架构](https://github.com/dunwu/blog/blob/master/source/_posts/design/architecture/分布式架构.md)
- [大型分布式网站架构](https://github.com/dunwu/blog/blob/master/source/_posts/design/architecture/大型分布式网站架构.md)
- [大型网站架构概述](https://github.com/dunwu/blog/blob/master/source/_posts/design/architecture/大型网站架构概述.md)
- [网站典型故障](https://github.com/dunwu/blog/blob/master/source/_posts/design/architecture/网站典型故障.md)
- [网站的伸缩性架构](https://github.com/dunwu/blog/blob/master/source/_posts/design/architecture/网站的伸缩性架构.md)
- [网站的可扩展架构](https://github.com/dunwu/blog/blob/master/source/_posts/design/architecture/网站的可扩展架构.md)
- [网站的安全架构](https://github.com/dunwu/blog/blob/master/source/_posts/design/architecture/网站的安全架构.md)
- [网站的高可用架构](https://github.com/dunwu/blog/blob/master/source/_posts/design/architecture/网站的高可用架构.md)
- [网站的高性能架构](https://github.com/dunwu/blog/blob/master/source/_posts/design/architecture/网站的高性能架构.md)

## ✨ 系统原理

> [✨系统原理](https://github.com/dunwu/blog/tree/master/source/_posts/design/theory) 整理系统的基本原理，为架构设计，技术应用提供理论支撑。

- [系统原理面试题](https://github.com/dunwu/blog/blob/master/source/_posts/design/theory/system-theory-interview.md)
- [分布式基本原理](https://github.com/dunwu/blog/blob/master/source/_posts/design/theory/distributed-base-theory.md)
- [负载均衡基本原理](https://github.com/dunwu/blog/blob/master/source/_posts/design/theory/load-balance-theory.md)
- [缓存基本原理](https://github.com/dunwu/blog/blob/master/source/_posts/design/theory/cache-theory.md)
- [消息队列基本原理](https://github.com/dunwu/blog/blob/master/source/_posts/design/theory/mq-theory.md)
- [分布式锁基本原理](https://github.com/dunwu/blog/blob/master/source/_posts/design/theory/distributed-lock-theory.md)
- [分布式会话基本原理](https://github.com/dunwu/blog/blob/master/source/_posts/design/theory/distributed-session-theory.md)
- [分布式存储基本原理](https://github.com/dunwu/blog/blob/master/source/_posts/design/theory/distributed-storage-theory.md)
- [分布式 ID 基本原理](https://github.com/dunwu/blog/blob/master/source/_posts/design/theory/distributed-id-theory.md)
- [分布式事务基本原理](https://github.com/dunwu/blog/blob/master/source/_posts/design/theory/distributed-transaction-theory.md)
- [分库分表基本原理](https://github.com/dunwu/blog/blob/master/source/_posts/design/theory/sharding-theory.md)
- [常见安全手段基本原理](https://github.com/dunwu/blog/blob/master/source/_posts/design/theory/security-theory.md)

## ⭐ 主流技术

> [主流技术](technology) 汇集 JavaWeb 开发常用的各种主流技术。

### Web 框架

- [Spring 教程 📚](https://dunwu.github.io/spring-tutorial/)
- [Spring Boot 教程 📚](https://dunwu.github.io/spring-boot-tutorial/)

### 服务器

- [Tomcat 应用指南](technology/server/Tomcat.md)
- [Jetty 应用指南](technology/server/Jetty.md)
- [Nginx 简易教程 📚](https://github.com/dunwu/nginx-tutorial)

### 消息队列

- [消息队列面经](technology/mq/MqInterview.md)
- [Kafka 应用指南基础篇](technology/mq/kafka-basic.md)
- [Kafka 应用指南进阶篇](technology/mq/kafka-advance.md)
- [Kafka 运维指南](technology/mq/kafka-ops.md)
- [RocketMQ 基础篇](technology/mq/RocketmqBasics.md)
- [RocketMQ 进阶篇](technology/mq/RocketmqAdvanced.md)
- [ActiveMQ 实战篇](technology/mq/ActiveMQ.md)

### 缓存

- [缓存面经](technology/cache/CacheInterview.md)
- [Redis](technology/cache/Redis.md)
- [Ehcache](technology/cache/Ehcache.md)
- [Caffeine](technology/cache/Caffeine.md)

### RPC

- [Dubbo 应用指南](technology/rpc/Dubbo.md)
- Spring Cloud

### 安全

- [Shiro 应用指南](technology/security/Shiro.md)
- [Spring Security 应用指南](technology/security/SpringSecurity.md)

### 数据

- [Mybatis 应用指南](technology/data/Mybatis.md)
- [ShardingSphere 应用指南](technology/data/ShardingSphere.md)

### 协调/监控/诊断/测试

- 协调
  - [ZooKeeper 应用指南](technology/monitor/zookeeper.md)
  - [ZooKeeper 运维指南](technology/monitor/zookeeper-ops.md)
- 诊断
  - [Arthas 应用指南](technology/monitor/arthas.md)
- 测试
  - [Jmeter 应用指南](technology/monitor/jmeter.md)

## ☕ JavaEE

> [☕ JavaEE](javaee/README.md) 技术是 Java Web 的基石

- [JavaEE 面经](javaee/javaee-interview.md)
- [JavaEE 之 Servlet 指南](javaee/javaee-servlet.md)
- [JavaEE 之 Jsp 指南](javaee/javaee-jsp.md)
- [JavaEE 之 Filter 和 Listener](javaee/javaee-filter-listener.md)
- [JavaEE 之 Cookie 和 Session](javaee/javaee-cookie-sesion.md)
