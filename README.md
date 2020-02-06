<p align="center">
    <a href="https://dunwu.github.io/javatech/#/" target="_blank" rel="noopener noreferrer">
        <img src="http://dunwu.test.upcdn.net/common/logo/java-logo.png" alt="logo" width="100px">
    </a>
</p>

<p align="center">
    <img src="https://badgen.net/github/license/dunwu/javatech" alt="license">
</p>

<h1 align="center">JavaTech</h1>

> ☕ **javatech** 汇总了 Java 开发中常见的主流技术的应用、特性、原理。
>
> - 🔁 项目同步维护：[Github](https://github.com/dunwu/javatech/) | [Gitee](https://gitee.com/turnon/javaweb/)
> - 📖 电子书阅读：[Github Pages](https://dunwu.github.io/javatech/) | [Gitee Pages](http://turnon.gitee.io/javaweb/)
>
> 说明：下面的内容清单中，凡是有 📚 标记的技术，都已整理成详细的教程。

## Java 生态

### 框架

- [Spring](https://dunwu.github.io/spring-tutorial/) 📚
- [Spring Boot](https://dunwu.github.io/spring-boot-tutorial/) 📚
- [Mybatis](docs/ecology/framework/mybatis.md)
- [ShardingSphere](docs/ecology/storage/shardingsphere.md)

### 消息队列

- [消息队列面经](docs/ecology/mq/MqInterview.md)
- [Kafka 应用指南基础篇](docs/ecology/mq/kafka-basic.md)
- [Kafka 应用指南进阶篇](docs/ecology/mq/kafka-advance.md)
- [Kafka 运维指南](docs/ecology/mq/kafka-ops.md)
- [RocketMQ 基础篇](docs/ecology/mq/RocketmqBasics.md)
- [RocketMQ 进阶篇](docs/ecology/mq/RocketmqAdvanced.md)
- [ActiveMQ 实战篇](docs/ecology/mq/ActiveMQ.md)

### 缓存

- [缓存面经](docs/ecology/cache/CacheInterview.md)
- [Redis](docs/ecology/cache/Redis.md)
- [Ehcache](docs/ecology/cache/Ehcache.md)
- [Caffeine](docs/ecology/cache/Caffeine.md)

### 安全

> Java 领域比较流行的安全框架就是 shiro 和 spring-security。
>
> shiro 更为简单、轻便，容易理解，能满足大多数基本安全场景下的需要。
>
> spring-security 功能更丰富，也比 shiro 更复杂。值得一提的是由于 spring-security 是 spring 团队开发，所以集成 spring 和 spring-boot 框架更容易。

- [shiro](docs/ecology/security/shiro.md)
- [spring-security](docs/ecology/security/spring-security.md)

### 微服务

- [Dubbo](docs/ecology/microservices/dubbo.md)
- Spring Cloud
- [ZooKeeper 应用指南](docs/ecology/microservices/zookeeper.md)
- [ZooKeeper 运维指南](docs/ecology/microservices/zookeeper-ops.md)

### 测试

- [Junit](docs/ecology/test/junit.md)
- [Mockito](docs/ecology/test/mockito.md)
- [JMH](docs/ecology/test/jmh.md)

### 服务器

> Tomcat 和 Jetty 都是 Java 比较流行的轻量级服务器。
>
> Nginx 是目前最流行的反向代理服务器，也常用于负载均衡。

- [Tomcat](docs/ecology/server/tomcat.md)
- [Jetty](docs/ecology/server/jetty.md)
- [Nginx](https://github.com/dunwu/nginx-tutorial) 📚

## Java 工具

### 构建

> Java 项目需要通过 [**构建工具**](docs/tool/build) 来管理项目依赖，完成编译、打包、发布、生成 JavaDoc 等任务。
>
> - 目前最主流的构建工具是 Maven，它的功能非常强大。
> - Gradle 号称是要替代 Maven 等构件工具，它的版本管理确实简洁，但是需要学习 Groovy，学习成本比 Maven 高。
> - Ant 功能比 Maven 和 Gradle 要弱，现代 Java 项目基本不用了，但也有一些传统的 Java 项目还在使用。

- [Maven](docs/tool/build/maven) 📚
- [Ant](docs/tool/build/ant.md)

### IDE

> 自动有了 [**IDE**](docs/tool/ide)，写代码从此就告别了刀耕火种的蛮荒时代。
>
> - [Eclipse](docs/tool/ide/eclipse.md) 是久负盛名的开源 Java IDE，我的学生时代一直使用它写 Java。
> - 曾经抗拒从转 [Intellij Idea](docs/tool/ide/intellij-idea.md) ，但后来发现真香，不得不说，确实是目前最优秀的 Java IDE。
> - 你可以在 [vscode](docs/tool/ide/vscode.md) 中写各种语言，只要安装相应插件即可。如果你的项目中使用了很多种编程语言，又懒得在多个 IDE 之间切换，那么就用 vscode 来一网打尽吧。

- [Intellij Idea](docs/tool/ide/intellij-idea.md)
- [Eclipse](docs/tool/ide/eclipse.md)
- [vscode](docs/tool/ide/vscode.md)

### 监控

- [Arthas](docs/tool/monitor/arthas.md)
- [SkyWalking](docs/tool/monitor/skywalking.md)

## JavaEE

> [☕ JavaEE](docs/javaee/README.md) 技术是 Java Web 的基石

- [JavaEE 面经](docs/javaee/javaee-interview.md)
- [Servlet](docs/javaee/javaee-servlet.md)
- [Jsp](docs/javaee/javaee-jsp.md)
- [Filter 和 Listener](docs/javaee/javaee-filter-listener.md)
- [Cookie 和 Session](docs/javaee/javaee-cookie-sesion.md)
