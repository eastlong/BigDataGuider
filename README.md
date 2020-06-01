# BigDataGuider
大数据学习笔记: 
* 计算机基础知识：数据结构与算法、SQL优化
* java核心技术：涉及java编程基础、java并发编程、jVM、设计模式  
* 大数据框架：spark、flink、hadoop、elasticsearch、zookeeper、kafka  
* 实战：畅购商城、乐优商城
<div align="center"> <img width="300px" src="pictures/homepage/BigdataGuider.PNG"/> </div>
<br/>

## Java进阶Guider
<div align="center">
  <table align="center">
      <tr>
        <th><img width="50px" src="pictures/homepage/leetcode.jpg"></th>
        <th><img width="50px" src="pictures/homepage/java.jpg"></th>
        <th><img width="50px" src="pictures/homepage/springboot-icon.jpg"></th>
        <th><img width="50px" src="pictures/homepage/springcloud-icon.jpg"></th>
        <th><img width="50px" src="pictures/homepage/redis-icon.jpg"></th>
        <th><img width="50px" src="pictures/homepage/sql-logo.jpg"></th>
      </tr>
      <tr>
        <td align="center"><a href="#leetcode">Leetcode</a></td>
        <td align="center"><a href="#java并发编程">并发编程</a></td>
        <td align="center"><a href="#springboot">springboot</a></td>
        <td align="center"><a href="#springcloud">springcloud</a></td>
        <td align="center"><a href="#redis">redis</a></td>
        <td align="center"><a href="#sql">SQL</a></td>
      </tr>
  </table>
</div>
<br/>

## 大数据框架Guider
<table>
    <tr>
      <th><img width="50px" src="pictures/homepage/hadoop.jpg"></th>
      <th><img width="50px" src="pictures/homepage/hive.jpg"></th>
      <th><img width="50px" src="pictures/homepage/spark.jpg"></th>
      <th><img width="50px" src="pictures/homepage/flink.png"></th>
      <th><img width="50px" src="pictures/homepage/hbase.png"></th>
      <th><img width="50px" src="pictures/homepage/kafka.png"></th>
      <th><img width="50px" src="pictures/homepage/zookeeper.jpg"></th>
      <!-- <th><img width="50px" src="pictures/homepage/flume.png"></th>
      <th><img width="50px" src="pictures/homepage/sqoop.png"></th> -->
    </tr>
    <tr>
      <td align="center"><a href="#hadoop">Hadoop</a></td>
      <td align="center"><a href="#hive">Hive</a></td>
      <td align="center"><a href="#spark">Spark</a></td>
      <td align="center"><a href="#flink">Flink</a></td>
      <td align="center"><a href="#hbase">HBase</a></td>
      <td align="center"><a href="#kafka">Kafka</a></td>
      <td align="center"><a href="#zookeeper">Zookeeper</a></td>
      <!-- <td align="center"><a href="#flume">Flume</a></td>
      <td align="center"><a href="#sqoop">Sqoop</a></td> -->
    </tr>
  </table>
<br/>

## LeetCode
1.线性规划
  * [斐波那契数列](java/leetcode/线性规划/斐波那契数列.md)
  * [矩阵路径](java/leetcode/线性规划/矩阵路径.md)
  * [LeetCode热题100题](java/leetcode/leetcode热题100题.md)

## 大数据框架
### **kafka**
  1. [Kafka概述](bigdata/kafka/1.Kafka概述.md)  
  2. [Kafka安装与配置.md](bigdata/kafka/2.Kafka安装与配置.md)   
  3. [3.工作流程及文件存储机制.md](bigdata/kafka/3.工作流程及文件存储机制.md) 
  4. [4.kafka生产者.md](bigdata/kafka/4.kafka生产者.md)   
  5. [Kafka简介](bigdata/kafka/Kafka简介.md)  
  6. [Kafka生产者详解.md](bigdata/kafka/Kafka生产者详解.md)  
  7. [Kafka消费者详解.md](bigdata/kafka/Kafka消费者详解.md)  
  8. [Kafka深入理解分区副本机制.md](bigdata/kafka/Kafka深入理解分区副本机制.md)  

### 


### Spark

**Spark Core :**

1. [Spark 简介](bigdata/spark/Spark简介.md)
2. [Spark 开发环境搭建](bigdata/spark/Spark开发环境搭建.md)
4. [弹性式数据集 RDD](bigdata/spark/Spark_RDD.md)
5. [RDD 常用算子详解](bigdata/spark/Spark_Transformation和Action算子.md)
5. [Spark 运行模式与作业提交](bigdata/spark/Spark部署模式与作业提交.md)
6. [Spark 累加器与广播变量](bigdata/spark/Spark累加器与广播变量.md)
7. [基于 Zookeeper 搭建 Spark 高可用集群](bigdata/spark/installation/Spark集群环境搭建.md)

**Spark SQL :**

