# ZooKeeper 应用指南

> **ZooKeeper 是一个分布式应用协调服务** ，由 Apache 进行维护。
>
> Zookeeper 可以用于发布/订阅、负载均衡、命令服务、分布式协调/通知、集群管理、Master 选举、分布式锁和分布式队列等功能 。

<!-- TOC depthFrom:2 depthTo:3 -->

- [1. 简介](#1-简介)
  - [1.1. ZooKeeper 是什么](#11-zookeeper-是什么)
  - [1.2. ZooKeeper 提供了什么](#12-zookeeper-提供了什么)
  - [1.3. Zookeeper 的特性](#13-zookeeper-的特性)
- [2. 设计目标](#2-设计目标)
  - [2.1. 简单的数据模型](#21-简单的数据模型)
  - [2.2. 分布式](#22-分布式)
  - [2.3. 顺序访问](#23-顺序访问)
  - [2.4. 高性能高可用](#24-高性能高可用)
- [3. 核心概念](#3-核心概念)
  - [3.1. 集群角色](#31-集群角色)
  - [3.2. 会话](#32-会话)
  - [3.3. 数据节点](#33-数据节点)
  - [3.4. 节点信息](#34-节点信息)
  - [3.5. Watcher](#35-watcher)
  - [3.6. ACL](#36-acl)
- [4. 文件系统](#4-文件系统)
- [5. ZAB 协议](#5-zab-协议)
- [6. 高可用](#6-高可用)
  - [6.1. 复制](#61-复制)
  - [6.2. 选举流程](#62-选举流程)
  - [6.3. 同步流程](#63-同步流程)
- [7. 应用场景](#7-应用场景)
  - [7.1. 数据的发布/订阅](#71-数据的发布订阅)
  - [7.2. 命名服务](#72-命名服务)
  - [7.3. 配置管理](#73-配置管理)
  - [7.4. 集群管理](#74-集群管理)
  - [7.5. 分布式锁](#75-分布式锁)
  - [7.6. Master 选举](#76-master-选举)
  - [7.7. 队列管理](#77-队列管理)
- [8. Client API](#8-client-api)
  - [8.1. ZooKeeper API 的基础知识](#81-zookeeper-api-的基础知识)
  - [8.2. Java 绑定](#82-java-绑定)
  - [8.3. 连接到 ZooKeeper 集合](#83-连接到-zookeeper-集合)
  - [8.4. 创建 Znode](#84-创建-znode)
  - [8.5. Exists - 检查 Znode 的存在](#85-exists---检查-znode-的存在)
  - [8.6. getData 方法](#86-getdata-方法)
  - [8.7. setData 方法](#87-setdata-方法)
  - [8.8. getChildren 方法](#88-getchildren-方法)
  - [8.9. 删除 Znode](#89-删除-znode)
- [9. 运维](#9-运维)
- [10. 参考资料](#10-参考资料)

<!-- /TOC -->

## 1. 简介

### 1.1. ZooKeeper 是什么

**ZooKeeper 是一个分布式应用协调服务** ，由 Apache 进行维护。

ZooKeeper 主要用来解决分布式集群中应用系统的一致性问题，它能提供基于类似于文件系统的目录节点树方式的数据存储。但是 ZooKeeper 并不是用来专门存储数据的，它的作用主要是用来**维护和监控存储数据的状态变化。通过监控这些数据状态的变化，从而可以达到基于数据的集群管理**。

### 1.2. ZooKeeper 提供了什么

- **文件系统**
- **通知机制**

### 1.3. Zookeeper 的特性

Zookeeper 具有以下特性：

- **顺序一致性**：从一个客户端发起的事务请求，最终都会严格按照其发起顺序被应用到 Zookeeper 中；
- **原子性**：所有事务请求的处理结果在整个集群中所有机器上都是一致的；不存在部分机器应用了该事务，而另一部分没有应用的情况；
- **单一视图**：所有客户端看到的服务端数据模型都是一致的；
- **可靠性**：一旦服务端成功应用了一个事务，则其引起的改变会一直保留，直到被另外一个事务所更改；
- **实时性**：一旦一个事务被成功应用后，Zookeeper 可以保证客户端立即可以读取到这个事务变更后的最新状态的数据。

## 2. 设计目标

Zookeeper 致力于为那些高吞吐的大型分布式系统提供一个高性能、高可用、且具有严格顺序访问控制能力的分布式协调服务。

### 2.1. 简单的数据模型

Zookeeper 通过树形结构来存储数据，它由一系列被称为 ZNode 的数据节点组成，类似于常见的文件系统。不过和常见的文件系统不同，Zookeeper 将数据全量存储在内存中，以此来实现高吞吐，减少访问延迟。

### 2.2. 分布式

Zookeeper 支持集群模式。集群中每个 Zookeeper 节点都会单独在内存中维护自身的状态，并且每台机器之间都保持着通讯，只要集群中有半数机器能够正常工作，那么整个集群就可以正常提供服务。

<div align="center">
<img src="http://dunwu.test.upcdn.net/cs/java/javaweb/distributed/rpc/zookeeper/zookeeper-service.png!zp" />
</div>

- ZooKeeper 服务器节点间的同步机制是基于原子广播。实现这个机制的协议叫做 **Zab 协议**。
- 客户端注册监听它关心的目录节点，当目录节点发生变化（数据改变、被删除、子目录节点增加删除）时，ZooKeeper 服务会通知客户端。

### 2.3. 顺序访问

对于来自客户端的每个更新请求，ZooKeeper 具备严格的顺序访问控制能力。

**为了保证事务的顺序一致性，ZooKeeper 采用了递增的事务 id 号（zxid）来标识事务**。

所有的提议（proposal）都在被提出的时候加上了 zxid。实现中 zxid 是一个 64 位的数字，它高 32 位是 epoch 用来标识 leader 关系是否改变，每次一个 leader 被选出来，它都会有一个新的 epoch，标识当前属于那个 leader 的统治时期。低 32 位用于递增计数。

### 2.4. 高性能高可用

ZooKeeper 将数据存全量储在内存中以保持高性能，并通过服务集群来实现高可用，由于 Zookeeper 的所有更新和删除都是基于事务的，所以其在读多写少的应用场景中有着很高的性能表现。

## 3. 核心概念

### 3.1. 集群角色

Zookeeper 集群中的机器分为以下三种角色：

- **`Leader`** ：为客户端提供读写服务，并维护集群状态，它是由集群选举所产生的；
- **`Follower`** ：为客户端提供读写服务，并定期向 Leader 汇报自己的节点状态。同时也参与写操作“过半写成功”的策略和 Leader 的选举；
- **`Observer`** ：为客户端提供读写服务，并定期向 Leader 汇报自己的节点状态，但不参与写操作“过半写成功”的策略和 Leader 的选举，因此 Observer 可以在不影响写性能的情况下提升集群的读性能。

### 3.2. 会话

Zookeeper 客户端通过 TCP 长连接连接到服务集群，会话 (Session) 从第一次连接开始就已经建立，之后通过心跳检测机制来保持有效的会话状态。通过这个连接，客户端可以发送请求并接收响应，同时也可以接收到 Watch 事件的通知。

关于会话中另外一个核心的概念是 sessionTimeOut(会话超时时间)，当由于网络故障或者客户端主动断开等原因，导致连接断开，此时只要在会话超时时间之内重新建立连接，则之前创建的会话依然有效。

### 3.3. 数据节点

Zookeeper 数据模型是由一系列基本数据单元 `Znode`(数据节点) 组成的节点树，其中根节点为 `/`。每个节点上都会保存自己的数据和节点信息。Zookeeper 中节点可以分为两大类：

- **持久节点** ：节点一旦创建，除非被主动删除，否则一直存在；
- **临时节点** ：一旦创建该节点的客户端会话失效，则所有该客户端创建的临时节点都会被删除。

临时节点和持久节点都可以添加一个特殊的属性：`SEQUENTIAL`，代表该节点是否具有递增属性。如果指定该属性，那么在这个节点创建时，Zookeeper 会自动在其节点名称后面追加一个由父节点维护的递增数字。

### 3.4. 节点信息

每个 ZNode 节点在存储数据的同时，都会维护一个叫做 `Stat` 的数据结构，里面存储了关于该节点的全部状态信息。如下：

| **状态属性**   | **说明**                                                                                   |
| -------------- | ------------------------------------------------------------------------------------------ |
| czxid          | 数据节点创建时的事务 ID                                                                    |
| ctime          | 数据节点创建时的时间                                                                       |
| mzxid          | 数据节点最后一次更新时的事务 ID                                                            |
| mtime          | 数据节点最后一次更新时的时间                                                               |
| pzxid          | 数据节点的子节点最后一次被修改时的事务 ID                                                  |
| cversion       | 子节点的更改次数                                                                           |
| version        | 节点数据的更改次数                                                                         |
| aversion       | 节点的 ACL 的更改次数                                                                      |
| ephemeralOwner | 如果节点是临时节点，则表示创建该节点的会话的 SessionID；如果节点是持久节点，则该属性值为 0 |
| dataLength     | 数据内容的长度                                                                             |
| numChildren    | 数据节点当前的子节点个数                                                                   |

### 3.5. Watcher

Zookeeper 中一个常用的功能是 Watcher(事件监听器)，它允许用户在指定节点上针对感兴趣的事件注册监听，当事件发生时，监听器会被触发，并将事件信息推送到客户端。该机制是 Zookeeper 实现分布式协调服务的重要特性。

### 3.6. ACL

Zookeeper 采用 ACL(Access Control Lists) 策略来进行权限控制，类似于 UNIX 文件系统的权限控制。它定义了如下五种权限：

- **CREATE**：允许创建子节点；
- **READ**：允许从节点获取数据并列出其子节点；
- **WRITE**：允许为节点设置数据；
- **DELETE**：允许删除子节点；
- **ADMIN**：允许为节点设置权限。

## 4. 文件系统

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

1. **`PERSISTENT(持久化目录节点)`** - 客户端与 zookeeper 断开连接后，该节点依旧存在
2. **`PERSISTENT_SEQUENTIAL(持久化顺序编号目录节点)`** - 客户端与 zookeeper 断开连接后，该节点依旧存在，只是 Zookeeper 给该节点名称进行顺序编号
3. **`EPHEMERAL(临时目录节点)`** - 客户端与 zookeeper 断开连接后，该节点被删除
4. **`EPHEMERAL_SEQUENTIAL(临时顺序编号目录节点)`** - 客户端与 zookeeper 断开连接后，该节点被删除，只是 Zookeeper 给该节点名称进行顺序编号

## 5. ZAB 协议

ZAB 协议包括两种基本的模式，分别是 **`崩溃恢复（选主）`**和 **`消息广播（同步）`**：

#### 1. 崩溃恢复

当整个服务框架在启动过程中，或者当 Leader 服务器出现异常时，ZAB 协议就会进入恢复模式，通过过半选举机制产生新的 Leader，之后其他机器将从新的 Leader 上同步状态，当有过半机器完成状态同步后，就退出恢复模式，进入消息广播模式。

#### 2. 消息广播

ZAB 协议的消息广播过程使用的是原子广播协议。在整个消息的广播过程中，Leader 服务器会每个事物请求生成对应的 Proposal，并为其分配一个全局唯一的递增的事务 ID(ZXID)，之后再对其进行广播。具体过程如下：

Leader 服务会为每一个 Follower 服务器分配一个单独的队列，然后将事务 Proposal 依次放入队列中，并根据 FIFO(先进先出) 的策略进行消息发送。Follower 服务在接收到 Proposal 后，会将其以事务日志的形式写入本地磁盘中，并在写入成功后反馈给 Leader 一个 Ack 响应。当 Leader 接收到超过半数 Follower 的 Ack 响应后，就会广播一个 Commit 消息给所有的 Follower 以通知其进行事务提交，之后 Leader 自身也会完成对事务的提交。而每一个 Follower 则在接收到 Commit 消息后，完成事务的提交。

## 6. 高可用

### 6.1. 复制

ZooKeeper 作为一个集群提供一致的数据服务，自然，它要在所有机器间做数据复制。

从客户端读写访问的透明度来看，数据复制集群系统分下面两种：

- 写主(WriteMaster) ：对数据的修改提交给指定的节点。读无此限制，可以读取任何一个节点。这种情况下客户端需要对读与写进行区别，俗称读写分离；
- 写任意(Write Any)：对数据的修改可提交给任意的节点，跟读一样。这种情况下，客户端对集群节点的角色与变化透明。

对 ZooKeeper 来说，它采用的方式是写任意。通过增加机器，它的读吞吐能力和响应能力扩展性非常好，而写，随着机器的增多吞吐能力肯定下降（这也是它建立 observer 的原因），而响应能力则取决于具体实现方式，是延迟复制保持最终一致性，还是立即复制快速响应。

### 6.2. 选举流程

选举状态：

- LOOKING，竞选状态。
- FOLLOWING，随从状态，同步 leader 状态，参与投票。
- OBSERVING，观察状态,同步 leader 状态，不参与投票。
- LEADING，领导者状态。

ZooKeeper 选举流程基于 Paxos 算法。

<div align="center">
<img src="http://dunwu.test.upcdn.net/cs/java/javaweb/distributed/rpc/zookeeper/ZooKeeper选举流程图.jpg!zp" width="640"/>
</div>

1. 选举线程由当前 Server 发起选举的线程担任，其主要功能是对投票结果进行统计，并选出推荐的 Server；
2. 选举线程首先向所有 Server 发起一次询问(包括自己)；
3. 选举线程收到回复后，验证是否是自己发起的询问(验证 zxid 是否一致)，然后获取对方的 id(myid)，并存储到当前询问对象列表中，最后获取对方提议的 leader 相关信息(id,zxid)，并将这些信息存储到当次选举的投票记录表中；
4. 收到所有 Server 回复以后，就计算出 zxid 最大的那个 Server，并将这个 Server 相关信息设置成下一次要投票的 Server；
5. 线程将当前 zxid 最大的 Server 设置为当前 Server 要推荐的 Leader，如果此时获胜的 Server 获得 n/2 + 1 的 Server 票数，设置当前推荐的 leader 为获胜的 Server，将根据获胜的 Server 相关信息设置自己的状态，否则，继续这个过程，直到 leader 被选举出来。 通过流程分析我们可以得出：要使 Leader 获得多数 Server 的支持，则 Server 总数必须是奇数 2n+1，且存活的 Server 的数目不得少于 n+1. 每个 Server 启动后都会重复以上流程。在恢复模式下，如果是刚从崩溃状态恢复的或者刚启动的 server 还会从磁盘快照中恢复数据和会话信息，zk 会记录事务日志并定期进行快照，方便在恢复时进行状态恢复。

述 Leader 选择过程中的状态变化，这是假设全部实例中均没有数据，假设服务器启动顺序分别为：A,B,C。

### 6.3. 同步流程

选完 Leader 以后，zk 就进入状态同步过程。

1. Leader 等待 server 连接；
2. Follower 连接 leader，将最大的 zxid 发送给 leader；
3. Leader 根据 follower 的 zxid 确定同步点；
4. 完成同步后通知 follower 已经成为 uptodate 状态；
5. Follower 收到 uptodate 消息后，又可以重新接受 client 的请求进行服务了。

## 7. 应用场景

> **Zookeeper 可以用于发布/订阅、负载均衡、命令服务、分布式协调/通知、集群管理、Master 选举、分布式锁和分布式队列等功能** 。

### 7.1. 数据的发布/订阅

数据的发布/订阅系统，通常也用作配置中心。在分布式系统中，你可能有成千上万个服务节点，如果想要对所有服务的某项配置进行更改，由于数据节点过多，你不可逐台进行修改，而应该在设计时采用统一的配置中心。之后发布者只需要将新的配置发送到配置中心，所有服务节点即可自动下载并进行更新，从而实现配置的集中管理和动态更新。

Zookeeper 通过 Watcher 机制可以实现数据的发布和订阅。分布式系统的所有的服务节点可以对某个 ZNode 注册监听，之后只需要将新的配置写入该 ZNode，所有服务节点都会收到该事件。

### 7.2. 命名服务

在分布式系统中，通常需要一个全局唯一的名字，如生成全局唯一的订单号等，Zookeeper 可以通过顺序节点的特性来生成全局唯一 ID，从而可以对分布式系统提供命名服务。

### 7.3. 配置管理

配置的管理在分布式应用环境中很常见，例如同一个应用系统需要多台 PC Server 运行，但是它们运行的应用系统的某些配置项是相同的，如果要修改这些相同的配置项，那么就必须同时修改每台运行这个应用系统的 PC Server，这样非常麻烦而且容易出错。

像这样的配置信息完全可以交给 ZooKeeper 来管理，将配置信息保存在 ZooKeeper 的某个目录节点中，然后将所有需要修改的应用机器监控配置信息的状态，一旦配置信息发生变化，每台应用机器就会收到 ZooKeeper 的通知，然后从 ZooKeeper 获取新的配置信息应用到系统中。

<div align="center">
<img src="http://dunwu.test.upcdn.net/cs/java/javaweb/distributed/rpc/zookeeper/Zookeeper配置管理.gif!zp" />
</div>

### 7.4. 集群管理

Zookeeper 还能解决大多数分布式系统中的问题：

- 如可以通过创建临时节点来建立心跳检测机制。如果分布式系统的某个服务节点宕机了，则其持有的会话会超时，此时该临时节点会被删除，相应的监听事件就会被触发。
- 分布式系统的每个服务节点还可以将自己的节点状态写入临时节点，从而完成状态报告或节点工作进度汇报。
- 通过数据的订阅和发布功能，Zookeeper 还能对分布式系统进行模块的解耦和任务的调度。
- 通过监听机制，还能对分布式系统的服务节点进行动态上下线，从而实现服务的动态扩容。

<div align="center">
<img src="http://dunwu.test.upcdn.net/cs/java/javaweb/distributed/rpc/zookeeper/Zookeeper集群管理结构.gif!zp" />
</div>

### 7.5. 分布式锁

可以通过 Zookeeper 的临时节点和 Watcher 机制来实现分布式锁，这里以排它锁为例进行说明：

分布式系统的所有服务节点可以竞争性地去创建同一个临时 ZNode，由于 Zookeeper 不能有路径相同的 ZNode，必然只有一个服务节点能够创建成功，此时可以认为该节点获得了锁。其他没有获得锁的服务节点通过在该 ZNode 上注册监听，从而当锁释放时再去竞争获得锁。锁的释放情况有以下两种：

- 当正常执行完业务逻辑后，客户端主动将临时 ZNode 删除，此时锁被释放；
- 当获得锁的客户端发生宕机时，临时 ZNode 会被自动删除，此时认为锁已经释放。

当锁被释放后，其他服务节点则再次去竞争性地进行创建，但每次都只有一个服务节点能够获取到锁，这就是排他锁。

### 7.6. Master 选举

分布式系统一个重要的模式就是主从模式 (Master/Salves)，Zookeeper 可以用于该模式下的 Matser 选举。可以让所有服务节点去竞争性地创建同一个 ZNode，由于 Zookeeper 不能有路径相同的 ZNode，必然只有一个服务节点能够创建成功，这样该服务节点就可以成为 Master 节点。

### 7.7. 队列管理

ZooKeeper 可以处理两种类型的队列：

1. 当一个队列的成员都聚齐时，这个队列才可用，否则一直等待所有成员到达，这种是同步队列。
2. 队列按照 FIFO 方式进行入队和出队操作，例如实现生产者和消费者模型。

同步队列用 ZooKeeper 实现的实现思路如下：

创建一个父目录 /synchronizing，每个成员都监控标志（Set Watch）位目录 /synchronizing/start 是否存在，然后每个成员都加入这个队列，加入队列的方式就是创建 /synchronizing/member_i 的临时目录节点，然后每个成员获取 / synchronizing 目录的所有目录节点，也就是 member_i。判断 i 的值是否已经是成员的个数，如果小于成员个数等待 /synchronizing/start 的出现，如果已经相等就创建 /synchronizing/start。

## 8. Client API

ZooKeeper 有一个绑定 Java 和 C 的官方 API。Zookeeper 社区为大多数语言（.NET，python 等）提供非官方 API。

使用 ZooKeeper API，应用程序可以连接，交互，操作数据，协调，最后断开与 ZooKeeper 集合的连接。

ZooKeeper API 具有丰富的功能，以简单和安全的方式获得 ZooKeeper 集合的所有功能。ZooKeeper API 提供同步和异步方法。

ZooKeeper 集合和 ZooKeeper API 在各个方面都完全相辅相成，对开发人员有很大的帮助。让我们在本章讨论 Java 绑定。

### 8.1. ZooKeeper API 的基础知识

与 ZooKeeper 集合进行交互的应用程序称为 **ZooKeeper 客户端**。

Znode 是 ZooKeeper 集合的核心组件，ZooKeeper API 提供了一小组方法使用 ZooKeeper 集合来操纵 znode 的所有细节。

客户端应该遵循以步骤，与 ZooKeeper 集合进行清晰和干净的交互。

- 连接到 ZooKeeper 集合。ZooKeeper 集合为客户端分配会话 ID。
- 定期向服务器发送心跳。否则，ZooKeeper 集合将过期会话 ID，客户端需要重新连接。
- 只要会话 ID 处于活动状态，就可以获取/设置 znode。
- 所有任务完成后，断开与 ZooKeeper 集合的连接。如果客户端长时间不活动，则 ZooKeeper 集合将自动断开客户端。

### 8.2. Java 绑定

让我们来了解本章中最重要的一组 ZooKeeper API。ZooKeeper API 的核心部分是**ZooKeeper 类**。它提供了在其构造函数中连接 ZooKeeper 集合的选项，并具有以下方法：

- **connect** - 连接到 ZooKeeper 集合
- **create**- 创建 znode
- **exists**- 检查 znode 是否存在及其信息
- **getData** - 从特定的 znode 获取数据
- **setData** - 在特定的 znode 中设置数据
- **getChildren** - 获取特定 znode 中的所有子节点
- **delete** - 删除特定的 znode 及其所有子项
- **close** - 关闭连接

### 8.3. 连接到 ZooKeeper 集合

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

### 8.4. 创建 Znode

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

### 8.5. Exists - 检查 Znode 的存在

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

### 8.6. getData 方法

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

### 8.7. setData 方法

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

### 8.8. getChildren 方法

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

### 8.9. 删除 Znode

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

## 9. 运维

> 安装、配置、命令可以参考：[ZooKeeper 运维指南](zookeeper-ops.md)

## 10. 参考资料

- **ZooKeeper 官方**
  - [官网](http://zookeeper.apache.org/)
  - [官网文档](https://cwiki.apache.org/confluence/display/ZOOKEEPER)
  - [Github](https://github.com/apache/zookeeper)
- **文章**
  - [分布式服务框架 ZooKeeper -- 管理分布式环境中的数据](https://www.ibm.com/developerworks/cn/opensource/os-cn-zookeeper/index.html)
  - [ZooKeeper 的功能以及工作原理](https://www.cnblogs.com/felixzh/p/5869212.html)
  - [Zookeeper 简介及核心概念](https://github.com/heibaiying/BigData-Notes/blob/master/notes/Zookeeper%E7%AE%80%E4%BB%8B%E5%8F%8A%E6%A0%B8%E5%BF%83%E6%A6%82%E5%BF%B5.md)
