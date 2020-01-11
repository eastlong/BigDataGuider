---
stypora-copy-images-to: img
typora-root-url: ./
---



## Spark Day03：DataFrames、Datasets and SQL



## 一、课程回顾与内容提纲



### 1、昨日课程内容回顾

​		参考总结思维导图

### 2、今日课程内容提纲

​	SparkSQL模块，主要针对结构化数据进行处理分析，将要处理的数据封装在：Dataset（DataFrame）

- Spark SQL 模块概述（知道）
  - SparkSQL 前世今生
  - Spark SQL优势，初步使用
- 数据结构Dataset/DataFrame是什么（掌握）
  - Dataset/DataFrame = RDD+ Schema (字段类型和字段名称)
  - DataFrame、Dataset、RDD之间关系及相互转换
- External DataSource（外部数据源）（掌握）
  - 从Spark 1.4开始提供一套API接口，使得SparkSQL可以从任意的地方读取数据，封装为DataFrame处理
  - 文件（text、csv、json、Parquet、orc）、表（RDMBS、hive、hbase）及NoSQL数据库等
- 分析数据（两种方式）：（掌握）
  - SQL，类似HivQL语句分析处理，95%以上都支持（UDF、UDAF和UDTF）
  - DSL，调用Dataset API（类似RDD中API调用，链式编程）
- 交互式分析引擎（知道）
  - 提供交互式SQL命令行spark-sql
  - 提供将应用当做服务启动ThriftServer2（HiveServer2），beeline客户端或JDBC Client
- Spark SQL中函数（掌握）
  - 自带函数（300个函数）
  - 自定义函数UDF、UDAF
  - 高级函数使用（窗口函数、分析函数、分组聚合函数、聚合函数在OVER语句）等使用

- Spark SQL Catalyst（了解）



Hive 框架底层分析引擎：（从Hive 2.0开始，官方推荐使用Spark或Tez作为分析引擎）

- 原始的分析引擎：MapReduce
- 内存计算分区引擎：SparkCore
- Hortonworks分析引擎：Tez



## 二、Spark SQL概述



### 1、Spark SQL 前世今生



#### 1）、Hive框架架构

![1562635224286](/img/1562635224286.png)



#### 2）、Shark（Spark 1.0之前）

​		将Hive框架源码，修改其中将SQL转换MapReduce代码为转换Spark RDD操作。

![1562635345343](/img/1562635345343.png)

最终Shark框架：

![1562635457144](/img/1562635457144.png)



可以发现问题：

- Shark模块框架太依赖于Hive框架，更多在于维护（Hive升级，Shark升级；SparkCore升级，Shark升级）
- 没有更多精力去优化Shark框架，提升性能

从Spark 1.x开始，废弃Shark模块，SparkSQL模块诞生了：编写引擎Catalyst引擎。



#### 3）、Catalyst

​		在Spark 1.x版本中，Spark框架重点发展的模块就是SparkSQL。

![1562635740637](/img/1562635740637.png)



#### 4）、发展史

![1562635906346](/img/1562635906346.png)

### 2、Spark SQL定义

![1562636645984](/img/1562636645984.png)

![1562636780582](/img/1562636780582.png)

​		从Spark 2.0开始，SparkSQL中出现SparkSession类，程序入口，用于读取数据，封装到DataFrame中，底层还是SparkContext。

​		在Spark 1.x中SparkSQL中程序入口为SQLContext（HiveContext），底层也是SparkContext。

```scala
Spark session available as 'spark'.
```





### 3、Spark SQL初步使用

![1562637067672](/img/1562637067672.png)



准备数据文件：SPARK框架自带的

​		${SPARK_HOME}/examples/src/main/resources/

![1562637163057](/img/1562637163057.png)

将数据文件上传到HDFS目录/datas下：

```
# /export/servers/hadoop/bin/hdfs dfs -put resources /datas/
```

![1562637229701](/img/1562637229701.png)

- 读取文本文件数据text：

![1562637391725](/img/1562637391725.png)