1. [DateFrame 和 DataSet ](bigdata/spark/SparkSQL_Dataset和DataFrame简介.md)
2. [Structured API 的基本使用](bigdata/spark/Spark_Structured_API的基本使用.md)
3. [Spark SQL 外部数据源](bigdata/spark/SparkSQL外部数据源.md)
4. [Spark SQL 常用聚合函数](bigdata/spark/SparkSQL常用聚合函数.md)
5. [Spark SQL JOIN 操作](bigdata/spark/SparkSQL联结操作.md)

**Spark Streaming ：**

1. [Spark Streaming 简介](bigdata/spark/Spark_Streaming与流处理.md)
2. [Spark Streaming 基本操作](bigdata/spark/Spark_Streaming基本操作.md)
3. [Spark Streaming 整合 Flume](bigdata/spark/Spark_Streaming整合Flume.md)
4. [Spark Streaming 整合 Kafka](bigdata/spark/Spark_Streaming整合Kafka.md)


## Flink

1. [Flink 核心概念综述](bigdata/flink/Flink核心概念综述.md)
2. [Flink 开发环境搭建](bigdata/flink/Flink开发环境搭建.md)
3. [Flink Data Source](bigdata/flink/Flink_Data_Source.md)
4. [Flink Data Transformation](bigdata/flink/Flink_Data_Transformation.md)
4. [Flink Data Sink](bigdata/flink/Flink_Data_Sink.md)
6. [Flink 窗口模型](bigdata/flink/Flink_Windows.md)
7. [Flink 状态管理与检查点机制](bigdata/flink/Flink状态管理与检查点机制.md)
8. [Flink Standalone 集群部署](bigdata/flink/installation/Flink_Standalone_Cluster.md)

#### Flink当前最火的实时计算引擎-入门篇
   * [Flink从入门到放弃(入门篇1)-Flink是什么](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/Flink/Flink%E4%BB%8E%E5%85%A5%E9%97%A8%E5%88%B0%E6%94%BE%E5%BC%83(%E5%85%A5%E9%97%A8%E7%AF%871)-Flink%E6%98%AF%E4%BB%80%E4%B9%88%EF%BC%9F.md)
   * [Flink从入门到放弃(入门篇2)-本地环境搭建&构建第一个Flink应用](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/Flink/Flink%E4%BB%8E%E5%85%A5%E9%97%A8%E5%88%B0%E6%94%BE%E5%BC%83(%E5%85%A5%E9%97%A8%E7%AF%872)-%E6%9C%AC%E5%9C%B0%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA%26%E6%9E%84%E5%BB%BA%E7%AC%AC%E4%B8%80%E4%B8%AAFlink%E5%BA%94%E7%94%A8.md)
   * [Flink从入门到放弃(入门篇3)-DataSetAPI](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/Flink/Flink%E4%BB%8E%E5%85%A5%E9%97%A8%E5%88%B0%E6%94%BE%E5%BC%83(%E5%85%A5%E9%97%A8%E7%AF%873)-DataSetAPI.md)
   * [Flink从入门到放弃(入门篇4)-DataStreamAPI](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/Flink/Flink%E4%BB%8E%E5%85%A5%E9%97%A8%E5%88%B0%E6%94%BE%E5%BC%83(%E5%85%A5%E9%97%A8%E7%AF%874)-DataStreamAPI.md)
   * [Flink集群部署](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/Flink/Flink%E9%9B%86%E7%BE%A4%E9%83%A8%E7%BD%B2.md)
   * [Flink重启策略](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/Flink/6-Flink%E9%87%8D%E5%90%AF%E7%AD%96%E7%95%A5.md)
   * [Flink的分布式缓存](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/Flink/7-Flink%E7%9A%84%E5%88%86%E5%B8%83%E5%BC%8F%E7%BC%93%E5%AD%98.md)
   * [Flink中的窗口](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/Flink/8-Flink%E4%B8%AD%E7%9A%84%E7%AA%97%E5%8F%A3.md)
   * [Flink中的Time](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/Flink/9-Flink%E4%B8%AD%E7%9A%84Time.md)
   * [Flink集群搭建的HA](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/Flink/10-Flink%E9%9B%86%E7%BE%A4%E7%9A%84%E9%AB%98%E5%8F%AF%E7%94%A8(%E6%90%AD%E5%BB%BA%E7%AF%87%E8%A1%A5%E5%85%85).md)
   * [Flink中的时间戳和水印](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/Flink/11-%E6%97%B6%E9%97%B4%E6%88%B3%E5%92%8C%E6%B0%B4%E5%8D%B0.md)
   * [Flink广播变量](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/Flink/12-Broadcast%E5%B9%BF%E6%92%AD%E5%8F%98%E9%87%8F.md)
   * [Flink-Kafka-Connector](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/Flink/13-Flink-Kafka-Connector.md)
   * [Flink-Table-&-SQL实战](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/Flink/14-Flink-Table-%26-SQL.md)
   * [15-Flink实战项目之实时热销排行](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/Flink/15-Flink%E5%AE%9E%E6%88%98%E9%A1%B9%E7%9B%AE%E4%B9%8B%E5%AE%9E%E6%97%B6%E7%83%AD%E9%94%80%E6%8E%92%E8%A1%8C.md)
   * [16-Flink-Redis-Sink](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/Flink/16-Flink-Redis-Sink.md)
   * [17-Flink消费Kafka写入Mysql](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/Flink/17-Flink%E6%B6%88%E8%B4%B9Kafka%E5%86%99%E5%85%A5Mysql.md)

