---
stypora-copy-images-to: img
typora-root-url: ./
---



# Spark Day05：Spark Stack





## 一、课程回顾与内容提纲

​		

### 1、昨日课程内容回顾

​	参考上次课程复习归纳思维导图

### 2、今日课程内容提纲

​		Spark 框架中三大基础模块（Core、SQL、Streaming）高级内容，非常重要：

- 新的状态统计函数mapWithState（掌握）
  - 从Spark 1.6版本提供函数，性能比updateStateByKey高10倍以上
- 窗口统计（掌握）
  - 基本窗口统计使用：每次实时处理的数据不再是一个批次的数据，而是很多批次的数据（数据重复处理）
  - 集成SparkSQL分析处理：Dataset/DataFrame
- Streaming 应用高级用法（知道）
  - Streaming如何停止，比如应用升级，软件升级，由于其他原因需要暂定应用
  - 每日状态统计如何实现（常见两种方式）
- 集成Kafka 0.10.0+版本（掌握）
  - 仅仅读取数据API不一样
- 结构化流式处理（知道）
  - Structured Streaming，通过两个案例，入门水平，第一个案例：Socket；第二案例：Kafka

- Core&SQL内容（掌握）
  - SparkCore如何与HBase数据交互（读写数据）
  - SparkSQL中开窗函数（窗口函数和分析函数）



## 二、状态统计函数mapWithState



![1562895033279](/img/1562895033279.png)



官方提供updateStateByKey与mapWithState性能比较：

![1562895121083](/img/1562895121083.png)





## 三、窗口统计Window

​	

### 1、应用场景

​		实时统计最近一段时间的数据的状态信息，比如外卖饿了么来说，实时统计最近二十分钟未送出订单数据，每隔1分钟刷新一次页面（重新计算一次数据）

在苏宁，为了用户推荐产品，发现现象：

- 85%用户，在5分钟之内搜索、下单及支付完成

- 95%用户，在8分钟之内搜索、下单及支付完成

- 99%用户，在10分钟之内搜索、下单及支付完成

  ​	为了统计出用户最近搜索的行为分析，关键词，点积商品类别，使用Streaming Window统计

  ```
  每1分钟统计最近15分钟用户行为数据
  ```

  

### 2、窗口使用

文档：http://spark.apache.org/docs/2.2.0/streaming-programming-guide.html#window-operations

![1562897133064](/img/1562897133064.png)



### 3、集成SQL

文档：http://spark.apache.org/docs/2.2.0/streaming-programming-guide.html#dataframe-and-sql-operations

![1562899359886](/img/1562899359886.png)



## 四、Streaming 生产使用

​		Streaming应用运行于实际生产环境，往往需要考虑很多方面优化：平台、代码、业务。

### 1、如何停止Streaming应用

​		当Streaming在运行的时候，由于程序的升级、软件版本的升级或者特殊需要（集群环境停止）等原因，需要人为停止Streaming应用运行。停止应用的时候要保证：正在处理的批次数据要处理完成（优雅的停止）

#### 1）、设置Streaming优雅的停止

参数如下：

```xml
spark.streaming.stopGracefullyOnShutdown
```

第一步、需要在应用配置中设置

```scala
val sparkConf = new SparkConf()
	.setMaster("local[3]") // 其中一个线程被运行Receiver接收器，剩余两个运行Task任务，并行的运行
	.setAppName("StreamingGracefullyStop")
	// TODO: 表示的是每秒钟读取每个分区的数据的条目数的最大值，此时3个分区，BatchInterval为5s，最大数据量：15万
	.set("spark.streaming.kafka.maxRatePerPartition", "10000")
	// TODO: 设置流式应用程序停止时，优雅的停止
	.set("spark.streaming.stopGracefullyOnShutdown", "true")
// def this(conf: SparkConf, batchDuration: Duration) 设置批处理时间间隔：划分流式数据时间间隔
val ssc: StreamingContext = new StreamingContext(sparkConf, Seconds(5))
```



#### 2）、如何停止Streaming Application

​		此处采用一种方式，监控HDFS目录中某个文件是否存在，如果存在，就停止Streaming应用。

