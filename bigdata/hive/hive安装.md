<!-- TOC -->

* [1. hive安装](#1-hive安装)
  + [1.1. Hive安装相关地址](#11-hive安装相关地址)
  + [1.2. hive安装步骤](#12-hive安装步骤)
    - [1.2.1. hive安装与配置](#121-hive安装与配置)
    - [1.2.2. hadoop集群配置](#122-hadoop集群配置)
    - [1.2.3. 安装mysql](#123-安装mysql)
  + [1.3. Hive元数据配置到MySql](#13-hive元数据配置到mysql)
    - [1.3.1. 安装驱动](#131-安装驱动)
    - [1.3.2. 配置Metastore到MySql](#132-配置metastore到mysql)
    - [1.3.3. hive安装的一些坑和注意点](#133-hive安装的一些坑和注意点)

<!-- /TOC -->

# 1. hive安装

## 1.1. Hive安装相关地址

1．Hive官网地址    
http://hive.apache.org/  
2．文档查看地址    
https://cwiki.apache.org/confluence/display/Hive/GettingStarted  
3．下载地址  
http://archive.apache.org/dist/hive/  
4．github地址    
https://github.com/apache/hive

## 1.2. hive安装步骤

### 1.2.1. hive安装与配置

1. 解压hive

[hadoop@hadoop101 app]$ tar -zxvf apache-hive-3.1.2-bin.tar.gz

2. 修改hive/conf目录下的 `hive-env.sh.template` 名称为 `hive-env.sh` 

``` sh
mv hive-env.sh.template hive-env.sh
```

3. 配置hive-env.sh文件

``` sh
# 配置HADOOP_HOME路径
export HADOOP_HOME=/home/hadoop/app/hadoop
# 配置HIVE_CONF_DIR路径
export HIVE_CONF_DIR=/home/hadoop/app/hive/conf
```

### 1.2.2. hadoop集群配置

1. 必须启动hdfs和yarn

``` 
sbin/start-dfs.sh

sbin/start-yarn.sh
```

2. 在HDFS上创建/tmp和/user/hive/warehouse两个目录并修改他们的同组权限可写

``` sh
[hadoop@hadoop101 app]$ hadoop fs -mkdir /tmp

[hadoop@hadoop101 app]$ hadoop fs -mkdir -p /user/hive/warehouse

[hadoop@hadoop101 app]$ hadoop fs -chmod g+w /tmp

[hadoop@hadoop101 app]$ hadoop fs -chmod g+w /user/hive/warehouse
```

3. 启动hive

``` sh
bin/hive
```

### 1.2.3. 安装mysql

参考文档
[1.centos7安装mysql（完整）](https://www.cnblogs.com/lzhdonald/p/12511998.html)  
[2. CentOS安装mysql](https://www.cnblogs.com/shuo1208/p/11237713.html)

在CentOS中默认安装有MariaDB，这个是MySQL的分支，但为了需要，还是要在系统中安装MySQL，而且安装完成之后可以直接覆盖掉MariaDB。

1. 下载并安装MySQL官方的 Yum Repository

``` sh
wget -i -c http://dev.mysql.com/get/mysql57-community-release-el7-10.noarch.rpm

yum -y install mysql57-community-release-el7-10.noarch.rpm

# 安装MySQL服务器。
yum -y install mysql-community-server
```

2. 配置数据库
* 字符集配置等

``` sh
vim /etc/my.cnf

## 添加这三行

skip-grant-tables
character_set_server=utf8
init_connect='SET NAMES utf8'

# skip-grant-tables：跳过登录验证
# character_set_server=utf8：设置默认字符集UTF-8
# init_connect='SET NAMES utf8'：设置默认字符集UTF-8

```

3. 启动mysql

设置开机启动

``` sh
systemctl start mysqld.service
```

启动mysql

``` sh
mysql
```

4. 设置密码

``` sh
update mysql.user set authentication_string=password('123456') where user='root';

flush privileges;

# 编辑my.cnf配置文件将：skip-grant-tables这一行注释掉
# 重启mysql服务
systemctl start mysqld.service

# 再次登录mysql
mysql -uroot -p
```

5. MySql中user表中主机配置  

配置只要是root用户+密码，在任何主机上都能登录MySQL数据库。

``` sql
-- 查询user表
select User, Host, Password from user;

-- 删除root用户的其他host
delete from user where Host='127.0.0.1';
delete from user where Host='::1';

-- 刷新
flush privileges;

-- 只留下一个用户
mysql> select User,Host from user;
+------+------+
| User | Host |
+------+------+
| root | %    |
+------+------+
```

## 1.3. Hive元数据配置到MySql

### 1.3.1. 安装驱动

1. 驱动

去官网下载mysql-connector  
https://downloads.mysql.com/archives/c-j/

2. 拷贝mysql驱动到 `hive/lib/` 目录下。

### 1.3.2. 配置Metastore到MySql

1. 在 `hive/conf` 目录下创建一个 `hive-site.xml` 
2. 根据官方文档配置参数，拷贝数据到hive-site.xml文件中

https://cwiki.apache.org/confluence/display/Hive/AdminManual+MetastoreAdmin

``` xml
<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
	<property>
	  <name>javax.jdo.option.ConnectionURL</name>
	  <value>jdbc:mysql://hadoop101:3306/metastore?createDatabaseIfNotExist=true</value>
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

3. 配置完毕后，如果启动hive异常，可以重新启动虚拟机。（重启后，别忘了启动hadoop集群）

### 1.3.3. hive安装的一些坑和注意点

1. bin/hive命令启动hive，报错：java.lang.NoSuchMethodError: com.google.common.base.Preconditions.checkArgument。

【解决方案】

* 查看hadoop安装目录下share/hadoop/common/lib内guava.jar版本
* 查看hive安装目录下lib内guava.jar的版本 如果两者不一致，删除版本低的，并拷贝高版本的 问题解决！

[参考资料](https://blog.csdn.net/GQB1226/article/details/102555820)

2. `bin/hive` 命令启动hive，报错：FAILED: HiveException java.lang. RuntimeException: Unable to instantiate org.apache.hadoop.hive.ql.metadata. SessionHiveMetaStoreClient.

【问题根源】

* hive配置数据库
* 安装好mysql后没有初始化数据库导致。

``` sh
# 在hive的bin目录下执行命令：
./schematool -dbType mysql -initSchema
```

3. hive没有自带的mysql-connector工具，需要自行下载。

https://dev.mysql.com/downloads/connector/j/

4. hive启动，一连串告警

``` 
 For compliance with existing applications not using SSL the verifyServerCertificate property is set to 'false'. You need either to explicitly disable SSL by setting useSSL=false, or set useSSL=true and provide truststore for server certificate verification.
```

【解决方案】hive-site.xml中加一个配置 `useSSL=false` 

``` xml
<property>
  <name>javax.jdo.option.ConnectionURL</name>
  <value>jdbc:mysql://localhost:3306/hive?createDatabaseIfNotExist=true&amp;useSSL=false</value>
  <description>JDBC connect string for a JDBC metastore</description>
</property>
```

5. 启动hive时必须先启动hadoop

若hadoop处于安全模式，需要关闭安全模式。

Cannot create directory /tmp/hive/root/67ae8485-0e7e-4e31-af1b-276dce2e86d3. Name node is in safe mode.
The reported blocks 70 has reached the threshold 0.9990 of total blocks 70. The minimum number of live datanodes is not required. In safe mode extension. Safe mode will be turned off automatically in 10 seconds.

``` sh
hadoop dfsadmin -safemode leave
```
