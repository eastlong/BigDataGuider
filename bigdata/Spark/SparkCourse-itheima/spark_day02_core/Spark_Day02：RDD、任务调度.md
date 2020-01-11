---
stypora-copy-images-to: img
typora-root-url: ./
---



# Spark Day02：RDD、任务调度



## 一、课程回顾与内容提纲



### 1、昨日课程内容回顾

​		参考XMIND思维导图

### 2、今日课程内容提纲

​		今天主要以RDD为主，讲解RDD是什么及RDD中函数使用（如何使用函数处理分析数据）：

- RDD是什么？？？（掌握）
- RDD创建（掌握）
- RDD Operations（掌握）：三类函数
  - Transformation函数：转换函数，当RDD使用此函数以后转换为另外一个RDD，比如：map、flatMap、reduceByKey、filter、groupByKey等等
  - Action函数：行为函数，当RDD调用此类函数以后，要么返回一个值（返回给Driver，非RDD），要么不返回值，比如：first，take，collect，foreach，saveAsTextFile等等
  - Persist函数：缓存函数，将RDD数据缓存到内存或磁盘中
- Spark 案例分析（掌握）
  - 点击流日志分析：PV、UV、Refer TOPKey
  - IP 地址统计分析：ip地址找到对应经度和维度信息，统计各个区域出现次数，最终保存到MySQL表中
- Spark Scheduler（知道）
  - RDD 依赖
  - RDD lineage
  - RDD DAG
  - Stage(Shuffle)
  - Task

- Spark 知识点（知道）
  - 常见函数：join、聚合函数、分区函数
  - 查看源码





## 二、RDD是什么



### 1、官方定义

​		RDD全称：**Resilient Distributed Dataset** 

```
Represents an immutable, partitioned collection of elements that can be operated on in parallel with failure recovery possibilities。
```

- 不可变：RDD集合不能改变的集合，改变 -> 删除元素、增加元素
- 分区（Partition）：分区的集合

![1562462795984](/img/1562462795984.png)

- 并行操作：可以对每个分区数据进行处理，并行的处理分析

- 容灾恢复：RDD中某个分区数据丢失以后，可以恢复，RDD依赖关系。



### 2、RDD 五个特性

```
Internally, each RDD is characterized by five main properties:
 *  - A list of partitions
 	表示：每个RDD有一些列的分区组成
 	protected def getPartitions: Array[Partition]
 *  - A function for computing each split
 	表示：每个分区的数据可以使用一个函数处理分析，每个分区数据被一个Task处理分析
 	def compute(split: Partition, context: TaskContext): Iterator[T]
 *  - A list of dependencies on other RDDs
 	表示：一个RDD依赖于一系列的RDD
 	protected def getDependencies: Seq[Dependency[_]] = deps
 *  - Optionally, a Partitioner for key-value RDDs (e.g. to say that the RDD is hash-partitioned)
 	可选的特性：针对RDD是（Key, Value)类型数据，可以设置分区器Partitioner，默认值哈希分区器
 	@transient val partitioner: Option[Partitioner] = None
 *  - Optionally, a list of preferred locations to compute each split on (e.g. block locations for an HDFS file)
 	可选的特性：数据本地性，计算每个分区数据的时候，可以找数据存储的最优位置
```

扩展：

- 一个分区的数据被一个Task处理，一个Task运行需要1Core CPU，以线程Thread方式运行在Executor中。

- 可以通过TaskContext.partitionId函数获取RDD中每个分区ID
- 数据本地性：

![1562464618930](/img/1562464618930.png)

- 通过WordCount词频统计，查看其中RDD（源码分析）

![1562465074383](/img/1562465074383.png)



## 三、RDD 创建

![1562465565428](/img/1562465565428.png)

RDD创建，主要有两种方式：

