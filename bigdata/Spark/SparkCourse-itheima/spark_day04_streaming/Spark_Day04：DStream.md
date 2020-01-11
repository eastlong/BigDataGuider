---
stypora-copy-images-to: img
typora-root-url: ./
---



# Spark Day04：DStream

[TOC]



## 一、课程回顾与内容提纲

​		

### 1、昨日课程内容回顾

​		http://spark.apache.org/docs/2.2.0/sql-programming-guide.html

掌握SparkSQL模块功能，分为两个方面：

- 结构化数据处理，数据封装数据结构：Dataset、DataFrame = Dataset[Row]，如何将数据封装到Dataset，底层依然是RDD数据结构，Dataset/DataFrame  = RDD + Schema(StructType(StructFiled))
- 数据分析两种方式：SQL（类似HiveQL）和DSL（调用Dataset API）

### 2、今日课程内容提纲

​		针对流式数据处理模块：SparkStreaming（Spark 2.x：StructuredStreaming），数据封装在数据结构：DStream（分离的流，离散的流）：

- SparkStreaming 模块概述（知道）
  - 大数据平台架构模式：Lambda 架构
  - Streaming Processing 模型方式（几种方式）
  - Quick Start，快速入门SparkStreaming实时处理数据
- SparkStreaming 工作原理（掌握）
  - 按照时间间隔将流式数据划分很多Batch批次，将每批次的数据当做RDD进行处理
- DStream 是什么（掌握）
  - DStream = List[RDD]
- 流式数据源（从哪里读取数据） 
  - 基本数据源：Sockect 读取数据、File读取数据、自定义Receivers接收数据  - （了解）
  - 高级数据源：
    - Flume集成，Streaming从Flume Agent中读取数据-（了解）
    - Kafka集成（重点掌握），两种类型，三种方式（掌握）
- DStream Operations（掌握）
  - 无状态操作：transform
  - 有状态操作：累加统计，updateStateByKey、mapWithState
  - 窗口操作：趋势统计，window  -> 与SparkSQL集成
- SparkStreaming Checkpoint（知道）
  - Streaming中Checkpoint是鸡肋



## 二、SparkStreaming  概述

![1562721699966](/img/1562721699966.png)



### 1、**Lambda Architecture**

​		目前在企业中大数据平台架构模式：Lambda架构，分为三层架构，离线和实时并存架构。

![1562721747339](/img/1562721747339.png)

- 第一层：批处理层
- 第二层：速度层（实时数据处理层）
- 第三层：服务层（将处理分析的结果，提供给外部系统使用）

![1562721888539](/img/1562721888539.png)



### 2、Streaming 应用场景

![1562721945410](/img/1562721945410.png)





### 3、Streaming 数据处理方式

​		总的来说，对于Streaming数据处理方式分为两种：

- 一条一条数据处理
  - 典型框架Storm、Flink
  - 延迟性很低，达到亚秒级别
- 一批次一批次数据处理
  - 典型框架SparkStreaming、Storm Trident
  - 延迟性较高，毫秒级别，秒级别

![1562722558111](/img/1562722558111.png)



### 4、Quick Start

​		http://spark.apache.org/docs/2.2.0/streaming-programming-guide.html#a-quick-example

**案例说明**：实时对每批次数据进行词频WordCount

- 数据源：从TCP Socket 读取数据（将数据放入到 TCP Sockect中，在某台机器，打开某个端口）
  - 使用linux系统自带命令nc，打开端口，模拟放入数据
- 数据输出：将每批次分析处理的结果输出到控制台
  - println()

- 运行Example

```shell
bin/run-example --master local[2] streaming.NetworkWordCount bigdata-cdh03.itcast.cn 9999
```

- 提示，为了更好看出效果，设置日志级别$SPARK_HOME/conf/log4j.properties

```
将日志配置文件中所有INFO改为WARN即可
```

![1562724242669](/img/1562724242669.png)

![1562724261153](/img/1562724261153.png)

通过监控可以发现：

![1562724462156](/img/1562724462156.png)

监控Streaming应用运行性能图：

![1562724570373](/img/1562724570373.png)















​		pom.xml内容：