```scala
		// TODO: 当应用启动以后，循环判断 HDFS上目录下某个文件（监控文件）是否存在，如果存在就停止优雅停止应用
		// 每隔10s中检查应用是否停止
		val checkInterval = 10 * 1000
		// 表示应用是否停止
		var isStreamingStop = false

		// 当流式应用没有被停止的时候，检查监控文件是否存在
		while(!isStreamingStop){
			// `true` if it's stopped, 等待10s以后，应用是否停止
			isStreamingStop = context.awaitTerminationOrTimeout(checkInterval)

			// 应用未停止并且监控文件存在，停止流式应用，要停止应用创建下面文件
			// 创建命令：${HADOOOP_HOME}/bin/hdfs dfs -touchz /spark/streaming/stop-order
			if(!isStreamingStop && isExsitsMonitorFile("datas/streaming/stop-order.txt")){
				// 当应用运行完成以后，关闭资源
				context.stop(stopSparkContext = true, stopGracefully = true)
			}
		}
```

检查文件是否存在：

```scala
	/**
	  * 指定HDFS路径，判断文件是否存在，存在返回true
	  */
	def isExsitsMonitorFile(checkFile: String): Boolean = {
		// i. 加载HDFS Client配置信息
		val conf = new Configuration()

		// ii. 获取路径
		val chechPath = new Path(checkFile)

		// iii. 获取HDFS文件系统
		val hdfs = chechPath.getFileSystem(conf)

		// iv. 判断文件是否存在
		hdfs.exists(chechPath)
	}
```



### 2、每日实时转态统计

​		每天从凌晨开始，统计进行数据状态信息。

**思路：**

​		每天启动一个应用，用于统计今日数据，得到状态信息。

- 每日凌晨的时候，先停止昨天的应用
- 再重启今日的新的应用

```scala
		val sparkConf = new SparkConf()
			.setMaster("local[3]") // 其中一个线程被运行Receiver接收器，剩余两个运行Task任务，并行的运行
			.setAppName("OrderTotalMapWithState")
			// TODO: 表示的是每秒钟读取每个分区的数据的条目数的最大值，此时3个分区，BatchInterval为5s，最大数据量：15万
			.set("spark.streaming.kafka.maxRatePerPartition", "10000")
		// 构建SparkContext上下文实例对象
		val sc: SparkContext = SparkContext.getOrCreate(sparkConf)

		// TODO：通过while死循环的方式，次日凌晨重启应用
		while(true){
			// TODO: 获取启动应用时距离次日凌晨时长
			val restInterval: Long = {
				// i. 获取当前时间
				val nowDate = new Date()
				
				// ii. 获取次日凌晨的时间
				val nextDate = new Date(nowDate.getYear, nowDate.getMonth, nowDate.getDate + 1)
				
				// iii. 计算时长
				nextDate.getTime - nowDate.getTime
			}
			
			// TODO: 1、构建Streaming应用上下文对象
			val CKPT_DIR = s"$CHECKPOINT_PATH/${new SimpleDateFormat("yyyyMMdd").format(new Date())}"
			val context = StreamingContext.getOrCreate(
				// 当检查点目录存在的时候（非第一次运行），再次启动应用从此目录中的数据恢复StreamingContext
				CKPT_DIR, //
				// 当检查点目录不存在的时候，创建新的StreamingContxt实例对象，第一次运行的时候
				() => {
					// def this(conf: SparkConf, batchDuration: Duration) 设置批处理时间间隔：划分流式数据时间间隔
					val ssc: StreamingContext = new StreamingContext(sc, Seconds(5))

					// TODO：有状态的统计，使用以前批次Batch得到的状态，为了安全起见，程勋自动定时的将数据进行Checkpoint到文件系统
					ssc.checkpoint(CKPT_DIR)

					// 处理数据
					processStreaming(ssc)

					ssc
				}
			)

			// 设置级别
			context.sparkContext.setLogLevel("WARN")

			// TODO: 5、启动Streaming应用程序
			context.start() // 启动Receiver接收器，实时接收数据源的数据
			
			// 当流式应用启动以后，正常情况下，一直运行，除非程序异常或者人为停止
			context.awaitTerminationOrTimeout(restInterval)

			// 当应用运行完成以后，关闭资源, TODO： 不停止SparkContext, 再次启动Streaming应用很快
			context.stop(stopSparkContext = false, stopGracefully = true)
		}
```



## 五、集成Kafka 0.10.0+

​		SparkStreaming集成Kafka 0.10.0+版本，采用new Consumer API读取Topic中的数据，类似Old Consumer API中Direct方式读取数据。

