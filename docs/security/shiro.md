# Shiro 应用指南

> Shiro 是一个安全框架，具有认证、授权、加密、会话管理功能。

<!-- TOC depthFrom:2 depthTo:3 -->

- [Shiro 核心组件](#shiro-核心组件)
- [Shiro 架构](#shiro-架构)
- [参考资料](#参考资料)

<!-- /TOC -->

## Shiro 核心组件

<p align="center">
  <img src="http://dunwu.test.upcdn.net/cs/java/javaweb/standalone/security/shiro/shiro-features.png!zp">
</p>

- **Authentication** - 身份认证/登录，验证用户是不是拥有相应的身份；
- **Authorization** - 授权，即权限验证，验证某个已认证的用户是否拥有某个权限；即判断用户是否能做事情，常见的如：验证某个用户是否拥有某个角色。或者细粒度的验证某个用户对某个资源是否具有某个权限；
- **Session Manager** - 会话管理，即用户登录后就是一次会话，在没有退出之前，它的所有信息都在会话中；会话可以是普通 JavaSE 环境的，也可以是如 Web 环境的；
- **Cryptography** - 加密，保护数据的安全性，如密码加密存储到数据库，而不是明文存储；
- **Web Support** - Web 支持，可以非常容易的集成到 Web 环境；
- **Caching** - 缓存，比如用户登录后，其用户信息、拥有的角色 / 权限不必每次去查，这样可以提高效率；
- **Concurrency** - shiro 支持多线程应用的并发验证，即如在一个线程中开启另一个线程，能把权限自动传播过去；
- **Testing** - 提供测试支持；
- **Run As** - 允许一个用户假装为另一个用户（如果他们允许）的身份进行访问；
- **Remember Me** - 记住我，这个是非常常见的功能，即一次登录后，下次再来的话不用登录了。

记住一点，Shiro 不会去维护用户、维护权限；这些需要我们自己去提供；然后通过相应的接口注入给 Shiro 即可。

## Shiro 架构

<p align="center">
  <img src="http://dunwu.test.upcdn.net/cs/java/javaweb/standalone/security/shiro/ShiroBasicArchitecture.png!zp">
</p>

- **Realm** - 域，Shiro 从从 Realm 获取安全数据（如用户、角色、权限），就是说 SecurityManager 要验证用户身份，那么它需要从 Realm 获取相应的用户进行比较以确定用户身份是否合法；也需要从 Realm 得到用户相应的角色/权限进行验证用户是否能进行操作；可以把 Realm 看成 DataSource，即安全数据源。

<p align="center">
  <img src="http://dunwu.test.upcdn.net/cs/java/javaweb/standalone/security/shiro/ShiroArchitecture.png!zp">
</p>

- **Subject** - 主题，负责 Shiro 的入口。
- **Realm** - Realm 可以视为数据源，所有安全相关的数据都从 Realm 获取。可以有一个或多个 Realm，可以是 JDBC 实现，也可以是 LDAP 实现，或者内存实现等等，由用户提供。注意：Shiro 不知道你的用户/权限存储在哪及以何种格式存储，所以用户需要在应用中都现自己的 Realm。
- **SecurityManager** - 它是 Shiro 的核心，所有与安全有关的操作（认证、授权、及会话、缓存的管理）都与 `SecurityManager` 交互，且它管理着所有 `Subject`。
  - **Authenticator** - 认证器，负责认证。如果用户需要定制认证策略，可以实现此接口。
  - **Authorizer** - 授权器，负责权限控制。用来决定主体是否有权限进行相应的操作；即控制着用户能访问应用中的哪些功能；
  - **SessionManager** - 会话管理器。Shiro 抽象了一个自己的 Session 来管理主体与应用之间交互的数据。
  - **SessionDAO** - 会话 DAO 用于实现用户定制化的会话 DAO，需要用户自己实现。
  - **CacheManager** - 缓存控制器。用于管理如用户、角色、权限等信息的缓存。
  - **Cryptography** - 密码器。用于对数据加密、解密。

## 参考资料

- [shiro 官方文档](http://shiro.apache.org/reference.html)
- [跟我学 Shiro](http://jinnianshilongnian.iteye.com/category/305053)
