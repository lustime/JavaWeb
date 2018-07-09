# Dubbo

## Dubbo 的实现过程？

<div align="center">
<img src="https://raw.githubusercontent.com/dunwu/JavaWeb/master/images/distributed/rpc/dubbo/dubbo基本架构.png" width="500"/>
</div>

节点角色：

| 节点      | 角色说明                               |
| --------- | -------------------------------------- |
| Provider  | 暴露服务的服务提供方                   |
| Consumer  | 调用远程服务的服务消费方               |
| Registry  | 服务注册与发现的注册中心               |
| Monitor   | 统计服务的调用次数和调用时间的监控中心 |
| Container | 服务运行容器                           |

调用关系：

1.  务容器负责启动，加载，运行服务提供者。
2.  服务提供者在启动时，向注册中心注册自己提供的服务。
3.  服务消费者在启动时，向注册中心订阅自己所需的服务。
4.  注册中心返回服务提供者地址列表给消费者，如果有变更，注册中心将基于长连接推送变更数据给消费者。
5.  服务消费者，从提供者地址列表中，基于软负载均衡算法，选一台提供者进行调用，如果调用失败，再选另一台调用。
6.  服务消费者和提供者，在内存中累计调用次数和调用时间，定时每分钟发送一次统计数据到监控中心。

## Dubbo 负载均衡策略有哪些？

#### Random

- 随机，按权重设置随机概率。
- 在一个截面上碰撞的概率高，但调用量越大分布越均匀，而且按概率使用权重后也比较均匀，有利于动态调整提供者权重。

#### RoundRobin

- 轮循，按公约后的权重设置轮循比率。
- 存在慢的提供者累积请求的问题，比如：第二台机器很慢，但没挂，当请求调到第二台时就卡在那，久而久之，所有请求都卡在调到第二台上。

#### LeastActive

- 最少活跃调用数，相同活跃数的随机，活跃数指调用前后计数差。
- 使慢的提供者收到更少请求，因为越慢的提供者的调用前后计数差会越大。

#### ConsistentHash

- 一致性 Hash，相同参数的请求总是发到同一提供者。
- 当某一台提供者挂时，原本发往该提供者的请求，基于虚拟节点，平摊到其它提供者，不会引起剧烈变动。
- 算法参见：<http://en.wikipedia.org/wiki/Consistent_hashing>
- 缺省只对第一个参数 Hash，如果要修改，请配置 `<dubbo:parameter key="hash.arguments" value="0,1" />`
- 缺省用 160 份虚拟节点，如果要修改，请配置 `<dubbo:parameter key="hash.nodes" value="320" />`

## Dubbo 集群容错策略 ？

