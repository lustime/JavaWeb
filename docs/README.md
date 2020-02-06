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
> - 🔁 项目同步维护：[Github](https://github.com/dunwu/javatech/) | [Gitee](https://gitee.com/turnon/javaweb/)
> - 📖 电子书阅读：[Github Pages](https://dunwu.github.io/javatech/) | [Gitee Pages](http://turnon.gitee.io/javaweb/)

## Java 生态

### 框架

- [Spring](https://dunwu.github.io/spring-tutorial/) 📚
- [Spring Boot](https://dunwu.github.io/spring-boot-tutorial/) 📚
- [Mybatis](ecology/framework/mybatis.md)
- [ShardingSphere](ecology/storage/shardingsphere.md)

### 消息队列

- [消息队列面经](ecology/mq/MqInterview.md)
- [Kafka 应用指南基础篇](ecology/mq/kafka-basic.md)
- [Kafka 应用指南进阶篇](ecology/mq/kafka-advance.md)
- [Kafka 运维指南](ecology/mq/kafka-ops.md)
- [RocketMQ 基础篇](ecology/mq/RocketmqBasics.md)
- [RocketMQ 进阶篇](ecology/mq/RocketmqAdvanced.md)
- [ActiveMQ 实战篇](ecology/mq/ActiveMQ.md)

### 缓存

- [缓存面经](ecology/cache/CacheInterview.md)
- [Redis](ecology/cache/Redis.md)
- [Ehcache](ecology/cache/Ehcache.md)
- [Caffeine](ecology/cache/Caffeine.md)

### 安全

> Java 领域比较流行的安全框架就是 shiro 和 spring-security。
>
> shiro 更为简单、轻便，容易理解，能满足大多数基本安全场景下的需要。
>
> spring-security 功能更丰富，也比 shiro 更复杂。值得一提的是由于 spring-security 是 spring 团队开发，所以集成 spring 和 spring-boot 框架更容易。

- [shiro](ecology/security/shiro.md)
- [spring-security](ecology/security/spring-security.md)

### 微服务

- [Dubbo](ecology/microservices/dubbo.md)
- Spring Cloud
- [ZooKeeper 应用指南](ecology/microservices/zookeeper.md)
- [ZooKeeper 运维指南](ecology/microservices/zookeeper-ops.md)

### 测试

- [Junit](ecology/test/junit.md)
- [Mockito](ecology/test/mockito.md)
- [JMH](ecology/test/jmh.md)

### 服务器

> Tomcat 和 Jetty 都是 Java 比较流行的轻量级服务器。
>
> Nginx 是目前最流行的反向代理服务器，也常用于负载均衡。

- [Tomcat](ecology/server/tomcat.md)
- [Jetty](ecology/server/jetty.md)
- [Nginx](https://github.com/dunwu/nginx-tutorial) 📚

## Java 工具

### 构建

> [构建工具](tool/build)

- [Maven 教程 📚](tool/build/maven/README.md)
- [Maven 快速指南](tool/build/maven/maven-quickstart.md)
- [Maven 教程之 pom.xml 详解](tool/build/maven/maven-pom.md)
- [Maven 教程之 settings.xml 详解](tool/build/maven/maven-settings.md)
- [Maven 实战问题和最佳实践](tool/build/maven/maven-action.md)
- [Maven 教程之发布 jar 到私服或中央仓库](tool/build/maven/maven-deploy.md)
- [Maven 插件之代码检查](tool/build/maven/maven-checkstyle-plugin.md)
- [Ant 简易教程](tool/build/ant.md)

### IDE

> [IDE](tool/ide)

- [Intellij Idea](tool/ide/intellij-idea.md)
- [Eclipse](tool/ide/eclipse.md)
- [vscode](tool/ide/vscode.md)

## JavaEE

> [☕ JavaEE](javaee/README.md) 技术是 Java Web 的基石

- [JavaEE 面经](javaee/javaee-interview.md)
- [Servlet](javaee/javaee-servlet.md)
- [Jsp](javaee/javaee-jsp.md)
- [Filter 和 Listener](javaee/javaee-filter-listener.md)
- [Cookie 和 Session](javaee/javaee-cookie-sesion.md)