- 第一种方式：读取外部存储系统的数据

  - HDFS（LocalFS）上数据

    ```scala
      def textFile(
          path: String,
          // 表示RDD的分区数目，默认值为最小分数目
          minPartitions: Int = defaultMinPartitions): RDD[String] = withScope {
        assertNotStopped()
        hadoopFile(path, classOf[TextInputFormat], classOf[LongWritable], classOf[Text],
          minPartitions).map(pair => pair._2.toString).setName(path)
      }
    ```

    ​		在实际运行中，如果从HDFS上数据，数据的block数目就是RDD的分区数目。

  - NoSQL数据库数据：目前来说很多NoSQL数据库都自身提供API，Spark可以读写其中数据
    -  MongoDB
      - https://github.com/mongodb/mongo-spark
      - https://docs.mongodb.com/spark-connector/current/
      - https://docs.mongodb.com/spark-connector/current/scala/read-from-mongodb/
    - ES
      - SHSE：https://github.com/SHSE/spark-es
      - 官方：https://www.elastic.co/guide/en/elasticsearch/hadoop/current/spark.html

- 第二种方式：并行化集合，此种方式通常用于测试某个功能（函数的使用）

```scala
  def parallelize[T: ClassTag](
      // 表示Scala中集合，都是Seq子类，比如List列表和数组Array
      seq: Seq[T],
      // 设置RDD分区数目
      numSlices: Int = defaultParallelism): RDD[T] = withScope {
    assertNotStopped()
    new ParallelCollectionRDD[T](this, seq, numSlices, Map[Int, Seq[String]]())
  }
```

案例：

```scala
val numbers = (1 to 10).toList

val numRDD = sc.parallelize(numbers)
```



## 四、RDD Operations	

​		![1562481606458](/img/1562481606458.png)

### 1、函数介绍

​		在RDD中对其操作函数，主要分为三大类：分别为Transformation、Action和Persis函数。

![1562467848935](/img/1562467848935.png)

- 第一类函数：转换函数Transformation

  - 使用此函数将一个RDD转换为另外一个RDD
  - 转换函数都是lazy操作，大概80个以上

  ![1562468096783](/img/1562468096783.png)

- 第二类函数：Action函数

  - 此类函数将会触发一个Job执行
  - 当RDD调用此类函数以后，要么返回一个值到Driver Program，要么保存数据到外部存储系统
  - Action函数式Eager，立即执行

  ![1562468117958](/img/1562468117958.png)

- 第三函数：持久化函数

  - 将RDD缓存到内存或者磁盘中



针对MapReduce 编程来说，API有两种，分界线版本：0.20.0
	- 旧API
		org.apache.hadoop.mapred
	- 新API
		org.apache.hadoop.mapreduce
	



### 2、特殊函数使用



#### 1）、xxPartition函数

​		企业的实际开发中，不建议对RDD中每个数据进行操作，而是建议**对RDD中每个分区的数据进行操作**。

```scala
  // 函数定义来看，针对RDD中每个元素操作的，有返回值
  def map[U: ClassTag](f: T => U): RDD[U] = withScope {
    val cleanF = sc.clean(f)  // 闭包函数操作
    //  iter.map(cleanF) 表示将每个分区中的数据，一条一条的应用到 函数中
    new MapPartitionsRDD[U, T](this, (context, pid, iter) => iter.map(cleanF))
  }
  // 函数定义来看，针对RDD中每个元素操作的，没有返回值
  def foreach(f: T => Unit): Unit = withScope {
    val cleanF = sc.clean(f)
    sc.runJob(this, (iter: Iterator[T]) => iter.foreach(cleanF))
  }
```

​		查看mapPartitions或foracheParitions函数：

```scala
  def mapPartitions[U: ClassTag](
      f: Iterator[T] => Iterator[U],
      preservesPartitioning: Boolean = false): RDD[U] = withScope {
    val cleanedF = sc.clean(f)
    new MapPartitionsRDD(
      this,
      // iter: Iterator[T]) => cleanedF(iter) ,将每个分区中的数据传给函数处理，一个分数据调用一个函数 
      (context: TaskContext, index: Int, iter: Iterator[T]) => cleanedF(iter),
      preservesPartitioning)
  }

  def foreachPartition(f: Iterator[T] => Unit): Unit = withScope {
    val cleanF = sc.clean(f)
    sc.runJob(this, (iter: Iterator[T]) => cleanF(iter))
  }
```

​		综上所述：建议使用mapPartitions和foreachPartition针对RDD中每个分区数据进行操作。

