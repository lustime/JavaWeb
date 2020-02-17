# Spring Security

## 快速开始

参考：[Securing a Web Application](https://spring.io/guides/gs/securing-web/)

## 核心 API

## 框架

## Spring Boot 集成

`@EnableWebSecurity` 和 `@Configuration` 注解一起使用, 注解 `WebSecurityConfigurer` 类型的类。

或者利用`@EnableWebSecurity`注解继承 `WebSecurityConfigurerAdapter` 的类，这样就构成了 *Spring Security* 的配置。

- configure(WebSecurity)：通过重载该方法，可配置Spring Security的Filter链。
- configure(HttpSecurity)：通过重载该方法，可配置如何通过拦截器保护请求。

## 参考资料

- [Spring Security Architecture](https://spring.io/guides/topicals/spring-security-architecture)
- [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