​		文档：http://spark.apache.org/docs/2.2.0/streaming-kafka-0-10-integration.html

### 1、加入依赖

```
groupId = org.apache.spark
artifactId = spark-streaming-kafka-0-10_2.11
version = 2.2.0
```



### 2、深入KafkaConsumer

​		新的消费者API提供类ConsumerRecord，将Topic中每条数据封装在此类中（数据和元数据）

![1562900165766](/img/1562900165766.png)



### 3、读取Kafka 数据

```scala
		// TODO: 2、从Kafka Topic中读取数据，集成Kafka 0.10.0以上版本的新的消费者API
		/*
		  def createDirectStream[K, V](
			  ssc: StreamingContext,
			  locationStrategy: LocationStrategy,
			  consumerStrategy: ConsumerStrategy[K, V]
			): InputDStream[ConsumerRecord[K, V]]
		 */
		// 相当于：MapReduce处理数据是MapTask本地性，相对于RDD中分区数据处理的Perfered Location
		val locationStrategy: LocationStrategy = LocationStrategies.PreferConsistent
		/*
		  def Subscribe[K, V](
			  topics: Iterable[jl.String],
			  kafkaParams: collection.Map[String, Object]
		  ): ConsumerStrategy[K, V]
		 */
		// 表示从哪些Topic中读取数据
		val topics: Iterable[String] = Array("orderTopic")
		// 表示从Kafka 消费数据的配置参数，传递给KafkaConsumer
		val kafkaParams: collection.Map[String, Object] = Map(
			"bootstrap.servers" ->
				"bigdata-cdh01.itcast.cn:9092,bigdata-cdh02.itcast.cn:9092,bigdata-cdh03.itcast.cn:9092",
			"key.deserializer" -> classOf[StringDeserializer],
			"value.deserializer" -> classOf[StringDeserializer],
			"group.id" -> "kafka-order-group-0001",
			"auto.offset.reset" -> "latest",
			"enable.auto.commit" -> (false: java.lang.Boolean)
		)
		// 指定从Kafka消费数据的消费策略，涵盖topics和配置参数
		val  consumerStrategy: ConsumerStrategy[String, String] = ConsumerStrategies.Subscribe(
			topics, kafkaParams
		)
		// 从Kafka的Topic中读取数据
		val kafkaDStream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](
			ssc, //
			locationStrategy, //
			consumerStrategy
		)
```



### 4、获取偏移量Offset数据

![1562905059037](/img/1562905059037.png)

```scala
// 对于Streaming应用来说，输出RDD的时候，需要判断RDD是否存在
if(!rdd.isEmpty()){
    rdd.foreachPartition{iter => iter.foreach(println)}

    // TODO: 当直接从Kafka Topic读取数据的RDD为KafkaRDD，里面包含RDD中各个分区偏移量范围
    val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
    for(offsetRange <- offsetRanges){
        println(s"topic: ${offsetRange.topic}   partition: ${offsetRange.partition}  offsets: ${offsetRange.fromOffset} to ${offsetRange.untilOffset}")
    }
}
```



## 六、Structured Streaming

推荐文章：https://www.cnblogs.com/yy3b2007com/p/9463426.html

![1562913447775](/img/1562913447775.png)





官方文档：http://spark.apache.org/docs/2.2.0/structured-streaming-programming-guide.html

​		从Spark 2.0开始提出来的，到2.2.0版本开始，才进行Release版本，可以用于实际生产环境。

当从Kafka Topic中读取数据，需要添加如下依赖：

```xml
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-sql-kafka-0-10_${scala.binary.version}</artifactId>
            <version>${spark.version}</version>
        </dependency>
```











![1562915637832](/img/1562915637832.png)







## 七、Spark Core优化



### 1、提交应用到集群执行

​		将Spark应用以`Cluster` DeployMode运行在`Spark Standalone`集群上

```shell
SPARK_HOME=/export/servers/spark-2.2.0-bin-2.6.0-cdh5.14.0

## 以client的Deploy Mode运行
${SPARK_HOME}/bin/spark-submit \
--master spark://bigdata-cdh02.itcast.cn:7077 \
--deploy-mode client \
--class cn.itcast.bigdata.spark.SparkWordCountSubmit \
--driver-memory 512m \
--executor-memory 512m \
--executor-cores 1 \
--total-executor-cores 2 \
${SPARK_HOME}/spark-day01_2.11-1.0.jar \
/datas/wordcount.input /datas/sparkOutput
```