```scala
// Iterator表示的每个分区的数据，封装在迭代器中
f: Iterator[T] => Iterator[U]
```



#### 2）、分区函数

​		对RDD中数据进行分区，改变RDD中分区数目。

- 增加分区数目，此函数将会产生Shuffle

```scala
  def repartition(numPartitions: Int)(implicit ord: Ordering[T] = null): RDD[T] = withScope {
    coalesce(numPartitions, shuffle = true) // 表示产生Shuffle操作
  }
```

- 降低分区数目，不会产生Shuffle

```scala
  // 默认情况下，此函数降低分区的时候，不产生Shuffle操作
  def coalesce(numPartitions: Int, shuffle: Boolean = false,
               partitionCoalescer: Option[PartitionCoalescer] = Option.empty)
              (implicit ord: Ordering[T] = null)
      : RDD[T] 
```

- 何时调整RDD的数目呢？？？

  - 增加分区数：当我们读取源端数据的时候，往往需要增加分区数目，源端的数据往往很大，需要快速的进行处理（ETL、过滤、转换等操作）；
  - 减少分区数：
    - 结果RDD输出的时候，建议考虑降低分区数目
    - 当对RDD数据使用filter类似过滤操作以后，需要考虑降低分数目

- 分区函数（PairRDDFunctions）：专门针对Key/Value类型的RDD进行分区操作的

  ```scala
    // 指定分区器，通过分区器重新对数据进行分区操作
    /**
     * Return a copy of the RDD partitioned using the specified partitioner.
     */
    def partitionBy(partitioner: Partitioner): RDD[(K, V)] = self.withScope {
      if (keyClass.isArray && partitioner.isInstanceOf[HashPartitioner]) {
        throw new SparkException("HashPartitioner cannot partition array keys.")
      }
      if (self.partitioner == Some(partitioner)) {
        self
      } else {
        new ShuffledRDD[K, V, V](self, partitioner)
      }
    }
  ```

  #### 3）、聚合函数

  ​		RDD中自带一些聚合函数，就是对RDD中数据进行聚合统计，类似Scala中列表List中聚合函数。

![1562471345494](/img/1562471345494.png)

​		**思路：聚合函数，往往在聚合的时候，聚合`中间临时变量`（几个变量，什么类型，依据需求而定）**

##### i、列表中聚合函数

![1562471641469](/img/1562471641469.png)

![1562471710293](/img/1562471710293.png)



##### ii、RDD中reduce函数

![1562472108869](/img/1562472108869.png)

![1562472062490](/img/1562472062490.png)



##### iii、RDD中aggregate函数

```scala
def aggregate[U: ClassTag]
// 聚合时中间临时变量的初始值，U也是聚合返回类型
(zeroValue: U)
(
    // 对每个分区数据聚合函数，U表示聚合的中间临时变量的值，T表示的是RDD中每个分区中元素
    seqOp: (U, T) => U, 
    // 对各个分区聚合的结果的聚合函数，第一个U表示聚合的中间临时变量的值，第二个U表示分区的聚合结果
    combOp: (U, U) => U
): U
```

代码演示：

```scala

		// 通过并行化创建RDD
		val numbersRDD: RDD[Int] = sc.parallelize(
			(1 to 10).toList,
			numSlices = 6
		)

		// TODO: 打印RDD中各个分区的数据
		numbersRDD.foreachPartition{ datas =>
			println(s"p-${TaskContext.getPartitionId()}, datas: ${datas.mkString(", ")}")
		}


		// 使用RDD中aggregate函数进行聚合操作
		/*
		 def aggregate[U: ClassTag]
		 (zeroValue: U)
		 (
		 	seqOp: (U, T) => U,
		 	combOp: (U, U) => U
		 ): U
		 */
		val resultAgg: Int = numbersRDD.aggregate(0)(
			// seqOp: (U, T) => U, 针对每个分区数据聚合函数
			(u: Int, item: Int) => {
				println(s"seqOp: p-${TaskContext.getPartitionId()}, $u + $item = ${u + item}")
				u + item
			},
			// combOp: (U, U) => U
			(u1: Int, u2: Int) => {
				println(s"combOp: p-${TaskContext.getPartitionId()}, $u1 + $u2 = ${u1 + u2}")
				u1 + u2
			}
		)

		println(s"ResultAgg = $resultAgg")
```