![img](https://raw.githubusercontent.com/dunwu/JavaWeb/master/images/distributed/rpc/dubbo/dubbo%E9%9B%86%E7%BE%A4%E5%AE%B9%E9%94%99.jpg)

- **Failover** - 失败自动切换，当出现失败，重试其它服务器。通常用于读操作，但重试会带来更长延迟。可通过 retries="2" 来设置重试次数(不含第一次)。
- **Failfast** - 快速失败，只发起一次调用，失败立即报错。通常用于非幂等性的写操作，比如新增记录。
- **Failsafe** - 失败安全，出现异常时，直接忽略。通常用于写入审计日志等操作。
- **Failback** - 失败自动恢复，后台记录失败请求，定时重发。通常用于消息通知操作。
- **Forking** - 并行调用多个服务器，只要一个成功即返回。通常用于实时性要求较高的读操作，但需要浪费更多服务资源。可通过 forks="2" 来设置最大并行数。
- **Broadcast** - 播调用所有提供者，逐个调用，任意一台报错则报错。通常用于通知所有提供者更新缓存或日志等本地资源信息。

## 动态代理策略？

Dubbo 作为 RPC 框架，首先要完成的就是跨系统，跨网络的服务调用。消费方与提供方遵循统一的接口定义，消费方调用接口时，Dubbo 将其转换成统一格式的数据结构，通过网络传输，提供方根据规则找到接口实现，通过反射完成调用。也就是说，消费方获取的是对远程服务的一个代理(Proxy)，而提供方因为要支持不同的接口实现，需要一个包装层(Wrapper)。调用的过程大概是这样：

![img](https://oscimg.oschina.net/oscnet/bef19cd5a31b5ae13aff35a8cb4898faaf0.jpg)

消费方的 Proxy 和提供方的 Wrapper 得以让 Dubbo 构建出复杂、统一的体系。而这种动态代理与包装也是通过基于 SPI 的插件方式实现的，它的接口就是**ProxyFactory**。

```
@SPI("javassist")
public interface ProxyFactory {

    @Adaptive({Constants.PROXY_KEY})
    <T> T getProxy(Invoker<T> invoker) throws RpcException;

    @Adaptive({Constants.PROXY_KEY})
    <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) throws RpcException;

}
```

ProxyFactory 有两种实现方式，一种是基于 JDK 的代理实现，一种是基于 javassist 的实现。ProxyFactory 接口上定义了@SPI("javassist")，默认为 javassist 的实现。

## Dubbo 支持哪些序列化协议？Hessian？Hessian 的数据结构？

1.  dubbo 序列化，阿里尚不成熟的 java 序列化实现。
2.  hessian2 序列化：hessian 是一种跨语言的高效二进制的序列化方式，但这里实际不是原生的 hessian2 序列化，而是阿里修改过的 hessian lite，它是 dubbo RPC 默认启用的序列化方式。
3.  json 序列化：目前有两种实现，一种是采用的阿里的 fastjson 库，另一种是采用 dubbo 中自已实现的简单 json 库，一般情况下，json 这种文本序列化性能不如二进制序列化。
4.  java 序列化：主要是采用 JDK 自带的 java 序列化实现，性能很不理想。
5.  Kryo 和 FST：Kryo 和 FST 的性能依然普遍优于 hessian 和 dubbo 序列化。

Hessian 序列化与 Java 默认的序列化区别？

Hessian 是一个轻量级的 remoting on http 工具，采用的是 Binary RPC 协议，所以它很适合于发送二进制数据，同时又具有防火墙穿透能力。

1.  Hessian 支持跨语言串行
2.  比 java 序列化具有更好的性能和易用性
3.  支持的语言比较多

## Protoco Buffer 是什么？

Protocol Buffer 是 Google 出品的一种轻量 & 高效的结构化数据存储格式，性能比 Json、XML 真的强！太！多！

Protocol Buffer 的序列化 & 反序列化简单 & 速度快的原因是：

1.  编码 / 解码 方式简单（只需要简单的数学运算 = 位移等等）
2.  采用 Protocol Buffer 自身的框架代码 和 编译器 共同完成

Protocol Buffer 的数据压缩效果好（即序列化后的数据量体积小）的原因是：

1.  采用了独特的编码方式，如 Varint、Zigzag 编码方式等等
2.  采用 T - L - V 的数据存储方式：减少了分隔符的使用 & 数据存储得紧凑

## 注册中心挂了可以继续通信吗？

可以。Dubbo 消费者在应用启动时会从注册中心拉取已注册的生产者的地址接口，并缓存在本地。每次调用时，按照本地存储的地址进行调用。

## ZooKeeper 原理是什么？ZooKeeper 有什么用？

## Netty 有什么用？NIO/BIO/AIO 有什么用？有什么区别？

Netty 是一个“网络通讯框架”。

Netty进行事件处理的流程。`Channel`是连接的通道，是ChannelEvent的产生者，而`ChannelPipeline`可以理解为ChannelHandler的集合。

![event driven in Netty](https://camo.githubusercontent.com/5f7331d15c79fba29474c5be6e9e86db465637c3/687474703a2f2f7374617469632e6f736368696e612e6e65742f75706c6f6164732f73706163652f323031332f303932312f3137343033325f313872625f3139303539312e706e67)

> 参考：https://github.com/code4craft/netty-learning/blob/master/posts/ch1-overview.md

IO的方式通常分为几种：

- 同步阻塞的BIO
- 同步非阻塞的NIO
- 异步非阻塞的AIO

在使用同步I/O的网络应用中，如果要同时处理多个客户端请求，或是在客户端要同时和多个服务器进行通讯，就必须使用多线程来处理。 

NIO基于Reactor，当socket有流可读或可写入socket时，操作系统会相应的通知引用程序进行处理，应用再将流读取到缓冲区或写入操作系统。也就是说，这个时候，已经不是一个连接就要对应一个处理线程了，而是有效的请求，对应一个线程，当连接没有数据时，是没有工作线程来处理的。

与NIO不同，当进行读写操作时，只须直接调用API的read或write方法即可。这两种方法均为异步的，对于读操作而言，当有流可读取时，操作系统会将可读的流传入read方法的缓冲区，并通知应用程序；对于写操作而言，当操作系统将write方法传递的流写入完毕时，操作系统主动通知应用程序。  即可以理解为，read/write方法都是异步的，完成后会主动调用回调函数。

> 参考：https://blog.csdn.net/skiof007/article/details/52873421

## 为什么要进行系统拆分？拆分不用 Dubbo 可以吗？

系统拆分从资源角度分为：应用拆分和数据库拆分。

从采用的先后顺序可分为：水平扩展、垂直拆分、业务拆分、水平拆分。

![img](http://misc.linkedkeeper.com/misc/img/blog/201804/linkedkeeper0_9c2ed2ed-6156-40f7-ad08-20af067047ca.jpg)

是否使用服务依据实际业务场景来决定。

当垂直应用越来越多，应用之间交互不可避免，将核心业务抽取出来，作为独立的服务，逐渐形成稳定的服务中心，使前端应用能更快速的响应多变的市场需求。此时，用于提高业务复用及整合的分布式服务框架(RPC)是关键。 

当服务越来越多，容量的评估，小服务资源的浪费等问题逐渐显现，此时需增加一个调度中心基于访问压力实时管理集群容量，提高集群利用率。此时，用于提高机器利用率的资源调度和治理中心(SOA)是关键。 

## Dubbo 和 Thrift 有什么区别？

- Thrift 是跨语言的 RPC 框架。
- Dubbo 支持服务治理，而 Thrift 不支持。
