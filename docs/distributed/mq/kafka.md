---
title: Kafka
date: 2018/06/13
categories:
- 分布式
tags:
- 分布式
- message
---

# Kafka

> Kafka 是一个分布式的、可水平扩展的、基于发布/订阅模式的、支持容错的消息系统。

<!-- TOC depthFrom:2 depthTo:3 -->

- [概述](#概述)
    - [分布式](#分布式)
    - [容错](#容错)
    - [提交日志](#提交日志)
    - [消息队列](#消息队列)
    - [为什么要使用消息系统](#为什么要使用消息系统)
    - [Kafka 的关键功能](#kafka-的关键功能)
    - [Kafka 基本概念](#kafka-基本概念)
    - [Kafka 核心 API](#kafka-核心-api)
    - [Topic 和日志](#topic-和日志)
- [Kafka 工作原理](#kafka-工作原理)
    - [术语](#术语)
- [持久化](#持久化)
- [复制](#复制)
- [流处理](#流处理)
    - [无状态处理](#无状态处理)
    - [有状态处理](#有状态处理)
- [Kafka 应用场景](#kafka-应用场景)
- [资料](#资料)
    - [官方资料](#官方资料)
    - [第三方资料](#第三方资料)

<!-- /TOC -->

## 概述

### 分布式

分布式系统是一个由多个运行机器组成的系统，所有这些机器在一个集群中一起工作，对最终端用户表现为一个节点。

Kafka 的分布式意义在于：它在不同的节点上存储、接收和发送消息。

### 容错

分布式系统一般都会设计容错机制，保证集群中几个节点出现故障时，仍能对外提供服务。

### 提交日志

提交日志（也称为预写日志，事务日志）是仅支持附加的持久有序数据结构。您不能修改或删除记录。它从左到右读取并保证项目排序。

Kafka 实际上将所有的消息存储到磁盘，并在结构中对它们进行排序，以便利用顺序磁盘读取。

### 消息队列

消息队列技术是分布式应用间交换信息的一种技术。消息队列可驻留在内存或磁盘上, 队列存储消息直到它们被应用程序读走。通过消息队列，应用程序可独立地执行--它们不需要知道彼此的位置、或在继续执行前不需要等待接收程序接收此消息。在分布式计算环境中，为了集成分布式应用，开发者需要对异构网络环境下的分布式应用提供有效的通信手段。为了管理需要共享的信息，对应用提供公共的信息交换机制是重要的。常用的消息队列技术是 Message Queue。

Message Queue 的通信模式：

- **点对点**：点对点方式是最为传统和常见的通讯方式，它支持一对一、一对多、多对多、多对一等多种配置方式，支持树状、网状等多种拓扑结构。
- **多点广播**：MQ 适用于不同类型的应用。其中重要的，也是正在发展中的是"多点广播"应用，即能够将消息发送到多个目标站点 (Destination List)。可以使用一条 MQ 指令将单一消息发送到多个目标站点，并确保为每一站点可靠地提供信息。MQ 不仅提供了多点广播的功能，而且还拥有智能消息分发功能，在将一条消息发送到同一系统上的多个用户时，MQ 将消息的一个复制版本和该系统上接收者的名单发送到目标 MQ 系统。目标 MQ 系统在本地复制这些消息，并将它们发送到名单上的队列，从而尽可能减少网络的传输量。
- **发布/订阅 (Publish/Subscribe)**：发布/订阅功能使消息的分发可以突破目的队列地理指向的限制，使消息按照特定的主题甚至内容进行分发，用户或应用程序可以根据主题或内容接收到所需要的消息。发布/订阅功能使得发送者和接收者之间的耦合关系变得更为松散，发送者不必关心接收者的目的地址，而接收者也不必关心消息的发送地址，而只是根据消息的主题进行消息的收发。
- **集群 (Cluster)**：为了简化点对点通讯模式中的系统配置，MQ 提供 Cluster(集群) 的解决方案。集群类似于一个域 (Domain)，集群内部的队列管理器之间通讯时，不需要两两之间建立消息通道，而是采用集群 (Cluster) 通道与其它成员通讯，从而大大简化了系统配置。此外，集群中的队列管理器之间能够自动进行负载均衡，当某一队列管理器出现故障时，其它队列管理器可以接管它的工作，从而大大提高系统的高可靠性。

### 为什么要使用消息系统

- 解耦
  在项目启动之初来预测将来项目会碰到什么需求，是极其困难的。消息系统在处理过程中间插入了一个隐含的、基于数据的接口层，两边的处理过程都要实现这一接口。这允许你独立的扩展或修改两边的处理过程，只要确保它们遵守同样的接口约束。
- 冗余
  有些情况下，处理数据的过程会失败。除非数据被持久化，否则将造成丢失。消息队列把数据进行持久化直到它们已经被完全处理，通过这一方式规避了数据丢失风险。许多消息队列所采用的"插入-获取-删除"范式中，在把一个消息从队列中删除之前，需要你的处理系统明确的指出该消息已经被处理完毕，从而确保你的数据被安全的保存直到你使用完毕。
- 扩展性
  因为消息队列解耦了你的处理过程，所以增大消息入队和处理的频率是很容易的，只要另外增加处理过程即可。不需要改变代码、不需要调节参数。扩展就像调大电力按钮一样简单。
- 灵活性 & 峰值处理能力
  在访问量剧增的情况下，应用仍然需要继续发挥作用，但是这样的突发流量并不常见；如果为以能处理这类峰值访问为标准来投入资源随时待命无疑是巨大的浪费。使用消息队列能够使关键组件顶住突发的访问压力，而不会因为突发的超负荷的请求而完全崩溃。
- 可恢复性
  系统的一部分组件失效时，不会影响到整个系统。消息队列降低了进程间的耦合度，所以即使一个处理消息的进程挂掉，加入队列中的消息仍然可以在系统恢复后被处理。
- 顺序保证
  在大多使用场景下，数据处理的顺序都很重要。大部分消息队列本来就是排序的，并且能保证数据会按照特定的顺序来处理。Kafka 保证一个 Partition 内的消息的有序性。
- 缓冲
  在任何重要的系统中，都会有需要不同的处理时间的元素。例如，加载一张图片比应用过滤器花费更少的时间。消息队列通过一个缓冲层来帮助任务最高效率的执行———写入队列的处理会尽可能的快速。该缓冲有助于控制和优化数据流经过系统的速度。
- 异步通信
  很多时候，用户不想也不需要立即处理消息。消息队列提供了异步处理机制，允许用户把一个消息放入队列，但并不立即处理它。想向队列中放入多少消息就放多少，然后在需要的时候再去处理它们。

### Kafka 的关键功能

- 发布和订阅流记录，类似于消息队列或企业级消息系统。
- 以容错、持久化的方式存储流记录。
- 处理流记录。

### Kafka 基本概念

- Kafka 作为一个集群运行在一台或多台可以跨越多个数据中心的服务器上。
- Kafka 集群在称为 Topic 的类别中存储记录流。
- Kafka 的每个记录由一个键，一个值和一个时间戳组成。

### Kafka 核心 API

<div align="center">
<img src="https://raw.githubusercontent.com/dunwu/java-web/master/images/distributed/mq/kafka/kafka-core-api.png" width="400"/>
</div>

- Producer - 允许应用程序将记录流发布到一个或多个 Kafka Topic。
- Consumer - 允许应用程序订阅一个或多个 Topic 并处理为他们生成的记录流。
- Streams - 允许应用程序充当流处理器，从一个或多个 Topic 中消费输入流，并将输出流生成为一个或多个输出 Topic，从而将输入流有效地转换为输出流。
- Connector - 允许构建和运行可重复使用的生产者或消费者，将 Kafka Topic 连接到现有的应用程序或数据系统。例如，连接到关系数据库的连接器可能会捕获对表的每个更改。

在 Kafka 中，客户端和服务器之间的通信是采用 TCP 协议方式。

### Topic 和日志

Topic 是一个目录名，它保存着发布记录。kafka 的 Topic 始终是多订阅者的，也就是说，一个主题可以有零个，一个或多个订阅写入数据的 Consumer。

在 Kafka 中，任意一个 Topic 维护一个 Partition 的日志，类似下图：

<div align="center">
<img src="https://raw.githubusercontent.com/dunwu/java-web/master/images/distributed/mq/kafka/kafka-log-anatomy.png" width="400"/>
</div>

每个 Partition 都是一个有序的，不可变的记录序列，不断追加到结构化的提交日志中。Partition 中的记录每个分配一个连续的 id 号，称为偏移量，用于唯一标识 Partition 内的每条记录。

Kafka 集群持久化保存（使用可配置的保留期限）所有发布记录——无论它们是否被消费。例如，如果保留期限被设置为两天，则在记录发布后的两天之内，它都可以被消费，超过时间后将被丢弃以释放空间。

<div align="center">
<img src="https://raw.githubusercontent.com/dunwu/java-web/master/images/distributed/mq/kafka/kafka-log-consumer.png" width="400"/>
</div>

实际上，保留在每个 Consumer 基础上的唯一元数据是该 Consumer 在日志中的抵消或位置。这个偏移量是由 Consumer 控制的：Consumer 通常会在读取记录时线性地推进其偏移量，但实际上，由于位置由 Consumer 控制，因此它可以按照喜欢的任何顺序消费记录。

这种功能组合意味着 Kafka Consumer 的开销很小——它们的出现对集群和其他 Consumer 没有多少影响。

日志中的 Partition 有多种目的。首先，它们允许日志的大小超出服务器限制的大小。每个单独的 Partition 必须适合承载它的服务器，但是一个主题可能有很多 Partition，因此它可以处理任意数量的数据。其次，它们作为并行的单位。

## Kafka 工作原理

### 术语

- **Broker** - Kafka 集群包含一个或多个服务器，这种服务器被称为 broker。
- **Topic** - 每条发布到 Kafka 集群的消息都有一个类别，这个类别被称为 Topic。（物理上不同 Topic 的消息分开存储，逻辑上一个 Topic 的消息虽然保存于一个或多个 broker 上但用户只需指定消息的 Topic 即可生产或消费数据而不必关心数据存于何处）。
- **Partition** - Parition 是物理上的概念，每个 Topic 包含一个或多个 Partition。
- **Producer** - 负责发布消息到 Kafka broker。
- **Consumer** - 消息消费者，向 Kafka broker 读取消息的客户端。
- **Consumer Group** - 每个 Consumer 属于一个特定的 Consumer Group（可为每个 Consumer 指定 group name，若不指定 group name 则属于默认的 group）。

Producer 将消息（记录）发送到 Kafka 节点（Broker），消息由称为 Consumer 的其他应用程序处理。消息被存储在 Topic 中，并且 Consumer 订阅该主题以接收新消息。

随着 Topic 变得日益庞大，它们会被分割成更小的 Partition 以提高性能和可伸缩性。Kafka 保证 Partition 内的所有消息按照它们出现的顺序排序。区分特定消息的方式是通过它的偏移量，您可以将它看作普通数组索引，每个新消息都会增加一个序列号在一个 Partition 中。

<div align="center">
<img src="https://raw.githubusercontent.com/dunwu/java-web/master/images/distributed/mq/kafka/kafka-log-anatomy.png" width="400"/>
</div>

Kafka 遵循发布/订阅模式。这意味着 Kafka 不会跟踪 Kafka 读取哪些记录并删除它们，而是将它们存储一段时间（例如一天）或直到满足某个大小阈值。Consumer 自己对 Kafka 进行新的消息调查并说出他们想要阅读的记录。这使得他们可以按照自己的意愿递增/递减偏移量，从而能够重播和重新处理事件。

Kafka 集群持久化保存（使用可配置的保留期限）所有发布记录——无论它们是否被消费。例如，如果保留期限被设置为两天，则在记录发布后的两天之内，它都可以被消费，超过时间后将被丢弃以释放空间。

值得注意的是，Consumer 实际上是内部拥有一个或多个 Consumer 流程的 Consumer 群体。为了避免两个进程读两次相同的消息，每个 Partition 仅与每个组的一个 Consumer 进程相关联。

<div align="center">
<img src="https://raw.githubusercontent.com/dunwu/java-web/master/images/distributed/mq/kafka/kafka-producer-consumer.png" width="640"/>
</div>

## 持久化

Kafka 实际上将其所有记录存储在磁盘中，并且不会将任何内容保留在 RAM 中。

- Kafka 有一个将消息分组在一起的协议。它允许网络请求将消息分组在一起以减少网络开销。服务器一气呵成的将消息的数据块持久化并立即获取较大的线性块。
- 线性读取/写入磁盘速度很快。现代磁盘速度较慢的概念是由于大量的磁盘搜索，这在大型线性操作中不是问题。
- 所说的线性操作由操作系统通过预读（预取大块数倍）和后写（将小的逻辑写入大物理写入）技术进行了大量优化。
- 现代操作系统将磁盘缓存在可用 RAM 中。这被称为 pagecache。
- 由于 Kafka 在整个流程（生产者 -> 经纪 -> 消费者）中以标准化的二进制格式存储未修改的消息，所以它可以利用零拷贝优化。这就是操作系统将数据从页面缓存直接复制到套接字时，完全绕过了 Kafka 经纪人应用程序。

所有这些优化都允许 Kafka 以接近网络速度传递消息。

## 复制

分区数据在多个代理中复制，以便在一个代理死亡的情况下保存数据。

在任何时候，一个代理“拥有”一个分区，并且是应用程序通过该分区读写数据的节点。这被称为分区领导。它将它收到的数据复制到 N 个其他代理（称为追随者）。他们也存储数据，并准备在领导者节点死亡的情况下取代领导者。这就是典型的一主多从模式。

<div align="center">
<img src="https://raw.githubusercontent.com/dunwu/java-web/master/images/distributed/mq/kafka/kafka-replication.png" width="640"/>
</div>

生产者/消费者如何知道分区的领导者是谁？

对于生产者/消费者来说，从一个分区写入/读取，他们需要知道它的领导者，对吧？这些信息需要从某处获得。Kafka 将这种元数据存储在一个名为 Zookeeper 的服务中。

生产者和消费者都和 Zookeeper 连接并通信。Kafka 一直在摆脱这种耦合，自 0.8 和 0.9 版分别开始，客户端直接从 Kafka 经纪人那里获取元数据信息，他们自己与 Zookeeper 交谈。

<div align="center">
<img src="https://raw.githubusercontent.com/dunwu/java-web/master/images/distributed/mq/kafka/kafka-metadata-flow.png" width="640"/>
</div>

## 流处理

在 Kafka 中，流处理器是任何需要从输入主题中持续输入数据流，对该输入执行一些处理并生成输出主题的数据流（或外部服务，数据库，垃圾桶，无论哪里真的......）

可以直接使用生产者/消费者 API 进行简单处理，但对于更复杂的转换（如将流连接在一起），Kafka 提供了一个集成的 Streams API 库。

此 API 旨在用于您自己的代码库中，它不在代理上运行。它与消费者 API 类似，可帮助您扩展多个应用程序的流处理工作（类似于消费者群体）。

<div align="center">
<img src="https://raw.githubusercontent.com/dunwu/java-web/master/images/distributed/mq/kafka/kafka-stream-processor.png" width="640"/>
</div>

### 无状态处理

流的无状态处理是确定性处理，不依赖于任何外部。你知道，对于任何给定的数据，你将总是产生独立于其他任何东西的相同输出。

一个流可以被解释为一个表，一个表可以被解释为一个流。

流可以被解释为数据的一系列更新，其中聚合是表的最终结果。

如果您看看如何实现同步数据库复制，您会发现它是通过所谓的流式复制，其中表中的每个更改都发送到副本服务器。

Kafka 流可以用同样的方式解释 - 当从最终状态积累时的事件。这样的流聚合被保存在本地的 RocksDB 中（默认情况下），被称为 KTable。

<div align="center">
<img src="https://raw.githubusercontent.com/dunwu/java-web/master/images/distributed/mq/kafka/kafka-ktable.png" width="640"/>
</div>

可以将表格视为流中每个键的最新值的快照。以同样的方式，流记录可以产生一个表，表更新可以产生一个更新日志流。

<div align="center">
<img src="https://raw.githubusercontent.com/dunwu/java-web/master/images/distributed/mq/kafka/kafka-table-as-stream.png" width="640"/>
</div>

### 有状态处理

一些简单的操作，如 map() 或 filter() 是无状态的，并且不要求您保留有关处理的任何数据。但是，在现实生活中，你要做的大多数操作都是有状态的（例如 count()），因此需要存储当前的累积状态。

维护流处理器上的状态的问题是流处理器可能会失败！你需要在哪里保持这个状态才能容错？

一种天真的做法是简单地将所有状态存储在远程数据库中，并通过网络连接到该存储。问题在于没有数据的地方和大量的网络往返，这两者都会显著减慢你的应用程序。一个更微妙但重要的问题是，您的流处理作业的正常运行时间将与远程数据库紧密耦合，并且作业不会自成体系（数据库中来自另一个团队的更改可能会破坏您的处理过程）。

那么更好的方法是什么？

回想一下表和流的双重性。这使我们能够将数据流转换为与我们的处理共处一地的表格。它还为我们提供了处理容错的机制 - 通过将流存储在 Kafka 代理中。

流处理器可以将其状态保存在本地表（例如 RocksDB）中，该表将从输入流更新（可能是某种任意转换之后）。当进程失败时，它可以通过重放流来恢复其数据。

您甚至可以让远程数据库成为流的生产者，从而有效地广播更新日志，以便在本地重建表。

<div align="center">
<img src="https://raw.githubusercontent.com/dunwu/java-web/master/images/distributed/mq/kafka/kafka-stateful-process.png" width="640"/>
</div>

## Kafka 应用场景

- 构建实时的流数据管道，在系统或应用间获取可靠数据。
- 构建实时的流应用程序，用于转换或响应数据流。

正如我们已经介绍的那样，Kafka 允许您将大量消息通过集中介质存储并存储，而不用担心性能或数据丢失等问题。

这意味着它非常适合用作系统架构的核心，充当连接不同应用程序的集中介质。 Kafka 可以成为事件驱动架构的核心部分，并允许您真正将应用程序彼此分离。

<div align="center">
<img src="https://raw.githubusercontent.com/dunwu/java-web/master/images/distributed/mq/kafka/kafka-event-system.png" width="640"/>
</div>

Kafka 允许您轻松分离不同（微）服务之间的通信。利用 Streams API，现在比以往更容易编写业务逻辑，丰富了 Kafka 主题数据以便服务消费。

## 资料

### 官方资料

[Github](https://github.com/apache/kafka) | [官方文档](https://kafka.apache.org/documentation/)

### 第三方资料

- [Kafka Manager](https://github.com/yahoo/kafka-manager) - Kafka 管理工具
- [Kafka 剖析（一）：Kafka 背景及架构介绍](http://www.infoq.com/cn/articles/kafka-analysis-part-1)
- [Thorough Introduction to Apache Kafka](https://hackernoon.com/thorough-introduction-to-apache-kafka-6fbf2989bbc1)