- 读取JSON格式数据

![1562637499590](/img/1562637499590.png)

使用DSL编程：

![1562637700661](/img/1562637700661.png)

注册DataFrame为临时视图，编写SQL操作：

![1562637817431](/img/1562637817431.png)

对数据分析两种方式（求取最大工资和平均工资）：

![1562637982199](/img/1562637982199.png)

![1562637988299](/img/1562637988299.png)

![1562638039371](/img/1562638039371.png)





## 三、Dataset/DataFrame是什么



### 1、创建Module

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
        <hive.version>1.1.0-cdh5.14.0</hive.version>
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
        <!-- Spark SQL 与 Hive 集成 依赖 -->
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-hive_${scala.binary.version}</artifactId>
            <version>${spark.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-hive-thriftserver_${scala.binary.version}</artifactId>
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



### 2、使用SparkSession词频统计

```scala
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

/**
  * 使用SparkSession读取数据，进行词频统计
  */
object SparkSessionWordCount {

	// TODO： Spark 2.0开始，提供新的程序的入口SparkSession，底层SparkContex
	def main(args: Array[String]): Unit = {

		// 创建SparkSession实例对象，使用的建造者模式
		val spark = SparkSession.builder()
			.appName("SparkSessionWordCount")
	    	.master("local[4]")
			.config("spark.eventLog.enabled", "true")
			.config("spark.eventLog.dir", "hdfs://bigdata-cdh01.itcast.cn:8020/spark/eventLogs/")
			.config("spark.eventLog.compress", "true")
			.getOrCreate()
		spark.sparkContext.setLogLevel("WARN")

		// For implicit conversions like converting RDDs to DataFrames
		import spark.implicits._


		// TODO: 使用SparkSession读取数据
		val inputDS: Dataset[String] = spark.read.textFile("datas/wordcount/input/wordcount.data")

		inputDS.printSchema()
		inputDS.show(10)

		println("===================================================================")

		val wordcountDF: DataFrame = inputDS
			.flatMap(line => line.split("\\s+").filter(word => null != word && word.length > 0))
	    	.groupBy("value").count()

		wordcountDF.printSchema()
		wordcountDF.show()


		Thread.sleep(10000000)

		// 应用程序结束，关闭
		spark.stop()
	}

}
```



### 3、建议阅读源码

![1562639465670](/img/1562639465670.png)





### 4、DataFrame

​		Spark SQL模块提供数据结构，封装要处理的结构化数据，等价与DataFrame = RDD + Schema。

![1562640377448](/img/1562640377448.png)

其中Schema封装在

```
StructType
	|- StructField
		|- name
		|- type
		|- nullable
# 结构化字段		
StructField(name,StringType,true)
# 结构化类型
StructType(StructField(name,StringType,true), StructField(salary,LongType,true))
```

​		在DataFrame中每行数据就是一个Row，弱类型，泛指结构，类似于数组或者列表。

![1562640693523](/img/1562640693523.png)

​		如何获取Row中数据呢？？

```
# 第一种方式：下标方式，类似数组，获取第一个字段
row(0)   

# 第二中方式：Map方式
row.get(0)

# 第三方式：指定数据类型
row.getString(0)
```

![1562640913536](/img/1562640913536.png)



### 5、Dataset

​		可以认为Dataset就是RDD 与Data'Frame，如下图所示：

![1562641270418](/img/1562641270418.png)

![1562641399763](/img/1562641399763.png)



![1562641454998](/img/1562641454998.png)

### 6、RDD、DataFrame和Dataset转换

![1562641546675](/img/1562641546675.png)

![1562641603504](/img/1562641603504.png)



![1562641772601](/img/1562641772601.png)

如何将Data'Frame/Dataset数据转换为JSON格式数据：

![1562641981460](/img/1562641981460.png)



### 7、为什么使用Datatset/DataFrame

![1562642124725](/img/1562642124725.png)



![1562642128964](/img/1562642128964.png)

![1562642149259](/img/1562642149259.png)



## 四、外部数据源

​		在Spark 1.4版本以后，提供一套外部数据接口（read、write）统一，读写数据。

![1562642952971](/img/1562642952971.png)

### 1、读取数据

​		使用SparkSession#read读取数据

```scala
def read: DataFrameReader = new DataFrameReader(self)
```

![1562642406901](/img/1562642406901.png)

读取数据标准格式：

```scala
spark.read
	.format()  // 指定从哪里读取数据，数据源格式，比如jdbc、json、parquet等
	.schema()  // 指定Schema信息，字段名称，字段类型，是否可以为空
	.option()  // 指定可选参数，比如读取数据的路径path，比如读取数据库的配置信息，jdbcUrl，user等
	.load()  // 表示加载数据
```



### 2、写出数据

​		将结果数据Dataset/DataFrame保存到外部存储系统，调用的Dataset#write：

```scala
def write: DataFrameWriter[T] = {
  if (isStreaming) {
    logicalPlan.failAnalysis(
      "'write' can not be called on streaming Dataset/DataFrame")
  }
  new DataFrameWriter[T](this)
}
```

![1562642733417](/img/1562642733417.png)

输出数据标准格式：

```
dataset/dataframe.write
	.format()  // 输出数据到哪里，指定格式
	.mode() // 表示保存数据模式
	.option() // 指定输出格式选项，比如路径path
	.save()  // 进行保存数据
```



保存数据模式SaveMode：

```scala
  /**
   * Specifies the behavior when data or table already exists. Options include:
   *   - `SaveMode.Overwrite`: overwrite the existing data.
   *   - `SaveMode.Append`: append the data.
   *   - `SaveMode.Ignore`: ignore the operation (i.e. no-op).
   *   - `SaveMode.ErrorIfExists`: default option, throw an exception at runtime.
   *
   * @since 1.4.0
   */
  def mode(saveMode: SaveMode): DataFrameWriter[T] = {
    this.mode = saveMode
    this
  }

  /**
   * Specifies the behavior when data or table already exists. Options include:
   *   - `overwrite`: overwrite the existing data.
   *   - `append`: append the data.
   *   - `ignore`: ignore the operation (i.e. no-op).
   *   - `error`: default option, throw an exception at runtime.
   *
   * @since 1.4.0
   */
  def mode(saveMode: String): DataFrameWriter[T] = {
    this.mode = saveMode.toLowerCase(Locale.ROOT) match {
      case "overwrite" => SaveMode.Overwrite
      case "append" => SaveMode.Append
      case "ignore" => SaveMode.Ignore
      case "error" | "default" => SaveMode.ErrorIfExists
      case _ => throw new IllegalArgumentException(s"Unknown save mode: $saveMode. " +
        "Accepted save modes are 'overwrite', 'append', 'ignore', 'error'.")
    }
    this
  }
```

- overwrite：如果存在，覆写
- append：追加模式
- ignore：如果存在，忽略保存操作
- error：存在，即报错



### 3、ETL链式操作

​		在SparkSQL中，可以进行数据ETL操作，属于链式编程，简单方便。

![1562643084093](/img/1562643084093.png)





## 五、读取数据



### 1、读取列式存储parquet

​		在SparkSQL中默认读取数据文件格式为parquet

```scala
  // This is used to set the default data source
  val DEFAULT_DATA_SOURCE_NAME = buildConf("spark.sql.sources.default")
    .doc("The default data source to use in input/output.")
    .stringConf
    .createWithDefault("parquet")
```



### 2、读取CSV格式数据

```scala
 /**
   * Loads CSV files and returns the result as a `DataFrame`.
   *
   * This function will go through the input once to determine the input schema if `inferSchema`
   * is enabled. To avoid going through the entire data once, disable `inferSchema` option or
   * specify the schema explicitly using `schema`.
   *
   * You can set the following CSV-specific options to deal with CSV files:
   * <ul>
   // 表示文件中每行数据的各个字段之间的分隔符，默认为逗号
   * <li>`sep` (default `,`): sets the single character as a separator for each
   * field and value.</li>
   * <li>`encoding` (default `UTF-8`): decodes the CSV files by the given encoding
   * type.</li>
   * <li>`quote` (default `"`): sets the single character used for escaping quoted values where
   * the separator can be part of the value. If you would like to turn off quotations, you need to
   * set not `null` but an empty string. This behaviour is different from
   * `com.databricks.spark.csv`.</li>
   * <li>`escape` (default `\`): sets the single character used for escaping quotes inside
   * an already quoted value.</li>
   * <li>`comment` (default empty string): sets the single character used for skipping lines
   * beginning with this character. By default, it is disabled.</li>
   // 表示文件的首行时是否列名称，默认值为false
   * <li>`header` (default `false`): uses the first line as names of columns.</li>
   // 表示是否自动依据各列值推断数据类型，默认值为false
   * <li>`inferSchema` (default `false`): infers the input schema automatically from data. It
   * requires one extra pass over the data.</li>
   * <li>`ignoreLeadingWhiteSpace` (default `false`): a flag indicating whether or not leading
   * whitespaces from values being read should be skipped.</li>
   * <li>`ignoreTrailingWhiteSpace` (default `false`): a flag indicating whether or not trailing
   * whitespaces from values being read should be skipped.</li>
   * <li>`nullValue` (default empty string): sets the string representation of a null value. Since
   * 2.0.1, this applies to all supported types including the string type.</li>
   * <li>`nanValue` (default `NaN`): sets the string representation of a non-number" value.</li>
   * <li>`positiveInf` (default `Inf`): sets the string representation of a positive infinity
   * value.</li>
   * <li>`negativeInf` (default `-Inf`): sets the string representation of a negative infinity
   * value.</li>
   * <li>`dateFormat` (default `yyyy-MM-dd`): sets the string that indicates a date format.
   * Custom date formats follow the formats at `java.text.SimpleDateFormat`. This applies to
   * date type.</li>
   * <li>`timestampFormat` (default `yyyy-MM-dd'T'HH:mm:ss.SSSXXX`): sets the string that
   * indicates a timestamp format. Custom date formats follow the formats at
   * `java.text.SimpleDateFormat`. This applies to timestamp type.</li>
   * <li>`maxColumns` (default `20480`): defines a hard limit of how many columns
   * a record can have.</li>
   * <li>`maxCharsPerColumn` (default `-1`): defines the maximum number of characters allowed
   * for any given value being read. By default, it is -1 meaning unlimited length</li>
   * <li>`mode` (default `PERMISSIVE`): allows a mode for dealing with corrupt records
   *    during parsing. It supports the following case-insensitive modes.
   *   <ul>
   *     <li>`PERMISSIVE` : sets other fields to `null` when it meets a corrupted record, and puts
   *     the malformed string into a field configured by `columnNameOfCorruptRecord`. To keep
   *     corrupt records, an user can set a string type field named `columnNameOfCorruptRecord`
   *     in an user-defined schema. If a schema does not have the field, it drops corrupt records
   *     during parsing. When a length of parsed CSV tokens is shorter than an expected length
   *     of a schema, it sets `null` for extra fields.</li>
   *     <li>`DROPMALFORMED` : ignores the whole corrupted records.</li>
   *     <li>`FAILFAST` : throws an exception when it meets corrupted records.</li>
   *   </ul>
   * </li>
   * <li>`columnNameOfCorruptRecord` (default is the value specified in
   * `spark.sql.columnNameOfCorruptRecord`): allows renaming the new field having malformed string
   * created by `PERMISSIVE` mode. This overrides `spark.sql.columnNameOfCorruptRecord`.</li>
   * <li>`multiLine` (default `false`): parse one record, which may span multiple lines.</li>
   * </ul>
   * @since 2.0.0
   */
  @scala.annotation.varargs
  def csv(paths: String*): DataFrame = format("csv").load(paths : _*)
```



### 3、读取JSON格式数据



```JSON
{
	"id": "2615300544",
	"type": "PushEvent",
	"actor": {
		"id": 14672,
		"login": "rande",
		"gravatar_id": "",
		"url": "https://api.github.com/users/rande",
		"avatar_url": "https://avatars.githubusercontent.com/u/14672?"
	},
	"repo": {
		"id": 1095228,
		"name": "sonata-project/SonataAdminBundle",
		"url": "https://api.github.com/repos/sonata-project/SonataAdminBundle"
	},
	"payload": {
		"push_id": 588230764,
		"size": 2,
		"distinct_size": 2,
		"ref": "refs/heads/master",
		"head": "baec5363dc3f73912a37bb72cc06835afa9f1d38",
		"before": "07887dd6d515d21c88012670f2849d4ce00bb51b",
		"commits": [{
			"sha": "b722b11790191d72c92d1c3c4e0ae320bf95233b",
			"author": {
				"email": "55cd6082a4cd396c3f8428b124fba4306925fb04@gmail.com",
				"name": "Andrej Hudec"
			},
			"message": "Do not add item in menu if the route admin is disabled or LIST operation is not granted",
			"distinct": true,
			"url": "https://api.github.com/repos/sonata-project/SonataAdminBundle/commits/b722b11790191d72c92d1c3c4e0ae320bf95233b"
		}, {
			"sha": "baec5363dc3f73912a37bb72cc06835afa9f1d38",
			"author": {
				"email": "583eaba57026faea610644b601ef5935608a45fe@gmail.com",
				"name": "Thomas"
			},
			"message": "Merge pull request #2750 from pulzarraider/knp_menu_list_route_fix\n\nDo not add item in menu if the route list is disabled or LIST operation is not granted",
			"distinct": true,
			"url": "https://api.github.com/repos/sonata-project/SonataAdminBundle/commits/baec5363dc3f73912a37bb72cc06835afa9f1d38"
		}]
	},
	"public": true,
	"created_at": "2015-03-01T11:00:00Z",
	"org": {
		"id": 404157,
		"login": "sonata-project",
		"gravatar_id": "",
		"url": "https://api.github.com/orgs/sonata-project",
		"avatar_url": "https://avatars.githubusercontent.com/u/404157?"
	}
}
```



```scala
		val gitDF: Dataset[String] = spark.read.textFile("datas/json/2015-03-01-11.json.gz")

		import org.apache.spark.sql.functions._
		gitDF
			// def get_json_object(e: Column, path: String): Column
	    	.select(
				get_json_object($"value", "$.id").as("id"),
				get_json_object($"value", "$.type").as("type"),
				get_json_object($"value", "$.public").as("public"),
				get_json_object($"value", "$.created_at").as("created_at")
			)
			.show(10, truncate = false)

```



### 4、读取MySQL表的数据

​		通过DataFrameReader可以三星，有三种函数读取MySQL表中数据：

![1562657929738](/img/1562657929738.png)



#### 1）、单分区函数

​		从RDBMS表中读的数据封装的RDD中只有一个分区。

```scala
def jdbc(url: String, table: String, properties: Properties): DataFrame
```





#### 2）、指定分区数及指定分区列



```scala
  def jdbc(
      url: String,
      table: String,
      // 指定列名，按此列进行分区
      columnName: String,
      // 指定列值的 最小值
      lowerBound: Long,
      // 指定列值的 最大值
      upperBound: Long,
      // 分区数
      numPartitions: Int,
      connectionProperties: Properties
  ): DataFrame
```



#### 3）、使用Where Cause指定分区范围

```scala
  def jdbc(
      url: String,
      table: String,
      // 谓词下压
      predicates: Array[String],
      connectionProperties: Properties
  ): DataFrame
```



### 5、集成Hive

​		从SparkSQL发展历史来，来源于Hive，天然支持从Hive表中的读取数据。SparkSQL与Hive集成，就是读取Hive中MetaStore元数据，获取数据库，表等信息。

```
Configuration of Hive is done by placing your hive-site.xml, core-site.xml (for security configuration), and hdfs-site.xml (for HDFS configuration) file in conf/.
```

​		将配置文件放置`hive-site.xml`于SPARK_HOM/conf目前中即可（MySQL数据库驱动包）。

```xml
<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
        <property>
          <name>javax.jdo.option.ConnectionURL</name>
          <value>jdbc:mysql://bigdata-cdh01.itcast.cn:3306/itcastmetastore?createDatabaseIfNotE
xist=true&amp;characterEncoding=UTF-8</value>
          <description>JDBC connect string for a JDBC metastore</description>
        </property>

        <property>
          <name>javax.jdo.option.ConnectionDriverName</name>
          <value>com.mysql.jdbc.Driver</value>
          <description>Driver class name for a JDBC metastore</description>
        </property>

        <property>
          <name>javax.jdo.option.ConnectionUserName</name>
          <value>root</value>
          <description>username to use against metastore database</description>
        </property>

        <property>
          <name>javax.jdo.option.ConnectionPassword</name>
          <value>123456</value>
          <description>password to use against metastore database</description>
        </property>
</configuration>
```

启动spark-shell命令：

```
# bin/spark-shell --master local[2] --jars mysql-connector-java-5.1.27-bin.jar 
```

演示：
![1562660147254](/img/1562660147254.png)

![1562660192747](/img/1562660192747.png)

emp表与dept表进行关联分析：

```SQL
select e.empno, e.ename, e.sal, d.dname from emp e join dept d on e.deptno = d.deptno
```

![1562660332627](/img/1562660332627.png)

使用DSL读取Hive表的数据：

![1562660430619](/img/1562660430619.png)

![1562660440306](/img/1562660440306.png)

将empDF和deptDF进行关联分析：

![1562660618106](/img/1562660618106.png)

选取关联后字段的值：

![1562660635347](/img/1562660635347.png)







## 六、数据分析（SQL和DSL）

​		在SparkSQL中针对Dataset/DataFrame数据分析有两种方式，SQL和DSL编程。

```
 需求：统计各个电影平均评分，要求每个电影至少被评分1000次以上，获取电影评分最高的Top10电影信息
     电影平均评分及电影评分次数，先按照电影评分次数降序，再按照电影平均评分降序
     root
         |-- userId: string (nullable = true)
         |-- movieId: string (nullable = true)
         |-- rating: double (nullable = false)
         |-- timestamp: long (nullable = false)
  数据集：
  	datas/ml-100k/ratings.dat
```

![1562646716967](/img/1562646716967.png)

​		在SparkSQL中，如果聚合或Join产生Shuffle的时候，ShuffleRDD的分区数目默认值为200，在实际项目中依据具体需求与数据量进行调整。

![1562646813406](/img/1562646813406.png)

具体设置方式如下：

```scala
        // 创建SparkSession实例对象，使用的建造者模式
		val spark = SparkSession.builder()
			.appName("SparkSQLMLProcess")
			.master("local[2]")
			// TODO: 设置SparkSQL中Shuffle时分区数目
	    	.config("spark.sql.shuffle.partitions", "4")
			.getOrCreate()
```



## 七、RDD转换Dataset

官方文档：http://spark.apache.org/docs/2.2.0/sql-programming-guide.html#interoperating-with-rdds

​		将RDD转换为Dataset/DataFrame有两种方式：通过反射和自定义Schema。

![1562654088613](/img/1562654088613.png)

### 1、Inferring the Schema Using Reflection

![1562654096029](/img/1562654096029.png)



### 2、Programmatically Specifying the Schema

![1562655164789](/img/1562655164789.png)



## 八、分布式SQL引擎

​		官方文档：http://spark.apache.org/docs/2.2.0/sql-programming-guide.html#distributed-sql-engine

### 1、交互式SQL命令行

​		http://spark.apache.org/docs/2.2.0/sql-programming-guide.html#running-the-spark-sql-cli

```shell
# bin/spark-sql --master local[2]
```

运行命令：

```SQL
 select e.empno, e.ename, e.sal, d.dname from emp e join dept d on e.deptno = d.deptno ;
```



### 2、Thrift JDBC/ODBC server

​		此功能就是HiveServer2，将应用当做一个服务启动，提供Beeline和JDBC两种方式客户端，此功能依赖Hive，所以编译的时候，支持Hive。

![1562662693433](/img/1562662693433.png)

启动服务：

```shell
SPARK_HOME=/export/servers/spark-2.2.0-bin-2.6.0-cdh5.14.0
$SPARK_HOME/sbin/start-thriftserver.sh \
--hiveconf hive.server2.thrift.port=10000 \
--hiveconf hive.server2.thrift.bind.host=bigdata-cdh03.itcast.cn \
--master local[2]
```

通过beeline客户端，连接服务：

```
[root@bigdata-cdh03 spark-2.2.0-bin-2.6.0-cdh5.14.0]# bin/beeline 
Beeline version 1.2.1.spark2 by Apache Hive
beeline> !connect jdbc:hive2://bigdata-cdh03.itcast.cn:10000
Connecting to jdbc:hive2://bigdata-cdh03.itcast.cn:10000
Enter username for jdbc:hive2://bigdata-cdh03.itcast.cn:10000: itcast
Enter password for jdbc:hive2://bigdata-cdh03.itcast.cn:10000: ******
19/07/04 19:25:25 INFO jdbc.Utils: Supplied authorities: bigdata-cdh03.itcast.cn:10000
19/07/04 19:25:25 INFO jdbc.Utils: Resolved authority: bigdata-cdh03.itcast.cn:10000
19/07/04 19:25:25 INFO jdbc.HiveConnection: Will try to open client transport with JDBC Uri: jdbc:hive2://bigdata-cdh03.itcast.cn:10000
```

![1562662826190](/img/1562662826190.png)

可以将表的数据缓存cache：

![1562663021796](/img/1562663021796.png)



## 九、Spark SQL函数

​		SparkSQL在企业的使用，主要在两个方面，第一个方面就是数据仓库（提数需求：数据分析），通常需要使用函数（SQL模块自带的，也有自定义函数）；第二个方面，数据特征处理，机器学习。

### 1、自带函数

![1562663239860](/img/1562663239860.png)

在程序开发，往往导入functions对象：

```scala
import org.apache.spark.sql.functions._
```

![1562663301060](/img/1562663301060.png)



### 2、自定义函数

​		对于Spark SQL来说，目前仅仅支持自定义UDF和UDAF函数，不支持UDTF函数，但是如果是HIVE定义的UDF、UDAF及UDTF函数注册以后，SparkSQL可以使用。

![1562663367382](/img/1562663367382.png)



#### 1）、UDF函数

​		SparkSQL中关于UDF函数的定义有两种：

- 第一种，通过spark.udf方式定义，使用SQL语句中

```scala
		// TODO: 将某列值的字母转换为小写字母
		spark.udf.register(
			"to_lower_case", // 函数的名称
			// 定义函数
			(word: String) => {
				word.toLowerCase
			}
		)
		// 读取Hive中emp表的ename值
		spark.sql(
			"""
			  |SELECT ename, to_lower_case(ename) AS name FROM db_hive.emp
			""".stripMargin)
	    	.show(20, truncate = false)
		
```

- 第二种，functions.udf方式定义，使用DSL语句中

```scala
		// 将匿名函数赋值给变量
		val lowerFunc = (word: String) => {
			word.toLowerCase
		}

		// TODO: 使用functions中udf函数声明自定义函数
		val to_lower: UserDefinedFunction = udf(lowerFunc)
		// 在DSL中使用
		spark
			.table("db_hive.emp")
	    	.select($"ename", to_lower($"ename").as("name"))
			.show(20, truncate = false)

```



#### 2）UDAF（自定义聚合函数）

官方文档：http://spark.apache.org/docs/2.2.0/sql-programming-guide.html#aggregations

​		SparkSQL中UDAF有两种定义方式，主要在于数据结构有两种：DataFrame和Dataset。

##### Untyped User-Defined Aggregate Functions：

​		http://spark.apache.org/docs/2.2.0/sql-programming-guide.html#untyped-user-defined-aggregate-functions

**需求**：获取各个部门的平均工资

```
select deptno, avg(sal) as avg_sal from emp group by deptno ;
```

**分析：**

- 如何求取平均值呢？？？

  总和 (sal_total) / 总数 (sal_cnt) = 平均工资，确定聚合时中间临时变量有两个，类型也确定

- 分区内聚合？？？

  针对各个分区中的数据来说，同一分组内，只要出现一条数据，sal_cnt就加1，sal_total累加工资的和

- 分区间聚合（分区合并）？？

  针对各个分区聚合的临时变量的值进行合并

![1562665740857](/img/1562665740857.png)

















回顾一下，RDD中聚合函数（RDD#aggregate）：

- 聚合中临时变量确定及初始化操作

- 第一点：对RDD中各个分区数据进行聚合
- 第二点：将各个分区聚合的结果进行合并操作



![1562665253229](/img/1562665253229.png)





## 十、Spark SQL Catalyst

​		Spark框架从1.0版本开始，自己编写Catalyst 引擎，用于将SQL/DSL解析成逻辑计划（Logical Plan）。

![1562667421306](/img/1562667421306.png)



Catalyst引擎三步走：

```
select deptno, avg(sal) as avg_sal from emp group by deptno ;
```

![1562667543567](/img/1562667543567.png)

```xml
== Parsed Logical Plan ==
'Aggregate ['deptno], ['deptno, 'avg('sal) AS avg_sal#348]
+- 'UnresolvedRelation `emp`

== Analyzed Logical Plan ==
deptno: int, avg_sal: double
Aggregate [deptno#356], [deptno#356, avg(sal#354) AS avg_sal#348]
+- SubqueryAlias emp
   +- CatalogRelation `db_hive`.`emp`, org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe, [empno#349, ename#350, job#351, mgr#352, hiredate#353, sal#354, comm#355, deptno#356]

== Optimized Logical Plan ==
Aggregate [deptno#356], [deptno#356, avg(sal#354) AS avg_sal#348]
+- Project [sal#354, deptno#356]
   +- InMemoryRelation [empno#349, ename#350, job#351, mgr#352, hiredate#353, sal#354, comm#355, deptno#356], true, 10000, StorageLevel(disk, memory, deserialized, 1 replicas), `emp`
         +- HiveTableScan [empno#49, ename#50, job#51, mgr#52, hiredate#53, sal#54, comm#55, deptno#56], CatalogRelation `db_hive`.`emp`, org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe, [empno#49, ename#50, job#51, mgr#52, hiredate#53, sal#54, comm#55, deptno#56]

== Physical Plan ==
*HashAggregate(keys=[deptno#356], functions=[avg(sal#354)], output=[deptno#356, avg_sal#348])
+- Exchange hashpartitioning(deptno#356, 200)
   +- *HashAggregate(keys=[deptno#356], functions=[partial_avg(sal#354)], output=[deptno#356, sum#403, count#404L])
      +- InMemoryTableScan [sal#354, deptno#356]
            +- InMemoryRelation [empno#349, ename#350, job#351, mgr#352, hiredate#353, sal#354, comm#355, deptno#356], true, 10000, StorageLevel(disk, memory, deserialized, 1 replicas), `emp`
                  +- HiveTableScan [empno#49, ename#50, job#51, mgr#52, hiredate#53, sal#54, comm#55, deptno#56], CatalogRelation `db_hive`.`emp`, org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe, [empno#49, ename#50, job#51, mgr#52, hiredate#53, sal#54, comm#55, deptno#56]
```

![1562667860113](/img/1562667860113.png)

**DataFrames
and SQL** **share** **the
same optimization/execution pipeline**

![1562668144459](/img/1562668144459.png)