# 传输控制协议 TCP

## 简介

<div align="center"><img src="https://gitee.com/turnon/images/raw/master/snap/1559263786555.png"/></div>

TCP 是通过 [IP 网络](https://en.wikipedia.org/wiki/Internet_Protocol)的面向连接的协议。 使用[握手](https://en.wikipedia.org/wiki/Handshaking)建立和断开连接。 发送的所有数据包保证以原始顺序到达目的地，用以下措施保证数据包不被损坏：

- 每个数据包的序列号和[校验码](https://en.wikipedia.org/wiki/Transmission_Control_Protocol#Checksum_computation)。
- [确认包](<https://en.wikipedia.org/wiki/Acknowledgement_(data_networks)>)和自动重传

如果发送者没有收到正确的响应，它将重新发送数据包。如果多次超时，连接就会断开。TCP 实行[流量控制](<https://en.wikipedia.org/wiki/Flow_control_(data)>)和[拥塞控制](https://en.wikipedia.org/wiki/Network_congestion#Congestion_control)。这些确保措施会导致延迟，而且通常导致传输效率比 UDP 低。

为了确保高吞吐量，Web 服务器可以保持大量的 TCP 连接，从而导致高内存使用。在 Web 服务器线程间拥有大量开放连接可能开销巨大，消耗资源过多，也就是说，一个 [memcached](https://github.com/donnemartin/system-design-primer/blob/master/README-zh-Hans.md#memcached) 服务器。[连接池](https://en.wikipedia.org/wiki/Connection_pool) 可以帮助除了在适用的情况下切换到 UDP。

TCP 对于需要高可靠性但时间紧迫的应用程序很有用。比如包括 Web 服务器，数据库信息，SMTP，FTP 和 SSH。

以下情况使用 TCP 代替 UDP：

- 你需要数据完好无损。
- 你想对网络吞吐量自动进行最佳评估。

## TCP 报文

<div align="center"><img src="https://gitee.com/turnon/images/raw/master/snap/1559264511812.png"/></div>

- TCP 的包是没有 IP 地址的，那是 IP 层上的事。但是有源端口和目标端口。
- 一个 TCP 连接需要四个元组来表示是同一个连接（src_ip, src_port, dst_ip, dst_port）准确说是五元组，还有一个是协议。但因为这里只是说 TCP 协议，所以，这里我只说四元组。
- 注意上图中的四个非常重要的东西：
  - **Sequence Number**是包的序号，**用来解决网络包乱序（reordering）问题。**
  - **Acknowledgement Number**就是 ACK——用于确认收到，**用来解决不丢包的问题**。
  - **Window 又叫 Advertised-Window**，也就是著名的滑动窗口（Sliding Window），**用于解决流控的**。
  - **TCP Flag** ，也就是包的类型，**主要是用于操控 TCP 的状态机的**。

<div align="center"><img src="https://gitee.com/turnon/images/raw/master/snap/1559264593860.png"/></div>

## TCP 通信流程

<div align="center"><img src="https://gitee.com/turnon/images/raw/master/snap/1559264679371.png"/></div>

### 三次握手

1. 第一次握手 - client 发送一个 **SYN(J)** 包给 server，然后等待 server 的 ACK 回复，进入`SYN-SENT`状态。p.s: SYN 为 synchronize 的缩写，ACK 为 acknowledgment 的缩写。
2. 第二次握手 - server 接收到 SYN(seq=J)包后就返回一个 **ACK(J+1)** 包以及一个自己的 **SYN(K)** 包，然后等待 client 的 ACK 回复，server 进入 `SYN-RECIVED` 状态。
3. 第三次握手 - client 接收到 server 发回的 ACK(J+1)包后，进入 `ESTABLISHED` 状态。然后根据 server 发来的 SYN(K)包，返回给等待中的 server 一个 **ACK(K+1)** 包。等待中的 server 收到 ACK 回复，也把自己的状态设置为 `ESTABLISHED`。到此 TCP 三次握手完成，client 与 server 可以正常进行通信了。

> 🤔 **思考**
>
> 为什么要进行三次握手？
>
> 我们来看一下为什么需要进行三次握手，两次握手难道不行么？这里我们用一个生活中的具体例子来解释就很好理解了。我们可以将三次握手中的客户端和服务器之间的握手过程比喻成 A 和 B 通信的过程：
>
> - 在第一次通信过程中，A 向 B 发送信息之后，B 收到信息后可以确认自己的收信能力和 A 的发信能力没有问题。
> - 在第二次通信中，B 向 A 发送信息之后，A 可以确认自己的发信能力和 B 的收信能力没有问题，**但是 B 不知道自己的发信能力到底如何**，所以就需要第三次通信。
> - 在第三次通信中，A 向 B 发送信息之后，B 就可以确认自己的发信能力没有问题。

### 四次挥手

1. 第一次挥手 - client 发送一个 **FIN(M)** 包，此时 client 进入 `FIN-WAIT-1` 状态，这表明 client 已经没有数据要发送了。
2. 第二次挥手 - server 收到了 client 发来的 FIN(M)包后，向 client 发回一个 **ACK(M+1)** 包，此时 server 进入 `CLOSE-WAIT` 状态，client 进入 `FIN-WAIT-2` 状态。
3. 第三次挥手 - server 向 client 发送 **FIN(N)** 包，请求关闭连接，同时 server 进入 `LAST-ACK` 状态。
4. 第四次挥手 - client 收到 server 发送的 **FIN(N)** 包，进入 `TIME-WAIT` 状态。向 server 发送 **ACK(N+1)** 包，server 收到 client 的 **ACK(N+1)** 包以后，进入 `CLOSE` 状态；client 等待一段时间还没有得到回复后判断 server 已正式关闭，进入 `CLOSE` 状态。

## TCP 滑动窗口

**TCP 必需要解决的可靠传输以及包乱序（reordering）的问题**，所以，TCP 必需要知道网络实际的数据处理带宽或是数据处理速度，这样才不会引起网络拥塞，导致丢包。

所以，TCP 引入了一些技术和设计来做网络流控，Sliding Window 是其中一个技术。 前面我们说过，**TCP 头里有一个字段叫 Window，又叫 Advertised-Window，这个字段是接收端告诉发送端自己还有多少缓冲区可以接收数据**。**于是发送端就可以根据这个接收端的处理能力来发送数据，而不会导致接收端处理不过来**。 为了说明滑动窗口，我们需要先看一下 TCP 缓冲区的一些数据结构

<div align="center"><img src="https://gitee.com/turnon/images/raw/master/snap/1559265819762.png"/></div>

- \#1 已收到 ack 确认的数据。
- \#2 发还没收到 ack 的。
- \#3 在窗口中还没有发出的（接收方还有空间）。
- \#4 窗口以外的数据（接收方没空间）

下面是个滑动后的示意图（收到 36 的 ack，并发出了 46-51 的字节）：

<div align="center"><img src="https://gitee.com/turnon/images/raw/master/snap/1559265927658.png"/></div>

## TCP 重传机制

TCP 要保证所有的数据包都可以到达，所以，必需要有重传机制。

### 超时重传机制

一种是不回 ack，死等 3，当发送方发现收不到 3 的 ack 超时后，会重传 3。一旦接收方收到 3 后，会 ack 回 4——意味着 3 和 4 都收到了。

但是，这种方式会有比较严重的问题，那就是因为要死等 3，所以会导致 4 和 5 即便已经收到了，而发送方也完全不知道发生了什么事，因为没有收到 Ack，所以，发送方可能会悲观地认为也丢了，所以有可能也会导致 4 和 5 的重传。

对此有两种选择：

1. 一种是仅重传 timeout 的包。也就是第 3 份数据。
2. 另一种是重传 timeout 后所有的数据，也就是第 3，4，5 这三份数据。

这两种方式有好也有不好。第一种会节省带宽，但是慢，第二种会快一点，但是会浪费带宽，也可能会有无用功。但总体来说都不好。因为都在等 timeout，timeout 可能会很长。

### 快速重传机制

于是，TCP 引入了一种叫 Fast Retransmit 的算法，不以时间驱动，而以数据驱动重传。也就是说，如果，包没有连续到达，就 ack 最后那个可能被丢了的包，如果发送方连续收到 3 次相同的 ack，就重传。Fast Retransmit 的好处是不用等 timeout 了再重传。

比如：如果发送方发出了 1，2，3，4，5 份数据，第一份先到送了，于是就 ack 回 2，结果 2 因为某些原因没收到，3 到达了，于是还是 ack 回 2，后面的 4 和 5 都到了，但是还是 ack 回 2，因为 2 还是没有收到，于是发送端收到了三个 ack=2 的确认，知道了 2 还没有到，于是就马上重转 2。然后，接收端收到了 2，此时因为 3，4，5 都收到了，于是 ack 回 6。

Fast Retransmit 只解决了一个问题，就是 timeout 的问题，它依然面临一个艰难的选择，就是，是重传之前的一个还是重传所有的问题。对于上面的示例来说，是重传#2 呢还是重传#2，#3，#4，#5 呢？因为发送端并不清楚这连续的 3 个 ack(2)是谁传回来的？也许发送端发了 20 份数据，是#6，#10，#20 传来的呢。这样，发送端很有可能要重传从 2 到 20 的这堆数据（这就是某些 TCP 的实际的实现）。可见，这是一把双刃剑。

## 参考资料

- [TCP 的那些事儿（上）](https://coolshell.cn/articles/11564.html)
- [TCP 的那些事儿（下）](https://coolshell.cn/articles/11609.html)
- [图解 TCP 三次握手与四次分手](https://juejin.im/post/5a7835a46fb9a063606eb801)
