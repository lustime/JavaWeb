---
title: Kafka 基础篇
date: 2018/07/12
categories:
- 分布式
tags:
- java
- javaweb
- 分布式
- mq
---

# Kafka 基础篇

> Kafka 是一个分布式的、可水平扩展的、基于发布/订阅模式的、支持容错的消息系统。

<!-- TOC depthFrom:2 depthTo:3 -->

- [1. 概述](#1-概述)
- [2. 安装配置](#2-安装配置)
    - [2.1. 下载解压](#21-下载解压)
    - [2.2. 启动服务器](#22-启动服务器)
    - [2.3. 停止服务器](#23-停止服务器)
    - [2.4. 创建 Topic](#24-创建-topic)
    - [2.5. 生产者生产消息](#25-生产者生产消息)
    - [2.6. 消费者消费消息](#26-消费者消费消息)
    - [2.7. 配置多 broker 集群](#27-配置多-broker-集群)
- [3. API](#3-api)
    - [3.1. Producer](#31-producer)
    - [3.2. Consumer](#32-consumer)
    - [3.3. Streams](#33-streams)
- [4. 资源](#4-资源)

<!-- /TOC -->

## 1. 概述

Kafka 是一个分布式的、可水平扩展的、基于发布/订阅模式的、支持容错的消息系统。

## 2. 安装配置

环境要求：JDK8、ZooKeeper

### 2.1. 下载解压

进入官方下载地址：http://kafka.apache.org/downloads，选择合适版本。

解压到本地：

```
> tar -xzf kafka_2.11-1.1.0.tgz
> cd kafka_2.11-1.1.0
```

现在您已经在您的机器上下载了最新版本的 Kafka。

### 2.2. 启动服务器

由于 Kafka 依赖于 ZooKeeper，所以运行前需要先启动 ZooKeeper

```
> bin/zookeeper-server-start.sh config/zookeeper.properties
[2013-04-22 15:01:37,495] INFO Reading configuration from: config/zookeeper.properties (org.apache.zookeeper.server.quorum.QuorumPeerConfig)
...
```

然后，启动 Kafka

```
> bin/kafka-server-start.sh config/server.properties
[2013-04-22 15:01:47,028] INFO Verifying properties (kafka.utils.VerifiableProperties)
[2013-04-22 15:01:47,051] INFO Property socket.send.buffer.bytes is overridden to 1048576 (kafka.utils.VerifiableProperties)
...
```

### 2.3. 停止服务器

执行所有操作后，可以使用以下命令停止服务器

```
$ bin/kafka-server-stop.sh config/server.properties
```

### 2.4. 创建 Topic

创建一个名为 test 的 Topic，这个 Topic 只有一个分区以及一个备份：

```
> bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
```

### 2.5. 生产者生产消息

运行生产者，然后可以在控制台中输入一些消息，这些消息会发送到服务器：

```
> bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test
This is a message
This is another message
```

### 2.6. 消费者消费消息

启动消费者，然后获得服务器中 Topic 下的消息：

```
> bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning
This is a message
This is another message
```

### 2.7. 配置多 broker 集群

复制配置为多份（Windows 使用 copy 命令代理）：

```
> cp config/server.properties config/server-1.properties
> cp config/server.properties config/server-2.properties
```

修改配置：

```
config/server-1.properties:
    broker.id=1
    listeners=PLAINTEXT://:9093
    log.dir=/tmp/kafka-logs-1

config/server-2.properties:
    broker.id=2
    listeners=PLAINTEXT://:9094
    log.dir=/tmp/kafka-logs-2
```

其中，broker.id 这个参数必须是唯一的。

端口故意配置的不一致，是为了可以在一台机器启动多个应用节点。

根据这两份配置启动三个服务器节点：

```
> bin/kafka-server-start.sh config/server.properties &
...
> bin/kafka-server-start.sh config/server-1.properties &
...
> bin/kafka-server-start.sh config/server-2.properties &
...
```

创建一个新的 Topic 使用 三个备份：

```
> bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 3 --partitions 1 --topic my-replicated-topic
```

查看主题：

```
> bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic my-replicated-topic
Topic:my-replicated-topic   PartitionCount:1    ReplicationFactor:3 Configs:
    Topic: my-replicated-topic  Partition: 0    Leader: 1   Replicas: 1,2,0 Isr: 1,2,0
```

- leader - 负责指定分区的所有读取和写入的节点。每个节点将成为随机选择的分区部分的领导者。
- replicas - 是复制此分区日志的节点列表，无论它们是否为领导者，或者即使它们当前处于活动状态。
- isr - 是“同步”复制品的集合。这是副本列表的子集，该列表当前处于活跃状态并且已经被领导者捕获。

## 3. API

Stream API 的 maven 依赖：

```xml
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-streams</artifactId>
    <version>1.1.0</version>
</dependency>
```

其他 API 的 maven 依赖：

```xml
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>1.1.0</version>
</dependency>
```

### 3.1. Producer

Producer 是 Kafka 中的生产者，负责生产消息。

示例：

```java
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * Kafka 生产者生产消息示例 生产者配置参考：https://kafka.apache.org/documentation/#producerconfigs
 */
public class ProducerDemo {
    private static final String HOST = "localhost:9092";

    public static void main(String[] args) {
        // 1. 指定生产者的配置
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, HOST);
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, 0);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            "org.apache.kafka.common.serialization.StringSerializer");

        // 2. 使用配置初始化 Kafka 生产者
        Producer<String, String> producer = new KafkaProducer<>(properties);

        try {
            // 3. 使用 send 方法发送异步消息
            for (int i = 0; i < 100; i++) {
                String msg = "Message " + i;
                producer.send(new ProducerRecord<>("HelloWorld", msg));
                System.out.println("Sent:" + msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 4. 关闭生产者
            producer.close();
        }
    }
}
```

### 3.2. Consumer

Consumer 是 Kafka 中的消费者，可以订阅 Topic ，当 Topic 下有消息，就可以消费。

示例：

```java
import java.util.Arrays;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

/**
 * Kafka 消费者消费消息示例 消费者配置参考：https://kafka.apache.org/documentation/#consumerconfigs
 */
public class ConsumerAOC {
    private static final String HOST = "localhost:9092";

    public static void main(String[] args) {
        // 1. 指定消费者的配置
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, HOST);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            "org.apache.kafka.common.serialization.StringDeserializer");

        // 2. 使用配置初始化 Kafka 消费者
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        // 3. 消费者订阅 Topic
        consumer.subscribe(Arrays.asList("t1"));
        while (true) {
            // 4. 消费消息
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("offset = %d, key = %s, value = %s%n",
                    record.offset(), record.key(), record.value());
            }
        }
    }
}
```

### 3.3. Streams

示例：

```java
import java.util.Arrays;
import java.util.Properties;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;

/**
 * Kafka 流示例 消费者配置参考：https://kafka.apache.org/documentation/#streamsconfigs
 */
public class StreamDemo {
    private static final String HOST = "localhost:9092";

    public static void main(String[] args) {
        // 1. 指定流的配置
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, HOST);
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // 设置流构造器
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> textLines = builder.stream("TextLinesTopic");
        KTable<String, Long> wordCounts = textLines
            .flatMapValues(textLine -> Arrays.asList(textLine.toLowerCase().split("\\W+")))
            .groupBy((key, word) -> word)
            .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("counts-store"));
        wordCounts.toStream().to("WordsWithCountsTopic", Produced.with(Serdes.String(), Serdes.Long()));

        // 根据流构造器和流配置初始化 Kafka 流
        KafkaStreams streams = new KafkaStreams(builder.build(), config);
        streams.start();
    }
}
```

## 4. 资源

| [官网](http://kafka.apache.org/) | [官方文档](https://kafka.apache.org/documentation/) | [Github](https://github.com/apache/kafka) |