```xml
   <!-- 指定仓库位置，依次为aliyun、cloudera和jboss仓库 -->
    <repositories>
        <repository>
            <id>aliyun</id>
            <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
        </repository>
        <repository>
            <id>cloudera</id>
            <url>https://repository.cloudera.com/artifactory/cloudera-repos/</url>
        </repository>
        <repository>
            <id>jboss</id>
            <url>http://repository.jboss.com/nexus/content/groups/public</url>
        </repository>
    </repositories>

    <properties>
        <scala.version>2.11.8</scala.version>
        <scala.binary.version>2.11</scala.binary.version>
        <spark.version>2.2.0</spark.version>
        <hadoop.version>2.6.0-cdh5.14.0</hadoop.version>
        <hbase.version>1.2.0-cdh5.14.0</hbase.version>
        <jedis.version>2.8.0</jedis.version>
        <fastjson.version>1.2.31</fastjson.version>
        <mysql.version>5.1.38</mysql.version>
    </properties>

    <dependencies>

        <!-- 依赖Scala语言 -->
        <dependency>
            <groupId>org.scala-lang</groupId>
            <artifactId>scala-library</artifactId>
            <version>${scala.version}</version>
        </dependency>

        <!-- Spark Core 依赖 -->
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-core_${scala.binary.version}</artifactId>
            <version>${spark.version}</version>
        </dependency>

        <!-- Spark SQL 依赖 -->
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-sql_${scala.binary.version}</artifactId>
            <version>${spark.version}</version>
        </dependency>

        <!-- Spark Streaming 依赖 -->
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-streaming_${scala.binary.version}</artifactId>
            <version>${spark.version}</version>
        </dependency>

        <!-- SparkStreaming与Flume 集成依赖 -->
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-streaming-flume_${scala.binary.version}</artifactId>
            <version>${spark.version}</version>
        </dependency>

        <!-- Spark Streaming 与Kafka 0.8.2.1 集成依赖-->
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-streaming-kafka-0-8_${scala.binary.version}</artifactId>
            <version>${spark.version}</version>
        </dependency>

        <!-- Hadoop Client 依赖 -->
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-client</artifactId>
            <version>${hadoop.version}</version>
        </dependency>

        <!-- HBase Client 依赖 -->
        <dependency>
            <groupId>org.apache.hbase</groupId>
            <artifactId>hbase-server</artifactId>
            <version>${hbase.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hbase</groupId>
            <artifactId>hbase-hadoop2-compat</artifactId>
            <version>${hbase.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hbase</groupId>
            <artifactId>hbase-client</artifactId>
            <version>${hbase.version}</version>
        </dependency>

        <!-- MySQL Client 依赖 -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql.version}</version>
        </dependency>
        <dependency>
            <groupId>c3p0</groupId>
            <artifactId>c3p0</artifactId>
            <version>0.9.1.2</version>
        </dependency>

        <!-- Jedis 依赖 -->
        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
            <version>${jedis.version}</version>
        </dependency>

        <!-- FastJson 依赖 -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>${fastjson.version}</version>
        </dependency>
        <!-- Codehaus 依赖 -->
        <dependency>
            <groupId>org.codehaus.jettison</groupId>
            <artifactId>jettison</artifactId>
            <version>1.3</version>
        </dependency>

    </dependencies>

    <build>
        <outputDirectory>target/classes</outputDirectory>
        <testOutputDirectory>target/test-classes</testOutputDirectory>
        <resources>
            <resource>
                <directory>${project.basedir}/src/main/resources</directory>
            </resource>
        </resources>
        <!-- Maven 编译的插件 -->
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.0</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>
            <plugin>
                <groupId>net.alchim31.maven</groupId>
                <artifactId>scala-maven-plugin</artifactId>
                <version>3.2.0</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                            <goal>testCompile</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```





![1562725058454](/img/1562725058454.png)

