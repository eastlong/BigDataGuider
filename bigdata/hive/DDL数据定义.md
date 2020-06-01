# 创建表

## 管理表（内部表）

默认创建的表都是所谓的**管理表**，有时也被称为**内部表**。因为这种表，Hive会（或多或少地）控制着数据的生命周期。Hive默认情况下会将这些表的数据存储在由配置项hive.metastore.warehouse.dir(例如，/user/hive/warehouse)所定义的目录的子目录下。	当我们**删除一个管理表时，Hive也会删除这个表中数据**。管理表不适合和其他工具共享数据。

## 外部表

### 理论

因为表是外部表，所以**Hive并非认为其完全拥有这份数据**。**删除该表并不会删除掉这份数据，不过描述表的元数据信息会被删除掉**。

### 使用场景

每天将收集到的网站日志定期流入HDFS文本文件。在**外部表（原始日志表**）的基础上做大量的统计分析，用到的中间表、结果表使用内部表存储，数据通过SELECT+INSERT进入内部表。

``` sql
-- step1: 建表语句
create external table if not exists learn.dept(
deptno int,
dname string,
loc int
)
row format delimited fields terminated by '\t';

create external table if not exists learn.emp(
empno int,
ename string,
job string,
mgr int,
hiredate string, 
sal double, 
comm double,
deptno int)
row format delimited fields terminated by '\t';

-- step2:向外部表中导入数据
load data local inpath '/home/hadoop/file/hive/dept.txt' into table learn.dept;

load data local inpath '/home/hadoop/file/hive/emp.txt' into table learn.emp;

-- step3:查看表格式化数据
desc formatted dept;
-- Table Type:             EXTERNAL_TABLE
```

## 管理表与外部表的互相转换

``` sql
-- 查询表的类型
desc formatted student2;
-- Table Type:             MANAGED_TABLE

-- 修改内部表student2为外部表
alter table student2 set tblproperties('EXTERNAL'='TRUE');

-- 查询表的类型
desc formatted student2;
-- Table Type:             EXTERNAL_TABLE

-- 修改外部表student2为内部表
alter table student2 set tblproperties('EXTERNAL'='FALSE'); 

-- 查询表的类型
desc formatted student2;
-- Table Type:             MANAGED_TABLE
```