#### Flink当前最火的实时计算引擎-放弃篇

   * [Flink漫谈系列1-概述](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/Flink%E6%BC%AB%E8%B0%88%E7%B3%BB%E5%88%97/Apache-Flink%E6%BC%AB%E8%B0%88%E7%B3%BB%E5%88%97(1)-%E6%A6%82%E8%BF%B0.md)
   * [Flink漫谈系列2-watermark](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/Flink%E6%BC%AB%E8%B0%88%E7%B3%BB%E5%88%97/Apache-Flink-%E6%BC%AB%E8%B0%88%E7%B3%BB%E5%88%97(02)-Watermark.md)
   * [Flink漫谈系列3-state](https://github.com/wangzhiwubigdata/God-Of-BigData/blob/master/Flink%E6%BC%AB%E8%B0%88%E7%B3%BB%E5%88%97/Apache-Flink-%E6%BC%AB%E8%B0%88%E7%B3%BB%E5%88%97(03)-State.md)


## Java进阶
### java并发编程

1. [java并发编程(一)：JAVA程序运行原来分析](java/Java高性能编程/高性能编程(一)：JAVA程序运行原来分析.md)  
2. [java并发编程(二)：线程的状态、终止和创建方式.md](java/Java高性能编程/高性能编程(二)：线程的状态、终止和创建方式.md)   
3. [java并发编程(三)：CPU性能优化手段](java/Java高性能编程/高性能编程(三)：CPU性能优化手段.md)  
4. [java并发编程(四)：线程通信.md](java/Java高性能编程/高性能编程(四)：线程通信.md)  
5. [java并发编程(五)：线程池详解.md](java/Java高性能编程/高性能编程(五)：线程池详解.md)  
6. [java并发编程(六)：线程可见性基础-JVM内存区域划分.md](java/Java高性能编程/高性能编程(六)：线程可见性基础-JVM内存区域划分.md)  
7. [java并发编程(七)：synchronized关键字详解.md](java/Java高性能编程/高性能编程(七)：synchronized关键字详解.md)  
8. [java并发编程(八)：ThreadLocal详解.md](java/Java高性能编程/高性能编程(八)：ThreadLocal详解.md)  
9. [java并发编程(九)：volatile关键字解析.md](java/Java高性能编程/高性能编程(九)：volatile关键字解析.md)  
10. [java并发编程(十)：锁机制.md](java/Java高性能编程/高性能编程(十)：锁机制.md)  
11. [java并发编程(十一)：乐观锁和悲观锁.md](java/Java高性能编程/高性能编程(十一)：乐观锁和悲观锁.md)  
12. [java并发编程(十二)：AQS原理以及AQS同步组件总结](java/Java高性能编程/高性能编程(十二)：AQS原理以及AQS同步组件总结.md)

* [并发面试题(一)](java/Java高性能编程/多线程面试题一.md)  
* [并发编程总结一：并发编程基础.md](java/Java高性能编程/并发编程总结一：并发编程基础.md)

## java框架 
### Springboot
* [springboot快速入门.md](java/框架/springboot/01.springboot快速入门.md)
* [一小时快速入门springboot](java/框架/springboot/进阶笔记/springboot.md)

### SpringCloud
* [springcloud入门实战（一）](java/框架/springcloud/springcloud入门实战（一）/SpringCloud.md)  
* [springcloud入门实战（二）](java/框架/springcloud/springcloud入门实战（二）/SpringCloud2.md)  


## 数据结构与算法
《玩转数据结构》学习笔记
1. [数组](java/数据结构与算法/数组.md)
2. [栈和队列](java/数据结构与算法/栈和队列.md)
3. [链表](java/数据结构与算法/链表.md)

## Java进阶
[HashMap详解](java/java进阶/HashMap详解.md)  
[反射基础](java/java进阶/java进阶之反射.md)  
[java8新特性之Optional类](java/java8新特性/Optional类.md)


## Redis
[Redis安装](bigdata/Redis/Redis安装.md)  
[Redis入门笔记](bigdata/Redis/Redis入门笔记.md)  
[Redis综合题](bigdata/Redis/Redis综合题.md)    

## hive
[hive安装](bigdata/hive/hive安装.md)  
[DDL数据定义](bigdata/hive/DDL数据定义.md)