```scala
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Streaming流式数据处理，从TCP Sockect 读取数据，对每批次的数据进行词频统计WordCount
  */
object StreamingWordCount {

	def main(args: Array[String]): Unit = {


		// TODO: 1、构建Streaming应用上下文对象
		val sparkConf = new SparkConf()
	    	.setMaster("local[3]")
	    	.setAppName("StreamingWordCount")
		// def this(conf: SparkConf, batchDuration: Duration) 设置批处理时间间隔：划分流式数据时间间隔
		val ssc: StreamingContext = new StreamingContext(sparkConf, Seconds(5))

		// 设置级别
		ssc.sparkContext.setLogLevel("WARN")


		// TODO: 2、从TCP Socket 读取数据 bigdata-cdh03.itcast.cn  9999
		/*
		  def socketTextStream(
			  hostname: String,
			  port: Int,
			  storageLevel: StorageLevel = StorageLevel.MEMORY_AND_DISK_SER_2
			): ReceiverInputDStream[String]
		 */
		val inputDStream: InputDStream[String] = ssc.socketTextStream("bigdata-cdh03.itcast.cn", 9999)


		// TODO: 3、针对DStream调用函数进行处理分析（基本与RDD中Transformation函数类似）
		// a. 将每行数据按照分隔符进行分割为单词，并且过滤为空的单词
		val wordsDStream: DStream[String] = inputDStream.flatMap(line => line.split("\\s+").filter(word => word.length > 0))

		// b. 将单词转换为二元组，表示每个单词出现一次
		val tuplesDStream: DStream[(String, Int)] = wordsDStream.map(word => (word ,1))

		// c. 按照单词Key分组，聚合统计出现的次数
		val wordCountsDStream: DStream[(String, Int)] = tuplesDStream.reduceByKey((a, b) => a + b)



		// TODO: 4、将每批次统计分析的结果进行输出
		wordCountsDStream.print(10)


		// TODO: 5、启动Streaming应用程序
		ssc.start()
		// 当流式应用启动以后，正常情况下，一直运行，除非程序异常或者人为停止
		ssc.awaitTermination()

		// 当应用运行完成以后，关闭资源
		ssc.stop(stopSparkContext = true, stopGracefully = true)
	}

}
```

![1562726606097](/img/1562726606097.png)





## 三、Streaming  工作原理



### 1、启动Receivers接收器

![1562726947675](/img/1562726947675.png)

![1562726933032](/img/1562726933032.png)



### 2、将接收数据流划分为Block

​		Receivers接收器同样按照时间间隔将流式的数据划分为Block，时间间隔blockInterval，默认值为200ms。

![1562727386555](/img/1562727386555.png)

![1562727313305](/img/1562727313305.png)

假设BatchInterval为1s，计算每批次处理的Block是多少？？

```
1s = 1000ms  = 5 * 200ms  =  5 * Block  =  RDD中5个分区
```



### 3、每批次时间间隔加载Job执行

![1562727661465](/img/1562727661465.png)



### 4、每批次RDD中分区数目

![1562727775283](/img/1562727775283.png)



### 5、Streaming流式数据处理流程

​		对流式数据处理，与批处理或交互式处理一样，同样三步骤：

- 数据源：从哪里读取流式数据
  - TCP Socket（开发测试，某个功能）
  - FLUME（了解）
  - KAFKA（掌握） - 95%以上的企业都是从Kafka中读取数据
- 数据处理：调用DStream#Transformation函数
  - 无状态处理：transform函数
  - 有状态处理：updateStateByKey、mapWithState
  - 窗口处理：window，聚合窗口
- 结果输出：将每批次处理分析的结果输出
  - 就是一个输出函数foreachRDD，针对每批次结果RDD进行输出：针对RDD中每个分区数据进行输出
  - RDBMS（关系型数据库）
  - Redis（状态统计）
  - HBase（全量数据存储）、ES（近期数据存储） ->  ETL

![1562728123345](/img/1562728123345.png)



## 四、DStream

官方文档：http://spark.apache.org/docs/2.2.0/streaming-programming-guide.html#discretized-streams-dstreams

### 1、DStream 定义

![1562728235977](/img/1562728235977.png)



如何创建StreamingContext

![1562728387935](/img/1562728387935.png)



企业中，往往使用如下函数闯将StreamingContext流式上下文对象：

![1562728512945](/img/1562728512945.png)



### 2、**Transformations**

![1562728659759](/img/1562728659759.png)

​		DStream中transform函数非常重要。

![1562728715355](/img/1562728715355.png)

​		函数声明如下：

```scala
def transform[U: ClassTag](transformFunc: RDD[T] => RDD[U]): DStream[U] 
```

​		针对每批次的RDD进行操作，返回任然是RDD。

建议：在Streaming实际开发中，可以的RDD操作的，就不要对DStream操作。（当使用DStream调用的函数，RDD中包含的话，就对RDD进行操作）



案例代码：