### 3、RDD 缓存

![1562481638231](/img/1562481638231.png)

#### 1）、缓存函数

​		调用RDD中persist函数进行缓存RDD的数据，到Executor内存或者磁盘中。

```scala
  def persist(newLevel: StorageLevel): this.type = {
    if (isLocallyCheckpointed) {
      // This means the user previously called localCheckpoint(), which should have already
      // marked this RDD for persisting. Here we should override the old storage level with
      // one that is explicitly requested by the user (after adapting it to use disk).
      persist(LocalRDDCheckpointData.transformStorageLevel(newLevel), allowOverride = true)
    } else {
      persist(newLevel, allowOverride = false)
    }
  }

 // 下面两个函数，表示将数据直接放到Executor内存中，使用考虑内存是否充分，或者数据是否太大
  /**
   * Persist this RDD with the default storage level (`MEMORY_ONLY`).
   */
  def persist(): this.type = persist(StorageLevel.MEMORY_ONLY)

  /**
   * Persist this RDD with the default storage level (`MEMORY_ONLY`).
   */
  def cache(): this.type = persist()
```



#### 2）、缓存级别	

​		对RDD的数据缓存，可以设置相应级别，

```scala
  // 不缓存
  val NONE = new StorageLevel(false, false, false, false)
  // 缓存到数据磁盘中（本地文件系统的磁盘），设置副本数
  val DISK_ONLY = new StorageLevel(true, false, false, false)
  val DISK_ONLY_2 = new StorageLevel(true, false, false, false, 2)
  // 缓存数据到内存中，可以设置副本数，是否序列化（序列化以后减少使用内存）
  val MEMORY_ONLY = new StorageLevel(false, true, false, true)
  val MEMORY_ONLY_2 = new StorageLevel(false, true, false, true, 2)
  val MEMORY_ONLY_SER = new StorageLevel(false, true, false, false)
  val MEMORY_ONLY_SER_2 = new StorageLevel(false, true, false, false, 2)
  // 缓存数据到内存和磁盘，当内存不足的时候放到磁盘中，可以设置副本数，是否序列化
  val MEMORY_AND_DISK = new StorageLevel(true, true, false, true)
  val MEMORY_AND_DISK_2 = new StorageLevel(true, true, false, true, 2)
  val MEMORY_AND_DISK_SER = new StorageLevel(true, true, false, false)
  val MEMORY_AND_DISK_SER_2 = new StorageLevel(true, true, false, false, 2)
  // 缓存数据到操作系统的内存中
  val OFF_HEAP = new StorageLevel(true, true, true, false, 1)
```

​		实际项目中常常选用MEMORY_AND_DISK_2和MEMORY_AND_DISK_SER_2。



#### 3）、释放资源，不缓存数据

​		调用RDD中unpersist函数，其中的参数表示的是，是否阻塞。

```scala
  def unpersist(blocking: Boolean = true): this.type = {
    logInfo("Removing RDD " + id + " from persistence list")
    sc.unpersistRDD(id, blocking)
    storageLevel = StorageLevel.NONE
    this
  }
```

​		当RDD不使用的时候，一定要释放资源。

#### 4）、什么时候缓存RDD数据

​		通常在如何两种情况下，建议将RDD数据进行缓存操作：

- 当某个RDD被重复使用多次，比如从HDFS读取数据，此RDD被多次使用，建议缓存
- 当某个RDD的数据来之不易（从源数据读取，经过ETL和Join关联分析，漫长过程），并且使用不止一次，建议将此RDD进行缓存操作。



#### 5）、RDD缓存注意事项

​		RDD数据缓存的操作属于Lazy操作，通常使用`RDD#count`函数触发执行。



### 4、Checkpoint（了解）

​		RDD的数据设置检查点目录，可以将数据保存到目录中，用于恢复，往往Job在执行的时候，会将RDD进行Checkpoint操作。

![1562481904296](/img/1562481904296.png)

![1562482288405](/img/1562482288405.png)

