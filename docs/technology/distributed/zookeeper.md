# ZooKeeper 应用指南

> **ZooKeeper 是一个分布式应用协调服务** ，由 Apache 进行维护。
>
> ZooKeeper 可以用于发布/订阅、负载均衡、命令服务、分布式协调/通知、集群管理、Master 选举、分布式锁和分布式队列等功能 。

## 简介

### ZooKeeper 是什么

ZooKeeper 主要用来解决分布式集群中应用系统的一致性问题，它能提供基于类似于文件系统的目录节点树方式的数据存储。但是 ZooKeeper 并不是用来专门存储数据的，它的作用主要是用来**维护和监控存储数据的状态变化。通过监控这些数据状态的变化，从而可以达到基于数据的集群管理**。

很多大名鼎鼎的框架都基于 ZooKeeper 来实现分布式高可用，如：Dubbo、Kafka 等。

### ZooKeeper 的特性

ZooKeeper 具有以下特性：

- **高性能** - ZooKeeper 将数据全量存储在内存中，所以其性能很高。_需要注意的是：由于 ZooKeeper 的所有更新和删除都是基于事务的，因此 ZooKeeper 在读多写少的应用场景中有性能表现较好，如果写操作频繁，性能会大大下滑_。
- **顺序一致性**：所有客户端看到的服务端数据模型都是一致的；从一个客户端发起的事务请求，最终都会严格按照其发起顺序被应用到 ZooKeeper 中。具体的实现可见：[原子广播](#原子广播)
- **原子性** - 所有事务请求的处理结果在整个集群中所有机器上的应用情况是一致的，即整个集群要么都成功应用了某个事务，要么都没有应用。 实现方式可见：[事务](#事务)
- **高可用** - ZooKeeper 的高可用是基于副本机制实现的，此外 ZooKeeper 支持故障恢复，可见：[选举 Leader](#选举-Leader)
- **单一视图** - 无论客户端连接的是哪个 Zookeeper 服务器，其看到的服务端数据模型都是一致的。

## 架构

Zookeeper 集群是一个基于主从复制的高可用集群，每个服务器承担如下三种角色中的一种

- **Leader** - 一个 Zookeeper 集群同一时间只会有一个实际工作的 Leader，它会发起并维护与各 Follwer 及 Observer 间的心跳。所有的写操作必须要通过 Leader 完成再由 Leader 将写操作广播给其它服务器。
- **Follower** - 一个 Zookeeper 集群可能同时存在多个 Follower，它会响应 Leader 的心跳。Follower 可直接处理并返回客户端的读请求，同时会将写请求转发给 Leader 处理，并且负责在 Leader 处理写请求时对请求进行投票。
- **Observer** - 角色与 Follower 类似，但是无投票权。

### 读操作

Leader/Follower/Observer 都可直接处理读请求，从本地内存中读取数据并返回给客户端即可。

由于处理读请求不需要服务器之间的交互，Follower/Observer 越多，整体可处理的读请求量越大，也即读性能越好。

<div align="center">
<img src="http://dunwu.test.upcdn.net/cs/distributed/zookeeper/zookeeper_3.png!zp" />
</div>

### 写操作

#### 写 Leader

<div align="center">
<img src="http://dunwu.test.upcdn.net/cs/distributed/zookeeper/zookeeper_4.png!zp" width="600" />
</div>

由上图可见，通过 Leader 进行写操作，主要分为五步：

1. 客户端向 Leader 发起写请求
2. Leader 将写请求以 Proposal 的形式发给所有 Follower 并等待 ACK
3. Follower 收到 Leader 的 Proposal 后返回 ACK
4. Leader 得到过半数的 ACK（Leader 对自己默认有一个 ACK）后向所有的 Follower 和 Observer 发送 Commmit
5. Leader 将处理结果返回给客户端

> 注意
>
> - Leader 并不需要得到 Observer 的 ACK，即 Observer 无投票权
> - Leader 不需要得到所有 Follower 的 ACK，只要收到过半的 ACK 即可，同时 Leader 本身对自己有一个 ACK。上图中有 4 个 Follower，只需其中两个返回 ACK 即可，因为(2+1) / (4+1) > 1/2
> - Observer 虽然无投票权，但仍须同步 Leader 的数据从而在处理读请求时可以返回尽可能新的数据

#### 写 Follower/Observer

<div align="center">
<img src="http://dunwu.test.upcdn.net/cs/distributed/zookeeper/zookeeper_5.png!zp" />
</div>

- Follower/Observer 均可接受写请求，但不能直接处理，而需要将写请求转发给 Leader 处理
- 除了多了一步请求转发，其它流程与直接写 Leader 无任何区别

## 原理

### 数据模型

**ZooKeeper 可以视为一个高可用的文件系统**。

ZooKeeper 维护着一个树形层次结构，树中的节点被称为 **`znode`**。其中根节点为 `/`，每个节点上都会保存自己的数据和节点信息。znode 可以用于存储数据，并且有一个与之相关联的 ACL（详情可见 [ACL](#ACL)）。ZooKeeper 的设计目标是实现协调服务，而不是真的作为一个文件存储，因此 znode 存储数据的大小被限制在 1MB 以内。

<div align="center">
<img src="http://dunwu.test.upcdn.net/cs/distributed/zookeeper/zookeeper_1.png!zp" width="400px" />
</div>

ZooKeeper 这种数据结构有如下这些特点：

**ZooKeeper 的数据访问具有原子性**。客户端在读取一个 znode 数据时，要么成功读取所有数据，要么读取失败。**znode 节点路径必须是绝对路径**。

znode 有两种类型：

- **临时的（ `PERSISTENT` ）** - 户端会话结束时，ZooKeeper 就会删除临时的 znode。
- **持久的（ `EPHEMERAL` ）** - 除非客户端主动执行删除操作，否则 ZooKeeper 不会删除持久的 znode。

znode 上有一个**顺序标志（ `SEQUENTIAL` ）**。如果在创建 znode 时，设置了**顺序标志（ `SEQUENTIAL` ）**，那么 ZooKeeper 会使用计数器为 znode 添加一个单调递增的数值，即 zxid。ZooKeeper 正是利用 zxid 实现了严格的顺序访问控制能力。

每个 znode 节点在存储数据的同时，都会维护一个叫做 `Stat` 的数据结构，里面存储了关于该节点的全部状态信息。如下：

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

### 事务

对于来自客户端的每个更新请求，ZooKeeper 具备严格的顺序访问控制能力。

**为了保证事务的顺序一致性，ZooKeeper 采用了递增的事务 id 号（zxid）来标识事务**。

Leader 服务会为每一个 Follower 服务器分配一个单独的队列，然后将事务 Proposal 依次放入队列中，并根据 FIFO(先进先出) 的策略进行消息发送。Follower 服务在接收到 Proposal 后，会将其以事务日志的形式写入本地磁盘中，并在写入成功后反馈给 Leader 一个 Ack 响应。当 Leader 接收到超过半数 Follower 的 Ack 响应后，就会广播一个 Commit 消息给所有的 Follower 以通知其进行事务提交，之后 Leader 自身也会完成对事务的提交。而每一个 Follower 则在接收到 Commit 消息后，完成事务的提交。

所有的提议（**`proposal`**）都在被提出的时候加上了 zxid。zxid 是一个 64 位的数字，它的高 32 位是 **`epoch`** 用来标识 Leader 关系是否改变，每次一个 Leader 被选出来，它都会有一个新的 epoch，标识当前属于那个 leader 的统治时期。低 32 位用于递增计数。

详细过程如下：

1. Leader 等待 server 连接；
2. Follower 连接 Leader，将最大的 zxid 发送给 Leader；
3. Leader 根据 Follower 的 zxid 确定同步点；
4. 完成同步后通知 follower 已经成为 uptodate 状态；
5. Follower 收到 uptodate 消息后，又可以重新接受 client 的请求进行服务了。

### 通知

客户端注册监听它关心的 znode，当 znode 状态发生变化（数据改变、被删除、子目录节点增加删除）时，ZooKeeper 服务会通知客户端。

客户端和服务端保持连接一般有两种形式：

- **客户端向服务端不断轮询**
- **服务端向客户端推送状态**

Zookeeper 的选择是服务端主动推送状态，也就是观察机制（ `Watch` ）。

ZooKeeper 的观察机制允许用户在指定节点上针对感兴趣的事件注册监听，当事件发生时，监听器会被触发，并将事件信息推送到客户端。

客户端使用 `getData` 等接口获取 znode 状态时传入了一个用于处理节点变更的回调，那么服务端就会主动向客户端推送节点的变更：

```java
public byte[] getData(final String path, Watcher watcher, Stat stat)
```

从这个方法中传入的 `Watcher` 对象实现了相应的 `process` 方法，每次对应节点出现了状态的改变，`WatchManager` 都会通过以下的方式调用传入 `Watcher` 的方法：

```java
Set<Watcher> triggerWatch(String path, EventType type, Set<Watcher> supress) {
    WatchedEvent e = new WatchedEvent(type, KeeperState.SyncConnected, path);
    Set<Watcher> watchers;
    synchronized (this) {
        watchers = watchTable.remove(path);
    }
    for (Watcher w : watchers) {
        w.process(e);
    }
    return watchers;
}
```

Zookeeper 中的所有数据其实都是由一个名为 `DataTree` 的数据结构管理的，所有的读写数据的请求最终都会改变这颗树的内容，在发出读请求时可能会传入 `Watcher` 注册一个回调函数，而写请求就可能会触发相应的回调，由 `WatchManager` 通知客户端数据的变化。

通知机制的实现其实还是比较简单的，通过读请求设置 `Watcher` 监听事件，写请求在触发事件时就能将通知发送给指定的客户端。

### 会话

ZooKeeper 客户端通过 TCP 长连接连接到 ZooKeeper 服务集群，会话 (Session) 从第一次连接开始就已经建立，之后通过心跳检测机制来保持有效的会话状态。通过这个连接，客户端可以发送请求并接收响应，同时也可以接收到 Watch 事件的通知。

每个 ZooKeeper 客户端配置中都配置了 ZooKeeper 服务器集群列表。启动时，客户端会遍历列表去尝试建立连接。

一旦一台客户端与一台服务器建立连接，这台服务器会为这个客户端创建一个新的会话。**每个会话都会有一个超时时间，若服务器在超时时间内没有收到任何请求，则相应会话被视为过期**。一旦会话过期，就无法再重新打开，且任何与该会话相关的临时 znode 都会被删除。

通常来说，会话应该长期存在，而这需要由客户端来保证。客户端可以通过心跳方式（ping）来保持会话不过期。

ZooKeeper 的会话具有四个属性：

- `sessionID` - 会话 ID，唯一标识一个会话，每次客户端创建新的会话时，Zookeeper 都会为其分配一个全局唯一的 sessionID。
- `TimeOut` - 会话超时时间，客户端在构造 Zookeeper 实例时，会配置 sessionTimeout 参数用于指定会话的超时时间，Zookeeper 客户端向服务端发送这个超时时间后，服务端会根据自己的超时时间限制最终确定会话的超时时间。
- `TickTime` - 下次会话超时时间点，为了便于 Zookeeper 对会话实行”分桶策略”管理，同时为了高效低耗地实现会话的超时检查与清理，Zookeeper 会为每个会话标记一个下次会话超时时间点，其值大致等于当前时间加上 TimeOut。
- `isClosing` - 标记一个会话是否已经被关闭，当服务端检测到会话已经超时失效时，会将该会话的 isClosing 标记为”已关闭”，这样就能确保不再处理来自该会话的心情求了。

Zookeeper 的会话管理主要是通过 `SessionTracker` 来负责，其采用了**分桶策略**（将类似的会话放在同一区块中进行管理）进行管理，以便 Zookeeper 对会话进行不同区块的隔离处理以及同一区块的统一处理。

### ZAB 协议

ZooKeeper 实现数据一致性和高可用这两个分布式系统标配特性是基于 Zab 协议。

> 注意：Zab 协议不是 Paxos 算法，只是比较类似，二者在操作上并不相同。

Zab 协议定义了两个可以无限循环的流程：

- 原子广播 - 用于主从同步，从而保证数据一致性
- 选举 Leader - 用于故障恢复，从而保证高可用

#### 选举 Leader

> **ZooKeeper 的故障恢复**
>
> ZooKeeper 集群采用一主（称为 Leader）多从（称为 Follower）模式，主从节点通过副本机制保证数据一致。
>
> - **如果 Follower 节点挂了** - ZooKeeper 集群中的每个节点都会单独在内存中维护自身的状态，并且各节点之间都保持着通讯，**只要集群中有半数机器能够正常工作，那么整个集群就可以正常提供服务**。
> - **如果 Leader 节点挂了** - 如果 Leader 节点挂了，系统就不能正常工作了。此时，需要通过 Zab 协议的选举 Leader 机制来进行故障恢复。
>
> Zab 协议的选举 Leader 机制简单来说，就是：基于过半选举机制产生新的 Leader，之后其他机器将从新的 Leader 上同步状态，当有过半机器完成状态同步后，就退出选举 Leader 模式，进入原子广播模式。

##### 术语

- **myid** - 每个 Zookeeper 服务器，都需要在数据文件夹下创建一个名为 myid 的文件，该文件包含整个 Zookeeper 集群唯一的 ID（整数）。
- **zxid** - 类似于 RDBMS 中的事务 ID，用于标识一次更新操作的 Proposal ID。为了保证顺序性，该 zkid 必须单调递增。因此 Zookeeper 使用一个 64 位的数来表示，高 32 位是 Leader 的 epoch，从 1 开始，每次选出新的 Leader，epoch 加一。低 32 位为该 epoch 内的序号，每次 epoch 变化，都将低 32 位的序号重置。这样保证了 zkid 的全局递增性。

##### 服务器状态

- **_LOOKING_** - 不确定 Leader 状态。该状态下的服务器认为当前集群中没有 Leader，会发起 Leader 选举
- **_FOLLOWING_** - 跟随者状态。表明当前服务器角色是 Follower，并且它知道 Leader 是谁
- **_LEADING_** - 领导者状态。表明当前服务器角色是 Leader，它会维护与 Follower 间的心跳
- **_OBSERVING_** - 观察者状态。表明当前服务器角色是 Observer，与 Folower 唯一的不同在于不参与选举，也不参与集群写操作时的投票

##### 选票数据结构

每个服务器在进行领导选举时，会发送如下关键信息

- **_logicClock_** 每个服务器会维护一个自增的整数，名为 logicClock，它表示这是该服务器发起的第多少轮投票
- **_state_** 当前服务器的状态
- **_self_id_** 当前服务器的 myid
- **_self_zxid_** 当前服务器上所保存的数据的最大 zxid
- **_vote_id_** 被推举的服务器的 myid
- **_vote_zxid_** 被推举的服务器上所保存的数据的最大 zxid

##### 投票流程

- **_自增选举轮次_** - Zookeeper 规定所有有效的投票都必须在同一轮次中。每个服务器在开始新一轮投票时，会先对自己维护的 logicClock 进行自增操作。
- **_初始化选票_** - 每个服务器在广播自己的选票前，会将自己的投票箱清空。该投票箱记录了所收到的选票。例：服务器 2 投票给服务器 3，服务器 3 投票给服务器 1，则服务器 1 的投票箱为(2, 3), (3, 1), (1, 1)。票箱中只会记录每一投票者的最后一票，如投票者更新自己的选票，则其它服务器收到该新选票后会在自己票箱中更新该服务器的选票。

- **_发送初始化选票_** - 每个服务器最开始都是通过广播把票投给自己。

- **_接收外部投票_** - 服务器会尝试从其它服务器获取投票，并记入自己的投票箱内。如果无法获取任何外部投票，则会确认自己是否与集群中其它服务器保持着有效连接。如果是，则再次发送自己的投票；如果否，则马上与之建立连接。

- **_判断选举轮次_** - 收到外部投票后，首先会根据投票信息中所包含的 logicClock 来进行不同处理

  - 外部投票的 logicClock 大于自己的 logicClock。说明该服务器的选举轮次落后于其它服务器的选举轮次，立即清空自己的投票箱并将自己的 logicClock 更新为收到的 logicClock，然后再对比自己之前的投票与收到的投票以确定是否需要变更自己的投票，最终再次将自己的投票广播出去。
  - 外部投票的 logicClock 小于自己的 logicClock。当前服务器直接忽略该投票，继续处理下一个投票。
  - 外部投票的 logickClock 与自己的相等。当时进行选票 PK。
- **_选票 PK_** - 选票 PK 是基于(self_id, self_zxid)与(vote_id, vote_zxid)的对比
  - 外部投票的 logicClock 大于自己的 logicClock，则将自己的 logicClock 及自己的选票的 logicClock 变更为收到的 logicClock
  - 若 logicClock 一致，则对比二者的 vote_zxid，若外部投票的 vote_zxid 比较大，则将自己的票中的 vote_zxid 与 vote_myid 更新为收到的票中的 vote_zxid 与 vote_myid 并广播出去，另外将收到的票及自己更新后的票放入自己的票箱。如果票箱内已存在(self_myid, self_zxid)相同的选票，则直接覆盖
  - 若二者 vote_zxid 一致，则比较二者的 vote_myid，若外部投票的 vote_myid 比较大，则将自己的票中的 vote_myid 更新为收到的票中的 vote_myid 并广播出去，另外将收到的票及自己更新后的票放入自己的票箱
- **_统计选票_** - 如果已经确定有过半服务器认可了自己的投票（可能是更新后的投票），则终止投票。否则继续接收其它服务器的投票。

- **_更新服务器状态_** - 投票终止后，服务器开始更新自身状态。若过半的票投给了自己，则将自己的服务器状态更新为 LEADING，否则将自己的状态更新为 FOLLOWING

通过以上流程分析，我们不难看出：要使 Leader 获得多数 Server 的支持，则 **ZooKeeper 集群节点数必须是奇数。且存活的节点数目不得少于 `N + 1`** 。

每个 Server 启动后都会重复以上流程。在恢复模式下，如果是刚从崩溃状态恢复的或者刚启动的 server 还会从磁盘快照中恢复数据和会话信息，zk 会记录事务日志并定期进行快照，方便在恢复时进行状态恢复。

#### 原子广播

**ZooKeeper 通过副本机制来实现高可用**。只要集群中半数以上机器处于可用状态，就能正常工作。

简单来说：ZooKeeper 会确保对 znode 所做的每一个修改都会被复制到集群中超过半数以上的机器上。如果少于半数的机器出现故障，则最少有一台机器保存了最新状态，其余的副本最终也会更新到这个状态。

那么，ZooKeeper 是如何实现副本机制的呢？答案是：Zab 协议的原子广播。

<div align="center">
<img src="http://dunwu.test.upcdn.net/cs/distributed/zookeeper/zookeeper_3.png!zp" width="650px" />
</div>

Zab 协议的原子广播要求：

所有的写请求都会被转发给 Leader，Leader 会以原子广播的方式通知 Follow。当半数以上的 Follow 已经更新状态持久化后，Leader 才会提交这个更新，然后客户端才会收到一个更新成功的响应。这有些类似数据库中的两阶段提交协议。

在整个消息的广播过程中，Leader 服务器会每个事物请求生成对应的 Proposal，并为其分配一个全局唯一的递增的事务 ID(ZXID)，之后再对其进行广播。

### ACL

每个 znode 创建时都会带有一个 ACL 列表，用于决定谁可以对它执行何种操作。

ACL 依赖于 ZooKeeper 的客户端认证机制。ZooKeeper 提供了以下几种认证方式：

- **digest** - 用户名和密码 来识别客户端
- **sasl** - 通过 kerberos 来识别客户端
- **ip** - 通过 IP 来识别客户端

ZooKeeper 定义了如下五种权限：

- **CREATE** - 允许创建子节点；
- **READ** - 允许从节点获取数据并列出其子节点；
- **WRITE** - 允许为节点设置数据；
- **DELETE** - 允许删除子节点；
- **ADMIN** - 允许为节点设置权限。

## 应用

> **ZooKeeper 可以用于发布/订阅、负载均衡、命令服务、分布式协调/通知、集群管理、Master 选举、分布式锁和分布式队列等功能** 。

### 数据的发布/订阅

数据的发布/订阅系统，通常也用作配置中心。在分布式系统中，你可能有成千上万个服务节点，如果想要对所有服务的某项配置进行更改，由于数据节点过多，你不可逐台进行修改，而应该在设计时采用统一的配置中心。之后发布者只需要将新的配置发送到配置中心，所有服务节点即可自动下载并进行更新，从而实现配置的集中管理和动态更新。

ZooKeeper 通过 Watcher 机制可以实现数据的发布和订阅。分布式系统的所有的服务节点可以对某个 ZNode 注册监听，之后只需要将新的配置写入该 ZNode，所有服务节点都会收到该事件。

### 命名服务

在分布式系统中，通常需要一个全局唯一的名字，如生成全局唯一的订单号等，ZooKeeper 可以通过顺序节点的特性来生成全局唯一 ID，从而可以对分布式系统提供命名服务。

### 配置管理

配置的管理在分布式应用环境中很常见，例如同一个应用系统需要多台 PC Server 运行，但是它们运行的应用系统的某些配置项是相同的，如果要修改这些相同的配置项，那么就必须同时修改每台运行这个应用系统的 PC Server，这样非常麻烦而且容易出错。

像这样的配置信息完全可以交给 ZooKeeper 来管理，将配置信息保存在 ZooKeeper 的某个目录节点中，然后将所有需要修改的应用机器监控配置信息的状态，一旦配置信息发生变化，每台应用机器就会收到 ZooKeeper 的通知，然后从 ZooKeeper 获取新的配置信息应用到系统中。

### 集群管理

ZooKeeper 还能解决大多数分布式系统中的问题：

- 如可以通过创建临时节点来建立心跳检测机制。如果分布式系统的某个服务节点宕机了，则其持有的会话会超时，此时该临时节点会被删除，相应的监听事件就会被触发。
- 分布式系统的每个服务节点还可以将自己的节点状态写入临时节点，从而完成状态报告或节点工作进度汇报。
- 通过数据的订阅和发布功能，ZooKeeper 还能对分布式系统进行模块的解耦和任务的调度。
- 通过监听机制，还能对分布式系统的服务节点进行动态上下线，从而实现服务的动态扩容。

### 分布式锁

可以通过 ZooKeeper 的临时节点和 Watcher 机制来实现分布式锁，这里以排它锁为例进行说明：

分布式系统的所有服务节点可以竞争性地去创建同一个临时 ZNode，由于 ZooKeeper 不能有路径相同的 ZNode，必然只有一个服务节点能够创建成功，此时可以认为该节点获得了锁。其他没有获得锁的服务节点通过在该 ZNode 上注册监听，从而当锁释放时再去竞争获得锁。锁的释放情况有以下两种：

- 当正常执行完业务逻辑后，客户端主动将临时 ZNode 删除，此时锁被释放；
- 当获得锁的客户端发生宕机时，临时 ZNode 会被自动删除，此时认为锁已经释放。

当锁被释放后，其他服务节点则再次去竞争性地进行创建，但每次都只有一个服务节点能够获取到锁，这就是排他锁。

### 选举 Leader

分布式系统一个重要的模式就是主从模式 (Master/Salves)，ZooKeeper 可以用于该模式下的 Matser 选举。可以让所有服务节点去竞争性地创建同一个 ZNode，由于 ZooKeeper 不能有路径相同的 ZNode，必然只有一个服务节点能够创建成功，这样该服务节点就可以成为 Master 节点。

### 队列管理

ZooKeeper 可以处理两种类型的队列：

1. 当一个队列的成员都聚齐时，这个队列才可用，否则一直等待所有成员到达，这种是同步队列。
2. 队列按照 FIFO 方式进行入队和出队操作，例如实现生产者和消费者模型。

同步队列用 ZooKeeper 实现的实现思路如下：

创建一个父目录 /synchronizing，每个成员都监控标志（Set Watch）位目录 /synchronizing/start 是否存在，然后每个成员都加入这个队列，加入队列的方式就是创建 /synchronizing/member_i 的临时目录节点，然后每个成员获取 / synchronizing 目录的所有目录节点，也就是 member_i。判断 i 的值是否已经是成员的个数，如果小于成员个数等待 /synchronizing/start 的出现，如果已经相等就创建 /synchronizing/start。

## Client API

> ZooKeeper 官方支持 Java 和 C 的 Client API。ZooKeeper 社区为大多数语言（.NET，python 等）提供非官方 API。
>
> 本文以 Java Client API 来进行介绍。

### Client API 简介

客户端和服务端交互遵循以下基本步骤：

1. 客户端连接 ZooKeeper 服务端集群任意工作节点，该节点为客户端分配会话 ID。
2. 为了保持通信，客户端需要和服务端保持心跳（实质上就是 ping ）。否则，ZooKeeper 服务会话超时时间内未收到客户端请求，会将会话视为过期。这种情况下，客户端如果要通信，就需要重新连接。
3. 只要会话 ID 处于活动状态，就可以执行读写 znode 操作。
4. 所有任务完成后，客户端断开与 ZooKeeper 服务端集群的连接。如果客户端长时间不活动，则 ZooKeeper 集合将自动断开客户端。

ZooKeeper Client API 的核心是 **`ZooKeeper` 类**。它在其构造函数中提供了连接 ZooKeeper 服务的配置选项，并提供了访问 ZooKeeper 数据的方法。

> 其主要操作如下：
>
> - **`connect`** - 连接 ZooKeeper 服务
> - **`create`** - 创建 znode
> - **`exists`** - 检查 znode 是否存在及其信息
> - **`getData`** - 从特定的 znode 获取数据
> - **`setData`** - 在特定的 znode 中设置数据
> - **`getChildren`** - 获取特定 znode 中的所有子节点
> - **`delete`** - 删除特定的 znode 及其所有子项
> - **`close`** - 关闭连接

### 引入依赖

maven 项目使用 ZooKeeper Client，只需在 pom.xml 中添加：

```xml
		<dependency>
			<groupId>org.apache.curator</groupId>
			<artifactId>curator-recipes</artifactId>
			<version>4.2.0</version>
		</dependency>
```

### 连接 ZooKeeper

ZooKeeper 类通过其构造函数提供连接 ZooKeeper 服务的功能。其构造函数的定义如下：

```java
ZooKeeper(String connectionString, int sessionTimeout, Watcher watcher)
```

> 参数说明：
>
> - **`connectionString`** - ZooKeeper 集群的主机列表。
> - **`sessionTimeout`** - 会话超时时间（以毫秒为单位）。
> - **watcher** - 实现监视机制的回调。当被监控的 znode 状态发生变化时，ZooKeeper 服务端的 `WatcherManager` 会主动调用传入的 Watcher ，推送状态变化给客户端。

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

### 创建 znode

ZooKeeper 类提供了在 ZooKeeper 集合中创建一个新的 znode 的 **`create`** 方法。 **create** 方法的签名如下：

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

### 检查 znode 是否存在

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

## 运维

> 安装、配置、命令可以参考：[ZooKeeper 运维指南](zookeeper-ops.md)

## 参考资料

- **官方**
  - [ZooKeeper 官网](http://zookeeper.apache.org/)
  - [ZooKeeper 官方文档](https://cwiki.apache.org/confluence/display/ZOOKEEPER)
  - [ZooKeeper Github](https://github.com/apache/zookeeper)
- **书籍**
  - [Hadoop 权威指南（第四版）](https://item.jd.com/12109713.html)
- **文章**
  - [分布式服务框架 ZooKeeper -- 管理分布式环境中的数据](https://www.ibm.com/developerworks/cn/opensource/os-cn-zookeeper/index.html)
  - [ZooKeeper 的功能以及工作原理](https://www.cnblogs.com/felixzh/p/5869212.html)
  - [ZooKeeper 简介及核心概念](https://github.com/heibaiying/BigData-Notes/blob/master/notes/ZooKeeper%E7%AE%80%E4%BB%8B%E5%8F%8A%E6%A0%B8%E5%BF%83%E6%A6%82%E5%BF%B5.md)
  - [详解分布式协调服务 ZooKeeper](https://draveness.me/zookeeper-chubby)
  - [深入浅出 Zookeeper（一） Zookeeper 架构及 FastLeaderElection 机制](http://www.jasongj.com/zookeeper/fastleaderelection/)