```scala
		// TODO: 3、针对DStream调用函数进行处理分析（基本与RDD中Transformation函数类似）
		// def transform[U: ClassTag](transformFunc: RDD[T] => RDD[U]): DStream[U]
		val wordCountsDStream: DStream[(String, Int)] = inputDStream.transform(rdd =>{
			rdd
				// a. 将每行数据按照分隔符进行分割为单词，并且过滤为空的单词
				.flatMap(line => line.split("\\s+").filter(word => word.length > 0))
				// b. 将单词转换为二元组，表示每个单词出现一次
				.mapPartitions{words => words.map(word => (word, 1))}
				// c. 按照单词Key分组，聚合统计出现的次数
				.reduceByKey((a, b) => a + b)
		})
```

![1562729241795](/img/1562729241795.png)

### 3、**Output Operations**

![1562728700639](/img/1562728700639.png)

![1562730078963](/img/1562730078963.png)



案例代码：

```scala
		// TODO: 4、将每批次统计分析的结果进行输出
		// 调用DStream输出函数foreachRDD，针对每批次结果RDD进行输出操作
		wordCountsDStream.foreachRDD{ (rdd, time) =>
			// TODO: 打印每批次时间，格式： 2019/07/10 11:39:00
			val batchTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(time.milliseconds))
			println("-----------------------------------")
			println(s"batchTime: $batchTime")
			println("-----------------------------------")

			// 对于Streaming应用来说，输出RDD的时候，需要判断RDD是否存在
			if(!rdd.isEmpty()){
				// 针对RDD中每个分区数据操作
				rdd.foreachPartition{iter =>
					iter.foreach(println)
				}
			}
		}
```



### 4、输出到HDFS分区目录



```scala
		// TODO: 4、将每批次统计分析的结果进行输出
		wordCountsDStream.foreachRDD{ (rdd, time) =>
			// TODO: 打印每批次时间，格式： 2019-07-10 11:39:00
			val batchTime: String = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time.milliseconds))
			val dateStr = batchTime.substring(0, 10)
			val hourStr = batchTime.substring(11, 13)

			// 对于Streaming应用来说，输出RDD的时候，需要判断RDD是否存在
			if(!rdd.isEmpty()){
				// 降低分区数为1，保存的文件只有一个
				rdd.coalesce(1).saveAsTextFile(s"datas/streaming/logs/date_str=$dateStr/hour_str=$hourStr/" + System.currentTimeMillis())
			}
		}
```

当Streaming应用将数据写入到HDFS文件系统中，调整如下参数：

![1562731420278](/img/1562731420278.png)



推荐一本书：

![1562732041455](/img/1562732041455.png)



## 五、数据源

![1562732094079](/img/1562732094079.png)