![1562482338460](/img/1562482338460.png)



RDD中缓存与Checkpoint区别：

- RDD的缓存将数据存储到内存或本地文件系统中，然而Checkpoint将RDD存储到可靠地文件系统中（HDFS）
- RDD缓存以后，依然保存着RDD的依赖关系；但是Checkpoint以后，不会保存RDD依赖关系。





## 五、Spark 案例应用



### 1、**点击流日志分析案例**

![1562484055162](/img/1562484055162.png)

### 2、IP 地址统计分析

![1562488688921](/img/1562488688921.png)





## 六、共享变量

官方定义：

```
A second abstraction in Spark is shared variables that can be used in parallel operations. 
共享变量能过并行的操作。
```

Spark 中支持两种类型共享变量：

```
第一个：broadcast variables, which can be used to cache a value in memory on all nodes
	将数据缓存到Executor中，而不是将数据缓存到Task中。

第二个： accumulators, which are variables that are only “added” to, such as counters and sums.
	计数器就相当于MapReduce框架中Counters，就是启动计数功能。
```

### 1、广播变量

​	未使用广播变量

![1562489856641](/img/1562489856641.png)

使用广播变量

![1562489935936](/img/1562489935936.png)

​		Spark 中广播变量还可以进行Map Join操作（思考：Hive中如果一个大表和一个小表数据关联分析）。

调用SparkContext中函数将数据广播出去：

```scala
def broadcast[T: ClassTag](value: T): Broadcast[T]
```

​		广播变量的数据类型不能是RDD，通常是Array或者Map集合。

获取广播变量的值：

```scala
broadcast.value
```



### 2、计数器

![1562490068363](/img/1562490068363.png)

案例演示：

![1562490239132](/img/1562490239132.png)

![1562490287889](/img/1562490287889.png)



## 七、PairRDDFunctions中聚合函数

​		在PairRDDFunctions中函数，基本上都是对Key/Value类型的RDD进行操作的，主要依据Key进行操作，其中包含一些列分组聚合函数：

![1562490457763](/img/1562490457763.png)

- 第一个分组函数：

![1562490518951](/img/1562490518951.png)

```scala
def groupByKey(): RDD[(K, Iterable[V])] 
```

​		按照Key对数据进行分组操作，不进行`”聚合操作“`，在实际开发能不使用就不使用。

- 第二个分组聚合函数：

![1562490664346](/img/1562490664346.png)

```scala
def reduceByKey(func: (V, V) => V): RDD[(K, V)]
```

​		首先按照Key分组，再对分组后的Values进行聚合操作（局部聚合、全局聚合）。

![1562490748977](/img/1562490748977.png)

​	foldByKey分组聚合函数与reduceByKey类似，只不过可以设置聚合时中间临时变量的初始值而已

```scala
def foldByKey(zeroValue: V)(func: (V, V) => V): RDD[(K, V)] 
```

- 第三个分区聚合函数：

![1562490832273](/img/1562490832273.png)

​		在实际项目开发中，此函数被用于分组聚合最多，性能很好，与RDD中aggregate函数差不多。

```scala
def aggregateByKey[U: ClassTag]
// 表示的是聚合时中间临时变量的初始值
(zeroValue: U)
(
    seqOp: (U, V) => U,
    combOp: (U, U) => U
): RDD[(K, U)] 
```

- 第四个分组聚合函数：

![1562490963152](/img/1562490963152.png)

​		此函数属于Spark RDD中聚合的底层函数，使用比较灵活，但是很复杂。

```scala
  def combineByKey[C](
      // 创建一个聚合器
      createCombiner: V => C,
      // 对RDD中各个分区数据进行聚合的函数
      mergeValue: (C, V) => C,
      // 对各个分区聚合结果进行聚合的函数
      mergeCombiners: (C, C) => C
  ): RDD[(K, C)] 
```

通过源码发现，上述四类分组聚合函数最终调用的是如下函数：

```scala
  def combineByKeyWithClassTag[C](
      createCombiner: V => C,
      mergeValue: (C, V) => C,
      mergeCombiners: (C, C) => C,
      partitioner: Partitioner,
      mapSideCombine: Boolean = true,  // 是否局部聚合
      serializer: Serializer = null
  )(implicit ct: ClassTag[C]): RDD[(K, C)] 
```



