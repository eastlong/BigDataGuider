并发编程：java原子类
<!-- TOC -->

- [导读](#导读)
- [并发编程：原子类](#并发编程原子类)
    - [线程不安全的高并发实现](#线程不安全的高并发实现)
    - [线程安全的高并发实现 AtomicInteger](#线程安全的高并发实现-atomicinteger)
    - [AtomicInteger 保证原子性](#atomicinteger-保证原子性)
    - [CAS](#cas)
    - [AtomicInteger 实现](#atomicinteger-实现)
    - [Atomic 延伸其它类](#atomic-延伸其它类)
        - [原子更新基本类型](#原子更新基本类型)
        - [原子更新数组](#原子更新数组)

<!-- /TOC -->
# 导读


# 并发编程：原子类
## 线程不安全的高并发实现
【需求】
客户端模拟执行 5000 个任务，线程数量是 200，每个线程执行一次，就将 count 计数加 1，当执行完以后，打印 count 的值。 
```java
package com.betop.multithread.atomic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class NotThreadSafeConcurrency {

    private static int CLIENT_COUNT = 5000;
    private static int THREAD_COUNT = 200;
    private static int count = 0;
    private static int[] values = new int[11];

    private static ExecutorService executorService = Executors.newCachedThreadPool();
    private final static Semaphore semaphore = new Semaphore(THREAD_COUNT);
    private final static CountDownLatch countDownLatch = new CountDownLatch(CLIENT_COUNT);

    public static void main(String[] args) throws Exception {
        testAtomicInt();
    }

    private static void testAtomicInt() throws Exception {
        for (int i = 0; i < CLIENT_COUNT; i++) {
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    add();
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // count每加 1，进行减 1 计数
                countDownLatch.countDown();
            });
        }
        // 等待线程池所有任务执行结束
        countDownLatch.await();
        executorService.shutdown();
        System.out.println("ConcurrencyDemo:" + count);
    }

    private static void add() {
        count++;
    }
}
```

【程序运行结果】
```sh
ConcurrencyDemo:4949
ConcurrencyDemo:4926
ConcurrencyDemo:4957
```

## 线程安全的高并发实现 AtomicInteger
```java
package com.betop.multithread.atomic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;


public class ThreadSafeConcurrency {

    private static int CLIENT_COUNT = 5000;
    private static int THREAD_COUNT = 200;
    // 区别在这里
    private static AtomicInteger count = new AtomicInteger(0);

    private static ExecutorService executorService = Executors.newCachedThreadPool();
    private final static Semaphore semaphore = new Semaphore(THREAD_COUNT);
    private final static CountDownLatch countDownLatch = new CountDownLatch(CLIENT_COUNT);

    public static void main(String[] args) throws Exception {
        testAtomicInteger();
    }

    private static void testAtomicInteger() throws Exception {
        for (int i = 0; i < CLIENT_COUNT; i++) {
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    add();
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // count每加 1，进行减 1 计数
                countDownLatch.countDown();
            });
        }
        // 等待线程池所有任务执行结束
        countDownLatch.await();
        executorService.shutdown();
        System.out.println("ConcurrencyDemo:" + count);
    }

    private static void add() {
        count.incrementAndGet();
    }
}
```

【程序结果】
```
ConcurrencyDemo:5000
ConcurrencyDemo:5000
ConcurrencyDemo:5000
```
## AtomicInteger 保证原子性
在 JDK1.5 中新增 java.util.concurrent(J.U.C) 包，它建立在 CAS 之上。CAS 是**非阻塞算法**的一种常见实现，相对于 **synchronized 这种阻塞算法**，它的性能更好。

## CAS
CAS 就是 **Compare and Swap** 的意思，比较并操作。很多的 CPU 直接支持 CAS 指令。CAS 是一项乐观锁技术，当多个线程尝试使用 CAS 同时更新同一个变量时，只有其中一个线程能更新变量的值，而其它线程都失败，失败的线程并不会被挂起，而是被告知这次竞争中失败，并可以再次尝试。

CAS 操作包含三个操作数 —— 内存位置（V）、预期原值（A）和新值（B）。当且仅当预期值 A 和内存值 V 相同时，将内存值 V 修改为 B，否则什么都不做。

JDK1.5 中引入了底层的支持，在 int、long 和对象的引用等类型上都公开了 CAS 的操作，并且 JVM 把它们编译为底层硬件提供的最有效的方法，在运行 CAS 的平台上，运行时把它们编译为相应的机器指令。在 java.util.concurrent.atomic 包下面的所有的原子变量类型中，比如 AtomicInteger，都使用了这些底层的JVM支持为数字类型的引用类型提供一种高效的 CAS 操作。

在 CAS 操作中，会出现 ABA 问题。就是如果 V 的值先由 A 变成 B，再由 B 变成 A，那么仍然认为是发生了变化，并需要重新执行算法中的步骤。

有简单的解决方案：不是更新某个引用的值，而是更新两个值，包括一个引用和一个版本号（比如时间戳），即使这个值由 A 变为 B，然后 B 变为 A，版本号也是不同的。

AtomicStampedReference 和 AtomicMarkableReference 支持在两个变量上执行原子的条件更新。AtomicStampedReference 更新一个 “对象-引用” 二元组，通过在引用上加上 “版本号”，从而避免 ABA 问题，AtomicMarkableReference 将更新一个“对象引用-布尔值”的二元组。

## AtomicInteger 实现
AtomicInteger 是一个支持原子操作的 Integer 类，就是**保证对 AtomicInteger 类型变量的增加和减少操作是原子性的，不会出现多个线程下的数据不一致问题。**

```java
package java.util.concurrent.atomic;

public class AtomicInteger extends Number implements java.io.Serializable {
    private static final long serialVersionUID = 6214790243416807050L;
    // setup to use Unsafe.compareAndSwapInt for updates
    private static final Unsafe unsafe = Unsafe.getUnsafe();

    private volatile int value;

    public AtomicInteger(int initialValue) {
        value = initialValue;
    }
    public AtomicInteger() {
    }

    public final int get() {
        return value;
    }

    //compareAndSet()方法调用的 compareAndSwapInt() 方法是一个 native 方法。compareAndSet 传入的为执行方法时获取到的 value 属性值，update 为加 1 后的值， compareAndSet 所做的为调用 Sun 的 UnSafe 的 compareAndSwapInt 方法来完成，此方法为 native 方法。
    //compareAndSwapInt 基于的是 CPU 的 CAS 指令来实现的。所以基于 CAS 的操作可认为是无阻塞的，一个线程的失败或挂起不会引起其它线程也失败或挂起。并且由于 CAS 操作是 CPU 原语，所以性能比较好。
    public final boolean compareAndSet(int expect, int update) {
        return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
    }

    //先获取到当前的 value 属性值，然后将 value 加 1，赋值给一个局部的 next 变量，然而，这两步都是非线程安全的，但是内部有一个死循环，不断去做 compareAndSet 操作，直到成功为止，也就是修改的根本在 compareAndSet 方法里面。
    public final int getAndIncrement() {
        for (;;) {
            int current = get();
            int next = current + 1;
            if (compareAndSet(current, next))
                return current;
        }
    }

    public final int getAndDecrement() {
        for (;;) {
            int current = get();
            int next = current - 1;
            if (compareAndSet(current, next))
                return current;
        }
    }
}
```

AtomicInteger 中还有 IncrementAndGet() 和 DecrementAndGet() 方法，他们的实现原理和上面的两个方法完全相同，区别是返回值不同，getAndIncrement() 和 getAndDecrement() 两个方法返回的是改变之前的值，即current。IncrementAndGet() 和 DecrementAndGet() 两个方法返回的是改变之后的值，即 next。

## Atomic 延伸其它类
### 原子更新基本类型
使用原子的方式更新基本类型，Atomic 包提供了以下 3 个类。

* AtomicBoolean：原子更新布尔类型。
* AtomicInteger：原子更新整型。
* AtomicLong：原子更新长整型。

### 原子更新数组
通过原子的方式更新数组里的某个元素，Atomic 包提供了以下 3 个类。
* AtomicIntegerArray：原子更新整型数组里的元素。
* AtomicLongArray：原子更新长整型数组里的元素。
* AtomicReferenceArray：原子更新引用类型数组里的元素。















