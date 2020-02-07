---
home: true
heroImage: http://dunwu.test.upcdn.net/common/logo/java-logo.png
heroText: JavaTech
tagline: ☕ javatech 汇总了 Java 开发中常见的主流技术的应用、特性、原理。
actionLink: /
features:
  - title: Java 生态
    details: Java 开发中常见应用技术，如：框架、缓存、消息队列、存储、安全、微服务、测试、服务器等。
  - title: Java 工具
    details: Java 开发中常用工具，如：构建、IDE、监控等。
  - title: JavaEE
    details: Java Web 开发的基础技术：Servlet、Jsp、Filter、Listener 等。
footer: MIT Licensed | Copyright © 2018-present Dunwu
---

# JavaTech

![license](https://badgen.net/github/license/dunwu/javatech)

> ☕ **javatech** 汇总了 Java 开发中常见的主流技术的应用、特性、原理。
>
> - 🔁 项目同步维护：[Github](https://github.com/dunwu/javatech/) | [Gitee](https://gitee.com/turnon/javatech/)
> - 📖 电子书阅读：[Github Pages](https://dunwu.github.io/javatech/) | [Gitee Pages](http://turnon.gitee.io/javatech/)
>
> 说明：下面的内容清单中，凡是有 📚 标记的技术，都已整理成详细的教程。

## Java 生态

## 框架

- [Spring](https://dunwu.github.io/spring-tutorial/) 📚
- [Spring Boot](https://dunwu.github.io/spring-boot-tutorial/) 📚
- [Mybatis](framework/mybatis.md)
- [ShardingSphere](storage/shardingsphere.md)

## 消息队列

> 消息队列（Message Queue，简称 MQ）技术是分布式应用间交换信息的一种技术。
>
> 消息队列主要解决应用耦合，异步消息，流量削锋等问题，实现高性能，高可用，可伸缩和最终一致性架构。是大型分布式系统不可缺少的中间件。
>
> 如果想深入学习各种消息队列产品，建议先了解一下 [消息队列基本原理](https://github.com/dunwu/blog/blob/master/source/_posts/distributed/mq-theory.md) ，有助于理解消息队列特性的实现和设计思路。

- [消息队列面经](mq/mq-interview.md)
- [Kafka](mq/kafka)
- [RocketMQ](mq/rocketmq.md)
- [ActiveMQ](mq/activemq.md)

## 缓存

> 缓存可以说是优化系统性能的第一手段，在各种技术中都会有缓存的应用。
>
> 如果想深入学习缓存，建议先了解一下 [缓存基本原理](https://github.com/dunwu/blog/blob/master/source/_posts/distributed/cache-theory.md)，有助于理解缓存的特性、原理，使用缓存常见的问题及解决方案。

- [缓存面经](cache/CacheInterview.md)
- [Redis](cache/Redis.md)
- [Ehcache](cache/Ehcache.md)
- [Caffeine](cache/Caffeine.md)

## 微服务

- [Dubbo](soa/dubbo.md)
- Spring Cloud
- [ZooKeeper](soa/zookeeper)
- Eureka
- Consul
- Nacos
- Zuul
- Gateway

## 搜索引擎

- [ElasticSearch](search/elasticsearch)
- Solr
- Lucene

## 安全

> Java 领域比较流行的安全框架就是 shiro 和 spring-security。
>
> shiro 更为简单、轻便，容易理解，能满足大多数基本安全场景下的需要。
>
> spring-security 功能更丰富，也比 shiro 更复杂。值得一提的是由于 spring-security 是 spring 团队开发，所以集成 spring 和 spring-boot 框架更容易。

- [Shiro](security/shiro.md)
- [Spring Security](security/spring-security.md)

## 测试

- [Junit](test/junit.md)
- [Mockito](test/mockito.md)
- [JMH](test/jmh.md)

## 服务器

> Tomcat 和 Jetty 都是 Java 比较流行的轻量级服务器。
>
> Nginx 是目前最流行的反向代理服务器，也常用于负载均衡。

- [Tomcat](server/tomcat.md)
- [Jetty](server/jetty.md)
- [Nginx](https://github.com/dunwu/nginx-tutorial) 📚