![1562491289902](/img/1562491289902.png)





## 八、Spark Scheduler

​		在Spark Application中一个Job是如何执行的？？？



### 1、RDD 依赖

​		在Spark中RDD数据结构，有一个特性：每个RDD依赖一系列RDD，依赖分为两种，

- **窄依赖**：父RDD每个分区数据经过Transformation函数以后到子RDD的一个分区中（一对一）

![1562493344920](/img/1562493344920.png)



- **宽依赖**：父RDD每个分区数据经过Transformation函数以后到子RDD的多个分区中（一对多）

![1562493405292](/img/1562493405292.png)



### 2、RDD lineage(RDD 血缘)

​		每个RDD都依赖一系列的RDD，在一个Job中，可以从一个RDD得到所有依赖RDD。

```scala
rdd.toBDebugString
```

![1562493532419](/img/1562493532419.png)



### 3、RDD DAG

​		一个Job，依据RDD的依赖（RDD lineage）构建一个DAG图。

![1562493576101](/img/1562493576101.png)

### 4、Stage

​		当调度一个Job执行的时候，首先依据resultRDD（结果RDD）的依赖关系，得到一个DAG图，然后将DAG图划分为Stage（阶段）：划分的依据就是RDD之间的依赖如果是宽依赖就进行划分。

![1562493851207](/img/1562493851207.png)



### 5、Task

![1562494252963](/img/1562494252963.png)

![1562494261095](/img/1562494261095.png)



## 九、Spark Shuffle

  	![1562494372430](/img/1562494372430.png)



发展历史：

![1562495006361](/img/1562495006361.png)





​		Spark有两种Shuffle机制：一种是基于Hash的Shuffle，一种是基于Sort的Shuffle。在Shuffle机制转变的过程中, 主要的一个优化点就是产生的小文件个数。

### 1）、Hash-based

![1561687303716](/../../SparkBjWare/spark_day02_core/03_%E7%AC%94%E8%AE%B0/img/1561687303716.png)

- hash-based的Shuffle， 每个map会根据reduce的个数创建对应的bucket， 那么bucket的总数量是: M * R (map的个数 * reduce的个数)。
- 假如分别有1k个map和reduce,将产生1百万的小文件！如上图所示,2个core, 4个map task, 3个reduce task 产生了4*3 = 12个小文件(每个文件中是不排序的)。



### 2）、**Hash-based 优化**

![1561687710637](/../../SparkBjWare/spark_day02_core/03_%E7%AC%94%E8%AE%B0/img/1561687710637.png)

- 由于hash-based产生的小文件太多, 对文件系统的压力很大, 后来做了优化. 
  把同一个core上的多个map输出到同一个文件. 这样文件数就变成了 core * R个。
- 2个core, 4个map task, 3个 reduce task, 产生了2*3 = 6个文件，每个文件中仍然不是排序的。



### 3）、Sort-based

![1561688026927](/../../SparkBjWare/spark_day02_core/03_%E7%AC%94%E8%AE%B0/img/1561688026927.png)

- 由于优化后的hash-based Shuffle的文件数为: core * R， 产生的小文件仍然过大,， 所以引入了 sort-based Shuffle。
- sort-based Shuffle中，一个map task 输出一个文件，文件在一些到磁盘之前，会根据key进行排序. 排序后, 分批写入磁盘，task完成之后会将多次溢写的文件合并成一个文件。
- 由于一个task只对应一个磁盘文件，因此还会单独写一份索引文件，标识下游各个task的数据对应在文件中的起始和结束offset。



![1562495052883](/img/1562495052883.png)



## 十、Spark Internal

![1562495229105](/img/1562495229105.png)

Spark Job调度：

![1562495303528](/img/1562495303528.png)

![1562495597334](/img/1562495597334.png)

![1562495654282](/img/1562495654282.png)

Spark的任务调度总体来说分两路进行，一路是**Stage级的调度**，一路是**Task级的调度**。

![1562495849644](/img/1562495849644.png)