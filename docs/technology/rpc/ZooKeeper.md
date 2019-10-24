# ZooKeeper 应用指南

> ZooKeeper 是一个分布式应用协调系统，已经用到了许多分布式项目中，用来完成统一命名服务、状态同步服务、集群管理、分布式应用配置项的管理等工作。
>
> 本文侧重于总结 ZooKeeper 工作原理。

<!-- TOC depthFrom:2 depthTo:3 -->

- [概述](#概述)
    - [ZooKeeper 是什么？](#zookeeper-是什么)
    - [ZooKeeper 提供了什么？](#zookeeper-提供了什么)
    - [Zookeeper 的特性](#zookeeper-的特性)
    - [工作原理](#工作原理)
    - [Server 工作状态](#server-工作状态)
- [安装](#安装)
    - [下载解压 ZooKeeper](#下载解压-zookeeper)
    - [创建配置文件](#创建配置文件)
    - [启动 ZooKeeper 服务器](#启动-zookeeper-服务器)
    - [启动 CLI](#启动-cli)
    - [停止 ZooKeeper 服务器](#停止-zookeeper-服务器)
- [文件系统](#文件系统)
- [通知机制](#通知机制)
- [应用场景](#应用场景)
    - [统一命名服务（Name Service）](#统一命名服务name-service)
    - [配置管理（Configuration Management）](#配置管理configuration-management)
    - [集群管理（Group Membership）](#集群管理group-membership)
    - [分布式锁](#分布式锁)
    - [队列管理](#队列管理)
- [复制](#复制)
- [选举流程](#选举流程)
- [同步流程](#同步流程)
- [CLI](#cli)
    - [创建 Znodes](#创建-znodes)
    - [获取数据](#获取数据)
    - [Watch（监视）](#watch监视)
    - [设置数据](#设置数据)
    - [创建子项/子节点](#创建子项子节点)
    - [列出子项](#列出子项)
    - [检查状态](#检查状态)
    - [移除 Znode](#移除-znode)
- [API](#api)
    - [ZooKeeper API 的基础知识](#zookeeper-api-的基础知识)
    - [Java 绑定](#java-绑定)
    - [连接到 ZooKeeper 集合](#连接到-zookeeper-集合)
    - [创建 Znode](#创建-znode)
    - [Exists - 检查 Znode 的存在](#exists---检查-znode-的存在)
    - [getData 方法](#getdata-方法)
    - [setData 方法](#setdata-方法)
    - [getChildren 方法](#getchildren-方法)
    - [删除 Znode](#删除-znode)
- [资源](#资源)
    - [官方资源](#官方资源)
    - [文章](#文章)

<!-- /TOC -->

## 概述

### ZooKeeper 是什么？

ZooKeeper 作为一个分布式的服务框架，主要用来解决分布式集群中应用系统的一致性问题，它能提供基于类似于文件系统的目录节点树方式的数据存储，但是 ZooKeeper 并不是用来专门存储数据的，它的作用主要是用来维护和监控你存储的数据的状态变化。通过监控这些数据状态的变化，从而可以达到基于数据的集群管理。

<div align="center">
<img src="http://dunwu.test.upcdn.net/cs/java/javaweb/distributed/rpc/zookeeper/zookeeper-service.png!zp" />
</div>

### ZooKeeper 提供了什么？

1.  文件系统
2.  通知机制

### Zookeeper 的特性

- 最终一致性：client 不论连接到哪个 Server，展示给它都是同一个视图，这是 zookeeper 最重要的性能。
- 可靠性：具有简单、健壮、良好的性能，如果消息被到一台服务器接受，那么它将被所有的服务器接受。
- 实时性：Zookeeper 保证客户端将在一个时间间隔范围内获得服务器的更新信息，或者服务器失效的信息。但由于网络延时等原因，Zookeeper 不能保证两个客户端能同时得到刚更新的数据，如果需要最新数据，应该在读数据之前调用 sync()接口。
- 等待无关（wait-free）：慢的或者失效的 client 不得干预快速的 client 的请求，使得每个 client 都能有效的等待。
- 原子性：更新只能成功或者失败，没有中间状态。
- 顺序性：包括全局有序和偏序两种：全局有序是指如果在一台服务器上消息 a 在消息 b 前发布，则在所有 Server 上消息 a 都将在消息 b 前被发布；偏序是指如果一个消息 b 在消息 a 后被同一个发送者发布，a 必将排在 b 前面。

### 工作原理

ZooKeeper 的核心是原子广播，这个机制保证了各个 Server 之间的同步。实现这个机制的协议叫做 Zab 协议。Zab 协议有两种模式，它们分别是恢复模式（选主）和广播模式（同步）。当服务启动或者在领导者崩溃后，Zab 就进入了恢复模式，当领导者被选举出来，且大多数 Server 完成了和 leader 的状态同步以后，恢复模式就结束了。状态同步保证了 leader 和 Server 具有相同的系统状态。

为了保证事务的顺序一致性，ZooKeeper 采用了递增的事务 id 号（zxid）来标识事务。所有的提议（proposal）都在被提出的时候加上了 zxid。实现中 zxid 是一个 64 位的数字，它高 32 位是 epoch 用来标识 leader 关系是否改变，每次一个 leader 被选出来，它都会有一个新的 epoch，标识当前属于那个 leader 的统治时期。低 32 位用于递增计数。

### Server 工作状态

每个 Server 在工作过程中有三种状态：

- LOOKING - 当前 Server 不知道 leader 是谁，正在搜寻
- LEADING - 当前 Server 即为选举出来的 leader
- FOLLOWING - leader 已经选举出来，当前 Server 与之同步

## 安装

在安装 ZooKeeper 之前，请确保你的系统是在以下任一操作系统上运行：

- **任意 Linux OS** - 支持开发和部署。适合演示应用程序。
- **Windows OS** - 仅支持开发。
- **Mac OS** - 仅支持开发。

环境要求：JDK6+

安装步骤如下：

### 下载解压 ZooKeeper

进入官方下载地址：http://zookeeper.apache.org/releases.html#download ，选择合适版本。

解压到本地：

```
$ tar -zxf zookeeper-3.4.6.tar.gz
$ cd zookeeper-3.4.6
```

### 创建配置文件

你必须创建 `conf/zoo.cfg` 文件，否则启动时会提示你没有此文件。

初次尝试，不妨直接使用 Kafka 提供的模板配置文件 `conf/zoo_sample.cfg`：

```
$ cp conf/zoo_sample.cfg conf/zoo.cfg
```

### 启动 ZooKeeper 服务器

执行以下命令

```
$ bin/zkServer.sh start
```

执行此命令后，你将收到以下响应

```
$ JMX enabled by default
$ Using config: /Users/../zookeeper-3.4.6/bin/../conf/zoo.cfg
$ Starting zookeeper ... STARTED
```

### 启动 CLI

键入以下命令

```
$ bin/zkCli.sh
```

键入上述命令后，将连接到 ZooKeeper 服务器，你应该得到以下响应。

```
Connecting to localhost:2181
................
................
................
Welcome to ZooKeeper!
................
................
WATCHER::
WatchedEvent state:SyncConnected type: None path:null
[zk: localhost:2181(CONNECTED) 0]
```

### 停止 ZooKeeper 服务器

连接服务器并执行所有操作后，可以使用以下命令停止 zookeeper 服务器。

```
$ bin/zkServer.sh stop
```

> 本节安装内容参考：[Zookeeper 安装](https://www.w3cschool.cn/zookeeper/zookeeper_installation.html)

## 文件系统

ZooKeeper 会维护一个具有层次关系的数据结构，它非常类似于一个标准的文件系统，如下图所示：

<div align="center">
<img src="http://dunwu.test.upcdn.net/cs/java/javaweb/distributed/rpc/zookeeper/Zookeeper数据结构.gif!zp" />
</div>

ZooKeeper 这种数据结构有如下这些特点：

- 每个子目录项如 NameService 都被称作为 znode，这个 znode 是被它所在的路径唯一标识，如 Server1 这个 znode 的标识为 /NameService/Server1
- znode 可以有子节点目录，并且每个 znode 可以存储数据，注意 EPHEMERAL 类型的目录节点不能有子节点目录
- znode 是有版本的，每个 znode 中存储的数据可以有多个版本，也就是一个访问路径中可以存储多份数据
- znode 可以是临时节点，一旦创建这个 znode 的客户端与服务器失去联系，这个 znode 也将自动删除，ZooKeeper 的客户端和服务器通信采用长连接方式，每个客户端和服务器通过心跳来保持连接，这个连接状态称为 session，如果 znode 是临时节点，这个 session 失效，znode 也就删除了
- znode 的目录名可以自动编号，如 App1 已经存在，再创建的话，将会自动命名为 App2
- znode 可以被监控，包括这个目录节点中存储的数据的修改，子节点目录的变化等，一旦变化可以通知设置监控的客户端，这个是 ZooKeeper 的核心特性，ZooKeeper 的很多功能都是基于这个特性实现的，后面在典型的应用场景中会有实例介绍

znode 类型：

1.  PERSISTENT(持久化目录节点) - 客户端与 zookeeper 断开连接后，该节点依旧存在
2.  PERSISTENT_SEQUENTIAL(持久化顺序编号目录节点) - 客户端与 zookeeper 断开连接后，该节点依旧存在，只是 Zookeeper 给该节点名称进行顺序编号
3.  EPHEMERAL(临时目录节点) - 客户端与 zookeeper 断开连接后，该节点被删除
4.  EPHEMERAL_SEQUENTIAL(临时顺序编号目录节点) - 客户端与 zookeeper 断开连接后，该节点被删除，只是 Zookeeper 给该节点名称进行顺序编号

## 通知机制

客户端注册监听它关心的目录节点，当目录节点发生变化（数据改变、被删除、子目录节点增加删除）时，zookeeper 会通知客户端。

## 应用场景

### 统一命名服务（Name Service）

分布式应用中，通常需要有一套完整的命名规则，既能够产生唯一的名称又便于人识别和记住，通常情况下用树形的名称结构是一个理想的选择，树形的名称结构是一个有层次的目录结构，既对人友好又不会重复。说到这里你可能想到了 JNDI，没错 ZooKeeper 的 Name Service 与 JNDI 能够完成的功能是差不多的，它们都是将有层次的目录结构关联到一定资源上，但是 ZooKeeper 的 Name Service 更加是广泛意义上的关联，也许你并不需要将名称关联到特定资源上，你可能只需要一个不会重复名称，就像数据库中产生一个唯一的数字主键一样。

Name Service 已经是 ZooKeeper 内置的功能，你只要调用 ZooKeeper 的 API 就能实现。如调用 create 接口就可以很容易创建一个目录节点。

### 配置管理（Configuration Management）

配置的管理在分布式应用环境中很常见，例如同一个应用系统需要多台 PC Server 运行，但是它们运行的应用系统的某些配置项是相同的，如果要修改这些相同的配置项，那么就必须同时修改每台运行这个应用系统的 PC Server，这样非常麻烦而且容易出错。

像这样的配置信息完全可以交给 ZooKeeper 来管理，将配置信息保存在 ZooKeeper 的某个目录节点中，然后将所有需要修改的应用机器监控配置信息的状态，一旦配置信息发生变化，每台应用机器就会收到 ZooKeeper 的通知，然后从 ZooKeeper 获取新的配置信息应用到系统中。

<div align="center">
<img src="http://dunwu.test.upcdn.net/cs/java/javaweb/distributed/rpc/zookeeper/Zookeeper配置管理.gif!zp" />
</div>

### 集群管理（Group Membership）

ZooKeeper 能够很容易的实现集群管理的功能，如有多台 Server 组成一个服务集群，那么必须要一个“总管”知道当前集群中每台机器的服务状态，一旦有机器不能提供服务，集群中其它集群必须知道，从而做出调整重新分配服务策略。同样当增加集群的服务能力时，就会增加一台或多台 Server，同样也必须让“总管”知道。

ZooKeeper 不仅能够帮你维护当前的集群中机器的服务状态，而且能够帮你选出一个“总管”，让这个总管来管理集群，这就是 ZooKeeper 的另一个功能 Leader Election。

它们的实现方式都是在 ZooKeeper 上创建一个 EPHEMERAL 类型的目录节点，然后每个 Server 在它们创建目录节点的父目录节点上调用 getChildren(String path, boolean watch) 方法并设置 watch 为 true，由于是 EPHEMERAL 目录节点，当创建它的 Server 死去，这个目录节点也随之被删除，所以 Children 将会变化，这时 getChildren 上的 Watch 将会被调用，所以其它 Server 就知道已经有某台 Server 死去了。新增 Server 也是同样的原理。

ZooKeeper 如何实现 Leader Election，也就是选出一个 Master Server。和前面的一样每台 Server 创建一个 EPHEMERAL 目录节点，不同的是它还是一个 SEQUENTIAL 目录节点，所以它是个 EPHEMERAL_SEQUENTIAL 目录节点。之所以它是 EPHEMERAL_SEQUENTIAL 目录节点，是因为我们可以给每台 Server 编号，我们可以选择当前是最小编号的 Server 为 Master，假如这个最小编号的 Server 死去，由于是 EPHEMERAL 节点，死去的 Server 对应的节点也被删除，所以当前的节点列表中又出现一个最小编号的节点，我们就选择这个节点为当前 Master。这样就实现了动态选择 Master，避免了传统意义上单 Master 容易出现单点故障的问题。

<div align="center">
<img src="http://dunwu.test.upcdn.net/cs/java/javaweb/distributed/rpc/zookeeper/Zookeeper集群管理结构.gif!zp" />
</div>

### 分布式锁

ZooKeeper 实现分布式锁的步骤：

1.  创建一个目录 mylock；
2.  线程 A 想获取锁就在 mylock 目录下创建临时顺序节点；
3.  获取 mylock 目录下所有的子节点，然后获取比自己小的兄弟节点，如果不存在，则说明当前线程顺序号最小，获得锁；
4.  线程 B 获取所有节点，判断自己不是最小节点，设置监听比自己次小的节点；
5.  线程 A 处理完，删除自己的节点，线程 B 监听到变更事件，判断自己是不是最小的节点，如果是则获得锁。

ZooKeeper 版本的分布式锁问题相对比较来说少。

- 锁的占用时间限制：redis 就有占用时间限制，而 ZooKeeper 则没有，最主要的原因是 redis 目前没有办法知道已经获取锁的客户端的状态，是已经挂了呢还是正在执行耗时较长的业务逻辑。而 ZooKeeper 通过临时节点就能清晰知道，如果临时节点存在说明还在执行业务逻辑，如果临时节点不存在说明已经执行完毕释放锁或者是挂了。由此看来 redis 如果能像 ZooKeeper 一样添加一些与客户端绑定的临时键，也是一大好事。
- 是否单点故障：redis 本身有很多中玩法，如客户端一致性 hash，服务器端 sentinel 方案或者 cluster 方案，很难做到一种分布式锁方式能应对所有这些方案。而 ZooKeeper 只有一种玩法，多台机器的节点数据是一致的，没有 redis 的那么多的麻烦因素要考虑。

总体上来说 ZooKeeper 实现分布式锁更加的简单，可靠性更高。但 ZooKeeper 因为需要频繁的创建和删除节点，性能上不如 Redis 方式。

### 队列管理

ZooKeeper 可以处理两种类型的队列：

1.  当一个队列的成员都聚齐时，这个队列才可用，否则一直等待所有成员到达，这种是同步队列。
2.  队列按照 FIFO 方式进行入队和出队操作，例如实现生产者和消费者模型。

同步队列用 ZooKeeper 实现的实现思路如下：

创建一个父目录 /synchronizing，每个成员都监控标志（Set Watch）位目录 /synchronizing/start 是否存在，然后每个成员都加入这个队列，加入队列的方式就是创建 /synchronizing/member_i 的临时目录节点，然后每个成员获取 / synchronizing 目录的所有目录节点，也就是 member_i。判断 i 的值是否已经是成员的个数，如果小于成员个数等待 /synchronizing/start 的出现，如果已经相等就创建 /synchronizing/start。

## 复制

ZooKeeper 作为一个集群提供一致的数据服务，自然，它要在所有机器间做数据复制。

从客户端读写访问的透明度来看，数据复制集群系统分下面两种：

- 写主(WriteMaster) ：对数据的修改提交给指定的节点。读无此限制，可以读取任何一个节点。这种情况下客户端需要对读与写进行区别，俗称读写分离；
- 写任意(Write Any)：对数据的修改可提交给任意的节点，跟读一样。这种情况下，客户端对集群节点的角色与变化透明。

对 ZooKeeper 来说，它采用的方式是写任意。通过增加机器，它的读吞吐能力和响应能力扩展性非常好，而写，随着机器的增多吞吐能力肯定下降（这也是它建立 observer 的原因），而响应能力则取决于具体实现方式，是延迟复制保持最终一致性，还是立即复制快速响应。

## 选举流程

选举状态：

- LOOKING，竞选状态。
- FOLLOWING，随从状态，同步 leader 状态，参与投票。
- OBSERVING，观察状态,同步 leader 状态，不参与投票。
- LEADING，领导者状态。

ZooKeeper 选举流程基于 Paxos 算法。

<div align="center">
<img src="http://dunwu.test.upcdn.net/cs/java/javaweb/distributed/rpc/zookeeper/ZooKeeper选举流程图.jpg!zp" width="640"/>
</div>

1.  选举线程由当前 Server 发起选举的线程担任，其主要功能是对投票结果进行统计，并选出推荐的 Server；
2.  选举线程首先向所有 Server 发起一次询问(包括自己)；
3.  选举线程收到回复后，验证是否是自己发起的询问(验证 zxid 是否一致)，然后获取对方的 id(myid)，并存储到当前询问对象列表中，最后获取对方提议的 leader 相关信息(id,zxid)，并将这些信息存储到当次选举的投票记录表中；
4.  收到所有 Server 回复以后，就计算出 zxid 最大的那个 Server，并将这个 Server 相关信息设置成下一次要投票的 Server；
5.  线程将当前 zxid 最大的 Server 设置为当前 Server 要推荐的 Leader，如果此时获胜的 Server 获得 n/2 + 1 的 Server 票数，设置当前推荐的 leader 为获胜的 Server，将根据获胜的 Server 相关信息设置自己的状态，否则，继续这个过程，直到 leader 被选举出来。 通过流程分析我们可以得出：要使 Leader 获得多数 Server 的支持，则 Server 总数必须是奇数 2n+1，且存活的 Server 的数目不得少于 n+1. 每个 Server 启动后都会重复以上流程。在恢复模式下，如果是刚从崩溃状态恢复的或者刚启动的 server 还会从磁盘快照中恢复数据和会话信息，zk 会记录事务日志并定期进行快照，方便在恢复时进行状态恢复。

述 Leader 选择过程中的状态变化，这是假设全部实例中均没有数据，假设服务器启动顺序分别为：A,B,C。

## 同步流程

选完 Leader 以后，zk 就进入状态同步过程。

1.  Leader 等待 server 连接；
2.  Follower 连接 leader，将最大的 zxid 发送给 leader；
3.  Leader 根据 follower 的 zxid 确定同步点；
4.  完成同步后通知 follower 已经成为 uptodate 状态；
5.  Follower 收到 uptodate 消息后，又可以重新接受 client 的请求进行服务了。

## CLI

ZooKeeper 命令行界面（CLI）用于与 ZooKeeper 集合进行交互以进行开发。它有助于调试和解决不同的选项。

要执行 ZooKeeper CLI 操作，首先打开 ZooKeeper 服务器（“bin/zkServer.sh start”），然后打开 ZooKeeper 客户端（“bin/zkCli.sh”）。一旦客户端启动，你可以执行以下操作：

- 创建 znode
- 获取数据
- 监视 znode 的变化
- 设置数据
- 创建 znode 的子节点
- 列出 znode 的子节点
- 检查状态
- 移除/删除 znode

现在让我们用一个例子逐个了解上面的命令。

### 创建 Znodes

用给定的路径创建一个 znode。flag 参数指定创建的 znode 是临时的，持久的还是顺序的。默认情况下，所有 znode 都是持久的。

当会话过期或客户端断开连接时，临时节点（flag：-e）将被自动删除。

顺序节点保证 znode 路径将是唯一的。

ZooKeeper 集合将向 znode 路径填充 10 位序列号。例如，znode 路径 /myapp 将转换为 /myapp0000000001，下一个序列号将为 /myapp0000000002。如果没有指定 flag，则 znode 被认为是持久的。

语法：

```
create /path /data
```

示例：

```
create /FirstZnode “Myfirstzookeeper-app"
```

输出：

```
[zk: localhost:2181(CONNECTED) 0] create /FirstZnode “Myfirstzookeeper-app"
Created /FirstZnode
```

要创建**顺序节点**，请添加 flag：**-s**，如下所示。

语法：

```
create -s /path /data
```

示例：

```
create -s /FirstZnode second-data
```

输出：

```
[zk: localhost:2181(CONNECTED) 2] create -s /FirstZnode “second-data"
Created /FirstZnode0000000023
```

要创建**临时节点**，请添加 flag：**-e** ，如下所示。

语法：

```
create -e /path /data
```

示例：

```
create -e /SecondZnode “Ephemeral-data"
```

输出：

```
[zk: localhost:2181(CONNECTED) 2] create -e /SecondZnode “Ephemeral-data"
Created /SecondZnode
```

记住当客户端断开连接时，临时节点将被删除。你可以通过退出 ZooKeeper CLI，然后重新打开 CLI 来尝试。

### 获取数据

它返回 znode 的关联数据和指定 znode 的元数据。你将获得信息，例如上次修改数据的时间，修改的位置以及数据的相关信息。此 CLI 还用于分配监视器以显示数据相关的通知。

语法：

```
get /path
```

示例：

```
get /FirstZnode
```

输出：

```
[zk: localhost:2181(CONNECTED) 1] get /FirstZnode
“Myfirstzookeeper-app"
cZxid = 0x7f
ctime = Tue Sep 29 16:15:47 IST 2015
mZxid = 0x7f
mtime = Tue Sep 29 16:15:47 IST 2015
pZxid = 0x7f
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 22
numChildren = 0
```

要访问顺序节点，必须输入 znode 的完整路径。

示例：

```
get /FirstZnode0000000023
```

输出：

```
[zk: localhost:2181(CONNECTED) 1] get /FirstZnode0000000023
“Second-data"
cZxid = 0x80
ctime = Tue Sep 29 16:25:47 IST 2015
mZxid = 0x80
mtime = Tue Sep 29 16:25:47 IST 2015
pZxid = 0x80
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 13
numChildren = 0
```

### Watch（监视）

当指定的 znode 或 znode 的子数据更改时，监视器会显示通知。你只能在 **get** 命令中设置**watch**。

语法：

```
get /path [watch] 1
```

示例：

```
get /FirstZnode 1
```

输出：

```
[zk: localhost:2181(CONNECTED) 1] get /FirstZnode 1
“Myfirstzookeeper-app"
cZxid = 0x7f
ctime = Tue Sep 29 16:15:47 IST 2015
mZxid = 0x7f
mtime = Tue Sep 29 16:15:47 IST 2015
pZxid = 0x7f
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 22
numChildren = 0
```

输出类似于普通的 **get** 命令，但它会等待后台等待 znode 更改。<从这里开始>

### 设置数据

设置指定 znode 的数据。完成此设置操作后，你可以使用 **get** CLI 命令检查数据。

语法：

```
set /path /data
```

示例：

```
set /SecondZnode Data-updated
```

输出：

```
[zk: localhost:2181(CONNECTED) 1] get /SecondZnode “Data-updated"
cZxid = 0x82
ctime = Tue Sep 29 16:29:50 IST 2015
mZxid = 0x83
mtime = Tue Sep 29 16:29:50 IST 2015
pZxid = 0x82
cversion = 0
dataVersion = 1
aclVersion = 0
ephemeralOwner = 0x15018b47db00000
dataLength = 14
numChildren = 0
```

如果你在 **get** 命令中分配了**watch**选项（如上一个命令），则输出将类似如下所示。

输出：

```
[zk: localhost:2181(CONNECTED) 1] get /FirstZnode “Mysecondzookeeper-app"

WATCHER: :

WatchedEvent state:SyncConnected type:NodeDataChanged path:/FirstZnode
cZxid = 0x7f
ctime = Tue Sep 29 16:15:47 IST 2015
mZxid = 0x84
mtime = Tue Sep 29 17:14:47 IST 2015
pZxid = 0x7f
cversion = 0
dataVersion = 1
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 23
numChildren = 0
```

### 创建子项/子节点

创建子节点类似于创建新的 znode。唯一的区别是，子 znode 的路径也将具有父路径。

语法：

```
create /parent/path/subnode/path /data
```

示例：

```
create /FirstZnode/Child1 firstchildren
```

输出：

```
[zk: localhost:2181(CONNECTED) 16] create /FirstZnode/Child1 “firstchildren"
created /FirstZnode/Child1
[zk: localhost:2181(CONNECTED) 17] create /FirstZnode/Child2 “secondchildren"
created /FirstZnode/Child2
```

### 列出子项

此命令用于列出和显示 znode 的子项。

语法：

```
ls /path
```

示例：

```
ls /MyFirstZnode
```

输出：

```
[zk: localhost:2181(CONNECTED) 2] ls /MyFirstZnode
[mysecondsubnode, myfirstsubnode]
```

### 检查状态

状态描述指定的 znode 的元数据。它包含时间戳，版本号，ACL，数据长度和子 znode 等细项。

语法：

```
stat /path
```

示例：

```
stat /FirstZnode
```

输出：

```
[zk: localhost:2181(CONNECTED) 1] stat /FirstZnode
cZxid = 0x7f
ctime = Tue Sep 29 16:15:47 IST 2015
mZxid = 0x7f
mtime = Tue Sep 29 17:14:24 IST 2015
pZxid = 0x7f
cversion = 0
dataVersion = 1
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 23
numChildren = 0
```

### 移除 Znode

移除指定的 znode 并递归其所有子节点。只有在这样的 znode 可用的情况下才会发生。

语法：

```
rmr /path
```

示例：

```
rmr /FirstZnode
```

输出：

```
[zk: localhost:2181(CONNECTED) 10] rmr /FirstZnode
[zk: localhost:2181(CONNECTED) 11] get /FirstZnode
Node does not exist: /FirstZnode
```

删除（delete/path）命令类似于 remove 命令，除了它只适用于没有子节点的 znode。

## API

ZooKeeper 有一个绑定 Java 和 C 的官方 API。Zookeeper 社区为大多数语言（.NET，python 等）提供非官方 API。

使用 ZooKeeper API，应用程序可以连接，交互，操作数据，协调，最后断开与 ZooKeeper 集合的连接。

ZooKeeper API 具有丰富的功能，以简单和安全的方式获得 ZooKeeper 集合的所有功能。ZooKeeper API 提供同步和异步方法。

ZooKeeper 集合和 ZooKeeper API 在各个方面都完全相辅相成，对开发人员有很大的帮助。让我们在本章讨论 Java 绑定。

### ZooKeeper API 的基础知识

与 ZooKeeper 集合进行交互的应用程序称为 **ZooKeeper 客户端**。

Znode 是 ZooKeeper 集合的核心组件，ZooKeeper API 提供了一小组方法使用 ZooKeeper 集合来操纵 znode 的所有细节。

客户端应该遵循以步骤，与 ZooKeeper 集合进行清晰和干净的交互。

- 连接到 ZooKeeper 集合。ZooKeeper 集合为客户端分配会话 ID。
- 定期向服务器发送心跳。否则，ZooKeeper 集合将过期会话 ID，客户端需要重新连接。
- 只要会话 ID 处于活动状态，就可以获取/设置 znode。
- 所有任务完成后，断开与 ZooKeeper 集合的连接。如果客户端长时间不活动，则 ZooKeeper 集合将自动断开客户端。

### Java 绑定

让我们来了解本章中最重要的一组 ZooKeeper API。ZooKeeper API 的核心部分是**ZooKeeper 类**。它提供了在其构造函数中连接 ZooKeeper 集合的选项，并具有以下方法：

- **connect** - 连接到 ZooKeeper 集合
- **create**- 创建 znode
- **exists**- 检查 znode 是否存在及其信息
- **getData** - 从特定的 znode 获取数据
- **setData** - 在特定的 znode 中设置数据
- **getChildren** - 获取特定 znode 中的所有子节点
- **delete** - 删除特定的 znode 及其所有子项
- **close** - 关闭连接

### 连接到 ZooKeeper 集合

ZooKeeper 类通过其构造函数提供 connect 功能。构造函数的签名如下 :

```
ZooKeeper(String connectionString, int sessionTimeout, Watcher watcher)
```

- **connectionString** - ZooKeeper 集合主机。
- **sessionTimeout** - 会话超时（以毫秒为单位）。
- **watcher** - 实现“监视器”界面的对象。ZooKeeper 集合通过监视器对象返回连接状态。

让我们创建一个新的帮助类 **ZooKeeperConnection** ，并添加一个方法 **connect** 。 **connect** 方法创建一个 ZooKeeper 对象，连接到 ZooKeeper 集合，然后返回对象。

这里 **CountDownLatch** 用于停止（等待）主进程，直到客户端与 ZooKeeper 集合连接。

ZooKeeper 集合通过监视器回调来回复连接状态。一旦客户端与 ZooKeeper 集合连接，监视器回调就会被调用，并且监视器回调函数调用**CountDownLatch**的**countDown**方法来释放锁，在主进程中**await**。

以下是与 ZooKeeper 集合连接的完整代码。

示例：

```java
// import java classes
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

// import zookeeper classes
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.data.Stat;

public class ZooKeeperConnection {

   // declare zookeeper instance to access ZooKeeper ensemble
   private ZooKeeper zoo;
   final CountDownLatch connectedSignal = new CountDownLatch(1);

   // Method to connect zookeeper ensemble.
   public ZooKeeper connect(String host) throws IOException,InterruptedException {

      zoo = new ZooKeeper(host,5000,new Watcher() {

         public void process(WatchedEvent we) {

            if (we.getState() == KeeperState.SyncConnected) {
               connectedSignal.countDown();
            }
         }
      });

      connectedSignal.await();
      return zoo;
   }

   // Method to disconnect from zookeeper server
   public void close() throws InterruptedException {
      zoo.close();
   }
}
```

保存上面的代码，它将在下一节中用于连接 ZooKeeper 集合。

### 创建 Znode

ZooKeeper 类提供了在 ZooKeeper 集合中创建一个新的 znode 的**create**方法。 **create** 方法的签名如下：

```
create(String path, byte[] data, List<ACL> acl, CreateMode createMode)
```

- **path** - Znode 路径。例如，/myapp1，/myapp2，/myapp1/mydata1，myapp2/mydata1/myanothersubdata
- **data** - 要存储在指定 znode 路径中的数据
- **acl** - 要创建的节点的访问控制列表。ZooKeeper API 提供了一个静态接口 **ZooDefs.Ids** 来获取一些基本的 acl 列表。例如，ZooDefs.Ids.OPEN_ACL_UNSAFE 返回打开 znode 的 acl 列表。
- **createMode** - 节点的类型，即临时，顺序或两者。这是一个**枚举**。

让我们创建一个新的 Java 应用程序来检查 ZooKeeper API 的 **create** 功能。创建文件 **ZKCreate.java** 。在 main 方法中，创建一个类型为 **ZooKeeperConnection** 的对象，并调用 **connect** 方法连接到 ZooKeeper 集合。

connect 方法将返回 ZooKeeper 对象 **zk** 。现在，请使用自定义**path**和**data**调用 **zk** 对象的 **create** 方法。

创建 znode 的完整程序代码如下：

示例：

```java
import java.io.IOException;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;

public class ZKCreate {
   // create static instance for zookeeper class.
   private static ZooKeeper zk;

   // create static instance for ZooKeeperConnection class.
   private static ZooKeeperConnection conn;

   // Method to create znode in zookeeper ensemble
   public static void create(String path, byte[] data) throws
      KeeperException,InterruptedException {
      zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE,
      CreateMode.PERSISTENT);
   }

   public static void main(String[] args) {

      // znode path
      String path = "/MyFirstZnode"; // Assign path to znode

      // data in byte array
      byte[] data = "My first zookeeper app".getBytes(); // Declare data

      try {
         conn = new ZooKeeperConnection();
         zk = conn.connect("localhost");
         create(path, data); // Create the data to the specified path
         conn.close();
      } catch (Exception e) {
         System.out.println(e.getMessage()); //Catch error message
      }
   }
}
```

一旦编译和执行应用程序，将在 ZooKeeper 集合中创建具有指定数据的 znode。你可以使用 ZooKeeper CLI **zkCli.sh** 进行检查。

```
cd /path/to/zookeeper
bin/zkCli.sh
>>> get /MyFirstZnode
```

### Exists - 检查 Znode 的存在

ZooKeeper 类提供了 **exists** 方法来检查 znode 的存在。如果指定的 znode 存在，则返回一个 znode 的元数据。**exists**方法的签名如下：

```
exists(String path, boolean watcher)
```

- **path**- Znode 路径
- **watcher** - 布尔值，用于指定是否监视指定的 znode

让我们创建一个新的 Java 应用程序来检查 ZooKeeper API 的“exists”功能。创建文件“ZKExists.java”。在 main 方法中，使用“ZooKeeperConnection”对象创建 ZooKeeper 对象“zk”。然后，使用自定义“path”调用“zk”对象的“exists”方法。完整的列表如下：

示例：

```java
import java.io.IOException;

import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;

public class ZKExists {
   private static ZooKeeper zk;
   private static ZooKeeperConnection conn;

   // Method to check existence of znode and its status, if znode is available.
   public static Stat znode_exists(String path) throws
      KeeperException,InterruptedException {
      return zk.exists(path, true);
   }

   public static void main(String[] args) throws InterruptedException,KeeperException {
      String path = "/MyFirstZnode"; // Assign znode to the specified path

      try {
         conn = new ZooKeeperConnection();
         zk = conn.connect("localhost");
         Stat stat = znode_exists(path); // Stat checks the path of the znode

         if(stat != null) {
            System.out.println("Node exists and the node version is " +
            stat.getVersion());
         } else {
            System.out.println("Node does not exists");
         }

      } catch(Exception e) {
         System.out.println(e.getMessage()); // Catches error messages
      }
   }
}
```

一旦编译和执行应用程序，你将获得以下输出。

```
Node exists and the node version is 1.
```

### getData 方法

ZooKeeper 类提供 **getData** 方法来获取附加在指定 znode 中的数据及其状态。 **getData** 方法的签名如下：

```
getData(String path, Watcher watcher, Stat stat)
```

- **path** - Znode 路径。
- **watcher** - 监视器类型的回调函数。当指定的 znode 的数据改变时，ZooKeeper 集合将通过监视器回调进行通知。这是一次性通知。
- **stat** - 返回 znode 的元数据。

让我们创建一个新的 Java 应用程序来了解 ZooKeeper API 的 **getData** 功能。创建文件 **ZKGetData.java** 。在 main 方法中，使用 **ZooKeeperConnection** 对象创建一个 ZooKeeper 对象 **zk** 。然后，使用自定义路径调用 zk 对象的 **getData** 方法。

下面是从指定节点获取数据的完整程序代码：

示例：

```java
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;

public class ZKGetData {

   private static ZooKeeper zk;
   private static ZooKeeperConnection conn;
   public static Stat znode_exists(String path) throws
      KeeperException,InterruptedException {
      return zk.exists(path,true);
   }

   public static void main(String[] args) throws InterruptedException, KeeperException {
      String path = "/MyFirstZnode";
      final CountDownLatch connectedSignal = new CountDownLatch(1);

      try {
         conn = new ZooKeeperConnection();
         zk = conn.connect("localhost");
         Stat stat = znode_exists(path);

         if(stat != null) {
            byte[] b = zk.getData(path, new Watcher() {

               public void process(WatchedEvent we) {

                  if (we.getType() == Event.EventType.None) {
                     switch(we.getState()) {
                        case Expired:
                        connectedSignal.countDown();
                        break;
                     }

                  } else {
                     String path = "/MyFirstZnode";

                     try {
                        byte[] bn = zk.getData(path,
                        false, null);
                        String data = new String(bn,
                        "UTF-8");
                        System.out.println(data);
                        connectedSignal.countDown();

                     } catch(Exception ex) {
                        System.out.println(ex.getMessage());
                     }
                  }
               }
            }, null);

            String data = new String(b, "UTF-8");
            System.out.println(data);
            connectedSignal.await();

         } else {
            System.out.println("Node does not exists");
         }
      } catch(Exception e) {
        System.out.println(e.getMessage());
      }
   }
}
```

一旦编译和执行应用程序，你将获得以下输出

```
My first zookeeper app
```

应用程序将等待 ZooKeeper 集合的进一步通知。使用 ZooKeeper CLI **zkCli.sh** 更改指定 znode 的数据。

```
cd /path/to/zookeeper
bin/zkCli.sh
>>> set /MyFirstZnode Hello
```

现在，应用程序将打印以下输出并退出。

```
Hello
```

### setData 方法

ZooKeeper 类提供 **setData** 方法来修改指定 znode 中附加的数据。 **setData** 方法的签名如下：

```
setData(String path, byte[] data, int version)
```

- **path**- Znode 路径
- **data** - 要存储在指定 znode 路径中的数据。
- **version**- znode 的当前版本。每当数据更改时，ZooKeeper 会更新 znode 的版本号。

现在让我们创建一个新的 Java 应用程序来了解 ZooKeeper API 的 **setData** 功能。创建文件 **ZKSetData.java** 。在 main 方法中，使用 **ZooKeeperConnection** 对象创建一个 ZooKeeper 对象 **zk** 。然后，使用指定的路径，新数据和节点版本调用 **zk** 对象的 **setData** 方法。

以下是修改附加在指定 znode 中的数据的完整程序代码。

示例：

```java
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import java.io.IOException;

public class ZKSetData {
   private static ZooKeeper zk;
   private static ZooKeeperConnection conn;

   // Method to update the data in a znode. Similar to getData but without watcher.
   public static void update(String path, byte[] data) throws
      KeeperException,InterruptedException {
      zk.setData(path, data, zk.exists(path,true).getVersion());
   }

   public static void main(String[] args) throws InterruptedException,KeeperException {
      String path= "/MyFirstZnode";
      byte[] data = "Success".getBytes(); //Assign data which is to be updated.

      try {
         conn = new ZooKeeperConnection();
         zk = conn.connect("localhost");
         update(path, data); // Update znode data to the specified path
      } catch(Exception e) {
         System.out.println(e.getMessage());
      }
   }
}
```

编译并执行应用程序后，指定的 znode 的数据将被改变，并且可以使用 ZooKeeper CLI **zkCli.sh** 进行检查。

```
cd /path/to/zookeeper
bin/zkCli.sh
>>> get /MyFirstZnode
```

### getChildren 方法

ZooKeeper 类提供 **getChildren** 方法来获取特定 znode 的所有子节点。 **getChildren** 方法的签名如下：

```
getChildren(String path, Watcher watcher)
```

- **path** - Znode 路径。
- **watcher** - 监视器类型的回调函数。当指定的 znode 被删除或 znode 下的子节点被创建/删除时，ZooKeeper 集合将进行通知。这是一次性通知。

示例：

```java
import java.io.IOException;
import java.util.*;

import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;

public class ZKGetChildren {
   private static ZooKeeper zk;
   private static ZooKeeperConnection conn;

   // Method to check existence of znode and its status, if znode is available.
   public static Stat znode_exists(String path) throws
      KeeperException,InterruptedException {
      return zk.exists(path,true);
   }

   public static void main(String[] args) throws InterruptedException,KeeperException {
      String path = "/MyFirstZnode"; // Assign path to the znode

      try {
         conn = new ZooKeeperConnection();
         zk = conn.connect("localhost");
         Stat stat = znode_exists(path); // Stat checks the path

         if(stat!= null) {

            // getChildren method - get all the children of znode.It has two args, path and watch
            List <String> children = zk.getChildren(path, false);
            for(int i = 0; i < children.size(); i++)
            System.out.println(children.get(i)); //Print children's
         } else {
            System.out.println("Node does not exists");
         }

      } catch(Exception e) {
         System.out.println(e.getMessage());
      }

   }
}
```

在运行程序之前，让我们使用 ZooKeeper CLI **zkCli.sh** 为 **/MyFirstZnode** 创建两个子节点。

```
cd /path/to/zookeeper
bin/zkCli.sh
>>> create /MyFirstZnode/myfirstsubnode Hi
>>> create /MyFirstZnode/mysecondsubmode Hi
```

现在，编译和运行程序将输出上面创建的 znode。

```
myfirstsubnode
mysecondsubnode
```

### 删除 Znode

ZooKeeper 类提供了 **delete** 方法来删除指定的 znode。 **delete** 方法的签名如下：

```
delete(String path, int version)
```

- **path** - Znode 路径。
- **version** - znode 的当前版本。

让我们创建一个新的 Java 应用程序来了解 ZooKeeper API 的 **delete** 功能。创建文件 **ZKDelete.java** 。在 main 方法中，使用 **ZooKeeperConnection** 对象创建一个 ZooKeeper 对象 **zk** 。然后，使用指定的路径和版本号调用 **zk** 对象的 **delete** 方法。

删除 znode 的完整程序代码如下：

示例：

```java
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException;

public class ZKDelete {
   private static ZooKeeper zk;
   private static ZooKeeperConnection conn;

   // Method to check existence of znode and its status, if znode is available.
   public static void delete(String path) throws KeeperException,InterruptedException {
      zk.delete(path,zk.exists(path,true).getVersion());
   }

   public static void main(String[] args) throws InterruptedException,KeeperException {
      String path = "/MyFirstZnode"; //Assign path to the znode

      try {
         conn = new ZooKeeperConnection();
         zk = conn.connect("localhost");
         delete(path); //delete the node with the specified path
      } catch(Exception e) {
         System.out.println(e.getMessage()); // catches error messages
      }
   }
}
```

## 资源

### 官方资源

| [官网](http://zookeeper.apache.org/) | [官网文档](https://cwiki.apache.org/confluence/display/ZOOKEEPER) | [Github](https://github.com/apache/zookeeper) |

### 文章

[分布式服务框架 ZooKeeper -- 管理分布式环境中的数据](https://www.ibm.com/developerworks/cn/opensource/os-cn-zookeeper/index.html)
[ZooKeeper 的功能以及工作原理](https://www.cnblogs.com/felixzh/p/5869212.html)