![1562919391137](/img/1562919391137.png)



两种解决方案：

- 第一种方案：将jar包发送到所有从节点相同的目录中
- 第二种方案：将JAR包放到HDFS上即可。

![1562919676698](/img/1562919676698.png)

```shell
## Usage: spark-submit [options] <app jar | python file> [app arguments]

SPARK_HOME=/export/servers/spark-2.2.0-bin-2.6.0-cdh5.14.0

## 以cluster的Deploy Mode运行
${SPARK_HOME}/bin/spark-submit \
--master  spark://bigdata-cdh02.itcast.cn:6066 \
--deploy-mode cluster \
--class cn.itcast.bigdata.spark.SparkWordCountSubmit \
--driver-memory 512m \
--driver-cores 1 \
--supervise \
--executor-memory 512m \
--executor-cores 1 \
--total-executor-cores 2 \
hdfs://bigdata-cdh01.itcast.cn:8020/spark/app/spark-day01_2.11-1.0.jar \
/datas/wordcount.input /datas/sparkOutput
```



### 2、Shuffle数据存储目录

​		Spark Application在运行的时候，有一个临时目录，用于存储运行过程中数据，比如Shuffle的数据。

```properties
spark.local.dir     /tmp
```

在实际生成环境中，需要配置此属性，尤其针对Streaming应用来说。

![1562919988286](/img/1562919988286.png)

从Spark 1.x以后，应用运行在不同的集群，使用不同属性参数配置（`$SPARK_HOME/conf/spark-env.sh`）：

- Spark Standalone集群：
  - `SPARK_LOCAL_DIRS=/data01/spark/datasDir,/datas02/spark/datasDir`
- Hadoop YARN集群：
  - `LOCAL_DIRS=/data01/spark/datasDir,/datas02/spark/datasDir`



## 八、SparkCore 与HBase交互

​		Spark如何读写HBase表中的数据，使用InputFormat和OutputFormat。

### 1、回顾Spark如何读取HDFS上文本文件



```scala
 hadoopFile(
     path,  // 数据文件存储的路径
     classOf[TextInputFormat],  // InputFormat格式，Key：LongWritable，Value: Text
     classOf[LongWritable],  // Key的类型
     classOf[Text], // Value的类型
      minPartitions  // 数据封装在RDD分区数目
 ).map(pair => pair._2.toString).setName(path)
```

​		HBase与MapReduce集成时，MapReduce从HBase Table表读写数据，提供一套InputFormat和OutputFormat实现类：

- 输入格式：TableInputFormat

  ​	MapReduce程序从HBase表中读取数据，采用的事TableInputFormat输入格式

![1562921577803](/img/1562921577803.png)

​		从HBase表中读取数据，将读取的每条数据封装在（Key，Value）对中，类型如下：

```java
public abstract class TableInputFormatBase extends InputFormat<ImmutableBytesWritable, Result> 
```

​		其中Key就是：ImmutableBytesWritable，Value就是：Result

设置属性从HBase那张表读取数据：

```scala
 /** Job parameter that specifies the input table. */
  public static final String INPUT_TABLE = "hbase.mapreduce.inputtable";
```



- 输出格式：TableOutputmat

  ​	MapReduce程序将数据写入到HBase表中，采用就是TableOutputFormat输出格式

![1562921849312](/img/1562921849312.png)





### 2、集成HBase依赖

```xml
		 <hbase.version>1.2.0-cdh5.14.0</hbase.version>

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
```



### 3、读取HBase表数据

​		使用SparkContext中API，如下读取数据：

```scala
  def newAPIHadoopRDD[K, V, F <: NewInputFormat[K, V]](
      conf: Configuration = hadoopConfiguration,
      fClass: Class[F],
      kClass: Class[K],
      vClass: Class[V]): RDD[(K, V)]
```





### 4、写入数据到HBase表中

​		将RDD数据保存到HBase表中，如下API：

```scala
  def saveAsNewAPIHadoopFile(
      path: String,
      keyClass: Class[_],
      valueClass: Class[_],
      outputFormatClass: Class[_ <: NewOutputFormat[_, _]],
      conf: Configuration = self.context.hadoopConfiguration): Unit
```



