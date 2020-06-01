高性能编程之并发编程：AQS 原理以及 AQS 同步组件总结
<!-- TOC -->

- [1. AQS简单介绍](#1-aqs简单介绍)

<!-- /TOC -->
常见问题：AQS 原理？;CountDownLatch 和 CyclicBarrier 了解吗,两者的区别是什么？用过 Semaphore 吗？
# 1. AQS简单介绍
AQS 的全称为（AbstractQueuedSynchronizer），这个类在 java.util.concurrent.locks 包下面。

![AQS类](imgs/1/24.png)

AQS 是一个用来构建锁和同步器的框架，使用 AQS 能简单且高效地构造出应用广泛的大量的同步器，比如我们提到的 ReentrantLock，Semaphore，其他的诸如 ReentrantReadWriteLock，SynchronousQueue，FutureTask 等等皆是基于 AQS 的。当然，我们自己也能利用 AQS 非常轻松容易地构造出符合我们自己需求的同步器。



