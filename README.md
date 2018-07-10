# JavaWeb

> Java Web 开发之路经验总结

| Ⅰ                 | Ⅱ                         | Ⅲ                         | Ⅳ             |
| ----------------- | ------------------------- | ------------------------- | ------------- |
| [JavaEE](#JavaEE) | [单点式技术](#单点式技术) | [分布式技术](#分布式技术) | [工具](#工具) |

## [JavaEE](docs/javaee/)

> JavaEE 技术——Java Web 的基石

## [单点式技术](docs/standalone/)

> 单点式技术（Standalone），典型的技术如：SSM 框架、SSH 框架。

- Platform
  - [x] [Spring](https://github.com/dunwu/spring-notes) - JavaSE/JavaEE 一站式开发框架。
- ORM
  - [x] [Mybatis](docs/standalone/orm/mybatis.md) - 一个支持普通 SQL 查询，存储过程和高级映射的优秀持久层框架。
  - [ ] Hibernate
- 安全
  - [x] [Shiro](docs/standalone/security/shiro.md) - 安全框架，具有认证、授权、加密、会话管理功能。

## [分布式技术](docs/distributed/)

> 分布式技术（Distributed），典型的技术如：分布式缓存、分布式消息队列、分布式服务、分布式搜索引擎等。

- 分布式
  - [x] [分布式原理](docs/distributed/分布式原理.md)
  - [x] [分布式架构](docs/distributed/分布式架构.md)
  - [x] [分布式技术实现](docs/distributed/分布式技术实现.md)
  - [x] [负载均衡](docs/distributed/负载均衡.md)
  - [x] [分布式技术面试题](docs/distributed/分布式技术面试题.md)
- 分布式缓存
  - [x] [分布式缓存](docs/distributed/分布式缓存.md)
  - [ ] Redis
  - [ ] Memcached
- [分布式服务（rpc）](docs/distributed/rpc)
  - [x] [Dubbo](docs/distributed/rpc/dubbo.md) - 基于 Java 开发的高性能 RPC 框架。
  - [x] [ZooKeeper](docs/distributed/rpc/ZooKeeper.md) - 一个分布式应用协调系统，已经用到了许多分布式项目中，用来完成统一命名服务、状态同步服务、集群管理、分布式应用配置项的管理等工作。
- [分布式消息队列（MQ）](docs/distributed/mq)
  - [x] [分布式消息队列](docs/distributed/mq/分布式消息队列.md)
  - [x] [Kafka](docs/distributed/mq/kafka.md) - 分布式的、可水平扩展的、基于发布/订阅模式的、支持容错的消息系统。
  - [ ] RocketMQ
  - [x] [ActiveMQ](docs/distributed/mq/ActiveMQ.md)
  - [ ] RabbitMQ
- 分布式搜索引擎
  - [ ] ElasticSearch

## [工具](docs/tools/)

> Java Web 领域常用工具。

- [x] [Nginx](https://github.com/dunwu/Nginx) - 轻量级的 Web 服务器、反向代理服务器及电子邮件（IMAP/POP3）代理服务器，支持负载均衡。
- [x] [Tomcat](docs/tools/tomcat.md) - 轻量级的应用服务器
- [x] [Jetty](docs/tools/jetty.md) - 比 Tomcat 更轻量级的应用服务器