- ***Basic sources***: Sources directly available in the StreamingContext API. Examples: file systems, and socket connections.
- ***Advanced sources***: Sources like Kafka, Flume, Kinesis, etc. are available through extra utility classes. These require linking against extra dependencies as discussed in the [linking](http://spark.apache.org/docs/2.2.0/streaming-programming-guide.html#linking) section.



### 1、**File Streams**

​		Streaming流式应用从文件目录中读取数据，监控某个目录中，是否有新的文件，如果有的话，立即读取数据，进行处理（类似Flume中Agent中监控目录spoolDirectory Source）。





### 2、**Custom Receivers**

​		可以自定义Receiver读取流式的数据（读取的地方自己定，依据需求而定）

参考文档：http://spark.apache.org/docs/2.2.0/streaming-custom-receivers.html

```
abstract class Receiver[T](val storageLevel: StorageLevel) extends Serializable 
```

![1562732707861](/img/1562732707861.png)





### 3、Advanced Sources

文档：http://spark.apache.org/docs/2.2.0/streaming-programming-guide.html#advanced-sources

![1562740600308](/img/1562740600308.png)

从Spark 2.3版本开始，Streaming与Flume集成，已经过时，不需要太多掌握与Flume获取数据处理。

![1562740672993](/img/1562740672993.png)



![1562741702255](/img/1562741702255.png)





## 六、集成Flume

官方文档：http://spark.apache.org/docs/2.2.0/streaming-flume-integration.html

### 1、Push-based Approach

![1562742040937](/img/1562742040937.png)



### 2、Pull-based Approach

![1562743110484](/img/1562743110484.png)

​		需要将如上的三个JAR包放入到Flume安装目录的lib目录下，启动Flume Agent自动加载。



## 七、集成Kafka

​		官方文档：http://spark.apache.org/docs/2.2.0/streaming-kafka-integration.html

![1562744854947](/img/1562744854947.png)

### 1、Kafka 数据准备

Kafka 面试题：

1）Kafka 集群配置，Topic分区数、副本数，为什么这么设置？？？？？每日数据量

​		是否连接Topic数据的分区策略

2）Kafka Topic中数据保留策略（删除策略），为什么这么选择？？？

​		数据量、数据大小

​		Compact（压缩）



模拟数据：订单数据，实时产生，统计各个省份销售订单额

- 数据字段如下：

```
订单ID,省份ID,订单金额
orderId,provinceId,orderPrice
```

- 模拟产生订单数据：

  ```
  201710261645320001,12,45.00
  201710261645320002,12,20.00
  201710261645320301,14,10.00
  201710261635320e01,12,4.00
  
  201710261645320034,21,100.50
  201710261645320021,14,2.00
  
  201710261645323501,12,1.50
  201710261645320281,14,3.00
  201710261645332568,15,1000.50
  
  201710261645320231,15,1000.00
  
  201710261645320001,12,1000.00
  201710261645320002,12,1000.00
  201710261645320301,14,1000.00
  ```

- Kafka Topic相关操作命令：

```shell
KAFKA_HOME=/export/servers/kafka

# 查看Topic信息
${KAFKA_HOME}/bin/kafka-topics.sh --list --zookeeper bigdata-cdh01.itcast.cn:2181,bigdata-cdh02.itcast.cn:2181,bigdata-cdh03.itcast.cn:2181/kafka100

# 创建topic
${KAFKA_HOME}/bin/kafka-topics.sh --create --zookeeper bigdata-cdh01.itcast.cn:2181,bigdata-cdh02.itcast.cn:2181,bigdata-cdh03.itcast.cn:2181/kafka100 --replication-factor 2 --partitions 3 --topic orderTopic
 
 # 模拟生成者，控制台发送数据
${KAFKA_HOME}/bin/kafka-console-producer.sh --broker-list bigdata-cdh01.itcast.cn:9092,bigdata-cdh02.itcast.cn:9092,bigdata-cdh03.itcast.cn:9092 --topic orderTopic 

# 模拟消费者，控制台消费数据
${KAFKA_HOME}/bin/kafka-console-consumer.sh --zookeeper bigdata-cdh01.itcast.cn:2181,bigdata-cdh02.itcast.cn:2181,bigdata-cdh03.itcast.cn:2181/kafka100 --topic orderTopic --from-beginning
```



### 2、**Receiver-based**

![1562745839686](/img/1562745839686.png)









### 3、Direct Approach

http://spark.apache.org/docs/2.2.0/streaming-kafka-0-8-integration.html#approach-2-direct-approach-no-receivers

![1562749500592](/img/1562749500592.png)

-  periodically queries Kafka for the latest offsets in each topic+partition

  周期性到Kafka中查询各个Topic对应分区Partition的最新偏移量offset

- accordingly defines the offset ranges to process in each batch

  Streaming应用每批次Batch处理的数据依据Topic中各个分区Partition的偏移量获取数据

  -  When the jobs to process the data are launched, Kafka’s simple consumer API is used to read the defined ranges of offsets from Kafka

    表示当加载每批次的job的时候，依据已经定义好的偏移量范围（各个Topic的各个分区Partition）使用Kafka Simple API读取数据，进行分析处理。

![1562749508661](/img/1562749508661.png)

Streaming计算框架处理数据语义：

http://spark.apache.org/docs/2.2.0/streaming-programming-guide.html#definitions

![1562749349072](/img/1562749349072.png)

- 最多一次：表示数据最多被处理一次，可能出现没有被处理
- 至少一次：表示数据至少处理一次，可能多次处理（重复处理）
- 精确一次：表示数据仅仅被处理一次，不会重复处理和不处理。



### 4、集成Kafka性能调优

![1562750009222](/img/1562750009222.png)









## 八、状态统计

**业务需求：**

​		实时统计各个省份的销售订单额（先不考虑退单的情况）。



### 1、状态函数updateStateByKey

```scala
  def updateStateByKey[S: ClassTag](
      updateFunc: (Seq[V], Option[S]) => Option[S]
    ): DStream[(K, S)] = ssc.withScope {
    updateStateByKey(updateFunc, defaultPartitioner())
  }
```

运行异常：

```scala
19/07/10 17:54:55 ERROR streaming.StreamingContext: Error starting the context, marking it as stopped
java.lang.IllegalArgumentException: requirement failed: The checkpoint directory has not been set. Please set it by StreamingContext.checkpoint().
	at scala.Predef$.require(Predef.scala:224)
	at org.apache.spark.streaming.dstream.DStream.validateAtStart(DStream.scala:243)
	at org.apache.spark.streaming.dstream.DStream$$anonfun$validateAtStart$8.apply(DStream.scala:276)
	at org.apache.spark.streaming.dstream.DStream$$anonfun$validateAtStart$8.apply(DStream.scala:276)
	at scala.collection.immutable.List.foreach(List.scala:381)
```

设置检查点目录，保存状态信息：

```scala
// TODO：有状态的统计，使用以前批次Batch得到的状态，为了安全起见，程勋自动定时的将数据进行Checkpoint到文件系统
ssc.checkpoint("datas/streaming/state-order-0001")
```

针对状态更新函数来说，最好在使用函数之前，对每批次的数据进行聚合操作，性能更好：

```scala
		// TODO: 3、实时统计各个省份销售订单额，数据格式：orderId,provinceId,orderPrice
		val ordersDStream: DStream[(Int, Double)] = kafkaDStream.transform{ rdd =>
			rdd
				// 过滤异常数据（为null，字段不对）
		    	.filter(msg => null != msg._2 && msg._2.trim.split(",").length >= 3)
				// 提取字段：provinceId,orderPrice
		    	.mapPartitions{ datas =>
					datas.map{ case(_, order) =>
						val Array(orderId, provinceId, orderPrice) = order.toString.split(",")
						// 返回二元组
						(provinceId.toInt, orderPrice.toDouble)
					}
				}
				// 聚合，按照各个省份聚合, 获取得到此批次中各个省份的订单销售额
		    	.reduceByKey((a, b) => a + b)
		}
```

![1562752945125](/img/1562752945125.png)



### 2、检查点恢复

文档：http://spark.apache.org/docs/2.2.0/streaming-programming-guide.html#how-to-configure-checkpointing

![1562756700822](/img/1562756700822.png)



```scala
		/*
		  def getOrCreate(
			  checkpointPath: String,
			  creatingFunc: () => StreamingContext,
			  hadoopConf: Configuration = SparkHadoopUtil.get.conf,
			  createOnError: Boolean = false
			): StreamingContext
		 */
		val context = StreamingContext.getOrCreate(
			// 当检查点目录存在的时候（非第一次运行），再次启动应用从此目录中的数据恢复StreamingContext
			CHECKPOINT_PATH, //
			// 当检查点目录不存在的时候，创建新的StreamingContxt实例对象，第一次运行的时候
			() => {
				val sparkConf = new SparkConf()
					.setMaster("local[3]") // 其中一个线程被运行Receiver接收器，剩余两个运行Task任务，并行的运行
					.setAppName("OrderTotalStreamingCkpt")
					// TODO: 表示的是每秒钟读取每个分区的数据的条目数的最大值，此时3个分区，BatchInterval为5s，最大数据量：15万
					.set("spark.streaming.kafka.maxRatePerPartition", "10000")
				// def this(conf: SparkConf, batchDuration: Duration) 设置批处理时间间隔：划分流式数据时间间隔
				val ssc: StreamingContext = new StreamingContext(sparkConf, Seconds(5))

				// TODO：有状态的统计，使用以前批次Batch得到的状态，为了安全起见，程勋自动定时的将数据进行Checkpoint到文件系统
				ssc.checkpoint(CHECKPOINT_PATH)

				// 处理数据
				processStreaming(ssc)

				ssc
			}
		)
```



推荐博文：

- Spark-Streaming状态管理应用优化之路
  - http://sharkdtu.com/posts/spark-streaming-state.html
- Spark Streaming 实时计算在甜橙金融监控系统中的应用及优化
  - https://mp.weixin.qq.com/s/Kv1Qq4118I2itYwPYyQUoA

