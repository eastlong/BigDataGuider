# 数据类型

## 业务数据的特殊性

作为缓存使用

### 应用场景

* 原始业务功能设计
  + 秒杀
  + 618活动
  + 双十一活动
  + 排队购票
* 运营平台监控到的突发高频访问数据
  + 突发市政要闻，被强势关注围观
* 高频、复杂的统计数据
  + 在线人数
  + 投票排行榜
* 附加功能
* 系统功能优化或升级
  + 单服务器升级集群
  + Session管理
  + Token管理

### Redis基本数据类型

|Redis数据类型|类比java|
|:----------: |:---:|
|string|String|
|hash|Hashmap|
|list|LinkList|
|set|HashSet|
|sorted_set|TreeSet|

## redis 数据存储格式

* **redis自身是一个Map, 其中所有的数据都是采用key:value的形式存储**

* 数据类型指的是存储的数据的类型，也就是value部分的类型，key部分永远都是字符串；

<center><img width="400" src="imgs/2/数据存储.png"></center>

## String类型

* 存储内容：通常使用字符串，如果字符串以整数的形式展示，可以作为数字操作使用

### 基本操作

``` sh
# 添加/修改多个数据
mset key1 valueq key2 value2 …

127.0.0.1:6379> mset a 1 b 2 c 3
OK
127.0.0.1:6379> mget a b
1) "1"
2) "2"

# 获取数据字符个数（字符串长度）
strlen key

127.0.0.1:6379> strlen a
(integer) 1

# 追加信息到原始信息后部（如果原始信息存在就追加，否则新建）
127.0.0.1:6379> append a "hadoop"
(integer) 7
127.0.0.1:6379> get a
"1hadoop"
```

多指令消耗的时间短。

<center><img width="400" src="imgs/2/多指令. JPG"></center>