### 5、序列化异常

```java
java.io.NotSerializableException: org.apache.hadoop.hbase.io.ImmutableBytesWritable
Serialization stack:
	- object not serializable (class: org.apache.hadoop.hbase.io.ImmutableBytesWritable, value: 30 30 30 31 33 33 37 31 5f 32 30 31 35 2d 30 34 2d 32 31 20 31 34 3a 32 37 3a 35 32 5f 33 31 34 32 39 35 39 39 35 36 33 31 34 39 36)
	- field (class: scala.Tuple2, name: _1, type: class java.lang.Object)
	- object (class scala.Tuple2, (30 30 30 31 33 33 37 31 5f 32 30 31 35 2d 30 34 2d 32 31 20 31 34 3a 32 37 3a 35 32 5f 33 31 34 32 39 35 39 39 35 36 33 31 34 39 36,keyvalues={00001371_2015-04-21 07:53:56_314295750751242/info:date/1558185213056/Put/vlen=21/seqid=0, 00001371_2015-04-21 07:53:56_314295750751242/info:order_amt/1558185213056/Put/vlen=3/seqid=0, 00001371_2015-04-21 07:53:56_314295750751242/info:order_id/1558185213056/Put/vlen=15/seqid=0, 00001371_2015-04-21 07:53:56_314295750751242/info:order_id_new/1558185213056/Put/vlen=15/seqid=0, 00001371_2015-04-21 07:53:56_314295750751242/info:user_id/1558185213056/Put/vlen=8/seqid=0}))
	- element of array (index: 0)
	- array (class [Lscala.Tuple2;, size 5)
```



![1562923600121](/img/1562923600121.png)

```scala
		// TODO: 1、SparkContext实例对象，读取数据和调度Job执行
		val sc: SparkContext = SparkContext.getOrCreate(
			new SparkConf()
				.setMaster("local[4]").setAppName("SparkReadHBase")
				// TODO: 设置Spark Application序列化为Kryo方式, 默认对基本数据使用Kryo序列化，其他类需要注册
				.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
				// TODO: 表示如果有数据类型没有被注册使用Kryo序列化，程序将报错
		    	//.set("spark.kryo.registrationRequired", "true")
		    	// 注册使用Kryo序列化的类型（看RDD中数据类型如果不是基本数据类型，就需要注册）
		    	.registerKryoClasses(
					Array(
						classOf[ImmutableBytesWritable], classOf[Result]
					)
				)
		)
```







![1562926265938](/img/1562926265938.png)





## 课程附录

### 1、Maven工程pom.xml内容

​		基于使用Kafka版本不同（Kafka Consumer API不同），导致Streaming集成Kafka时，依赖不同。

#### 1）、使用Kafka 0.8.2.1+ API集成

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
        <zookeeper.version>3.4.5-cdh5.14.0</zookeeper.version>
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

        <!-- Spark Streaming 与Kafka 0.8.2.1 集成依赖-->
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-streaming-kafka-0-8_${scala.binary.version}</artifactId>
            <version>${spark.version}</version>
        </dependency>

        <!-- Zookeeper Client 依赖 -->
        <dependency>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
            <version>3.4.5-cdh5.14.0</version>
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



#### 2）、使用Kafka 0.10.0+ API集成

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
        <zookeeper.version>3.4.5-cdh5.14.0</zookeeper.version>
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

        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-sql-kafka-0-10_${scala.binary.version}</artifactId>
            <version>${spark.version}</version>
        </dependency>

        <!-- Spark Streaming 依赖 -->
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-streaming_${scala.binary.version}</artifactId>
            <version>${spark.version}</version>
        </dependency>

        <!-- Spark Streaming 与Kafka 0.10.0 集成依赖-->
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-streaming-kafka-0-10_${scala.binary.version}</artifactId>
            <version>${spark.version}</version>

        </dependency>

        <!-- Zookeeper Client 依赖 -->
        <dependency>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
            <version>3.4.5-cdh5.14.0</version>
        </dependency>

        <!-- Hadoop Client 依赖 -->
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-client</artifactId>
            <version>${hadoop.version}</version>
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



### 2、模拟订单数据

​		模拟数据：订单数据，实时产生，统计各个省份销售订单额

#### 1）、订单数据格式

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

  

#### 2）、Kafka Topic 操作命令

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





