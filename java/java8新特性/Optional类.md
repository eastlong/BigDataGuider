<!-- TOC -->

- [1. Optional](#1-optional)
    - [1.1. Optional解决空指针问题](#11-optional解决空指针问题)
        - [1.1.1. ofNullable判空](#111-ofnullable判空)
        - [1.1.2. ifPresent()替代if判断](#112-ifpresent替代if判断)
    - [1.2. filter()方法筛选判断](#12-filter方法筛选判断)
    - [1.3. map()方法](#13-map方法)
    - [1.4. orElseGet()和orElse()](#14-orelseget和orelse)
    - [1.5. orElseThrow()方法](#15-orelsethrow方法)
- [2. 参考资料](#2-参考资料)

<!-- /TOC -->
# 1. Optional

## 1.1. Optional解决空指针问题
Optional: 进行非空判断，避免空指针。
典型应用场景：
### 1.1.1. ofNullable判空
* 很low的写法：
```java
@Test
public void test4() {
    Employee employee = new Employee();
    if (employee != null) {
        if (employee.getName() != null) {
            System.out.println(employee.getName());
        }
    }
}
```

* Optional写法
```java
    @Test
    public void test3() {
        Employee employee = new Employee();
        Optional.ofNullable(employee).map(u -> u.getName()).orElse("UnKnow");

         Optional.ofNullable(employee).map(Employee::getName).orElse("UnKnow");
    }
```

### 1.1.2. ifPresent()替代if判断
```java
    public static void printName(Employee employee){
        Optional.ofNullable(employee).ifPresent(u ->
                System.out.println("The employee name is:"+ employee.getName()) );
    }

    // 重点掌握
    @Test
    public void test1(){
        User user = new User(1,"hadoop");
        Optional.ofNullable(user).
                map(User::getName).
                ifPresent(u ->{
            System.out.println(u);
            // 一系列的操作
            System.out.println("一系列的操作");
        });
    }
```
【说明】
ifPresent()方法接受一个`Consumer对象（消费函数）`，**如果包装对象的值非空，运行Consumer对象的accept()方法**。


## 1.2. filter()方法筛选判断
1. 很low的写法
```java
    public static void filterAge(Employee employee){
        if(employee != null){
            if(employee.getAge() > 18){
                System.out.println("The student age is more than 18.");
            }
        }
    }
```

2. filter()引入
```java
    public static void filterAge2(Employee employee){
        Optional.ofNullable(employee).
                filter(u -> u.getAge() > 18).
                ifPresent(u -> System.out.println("The student age is more than 18."));
    }
```
【说明】
filter()方法接受参数为Predicate对象，用于对Optional对象进行过滤，如果符合Predicate的条件，返回Optional对象本身，否则返回一个空的Optional对象。

## 1.3. map()方法
map()方法的参数为Function（函数式接口）对象，map()方法将Optional中的包装对象用Function函数进行运算，并包装成新的Optional对象（包装对象的类型可能改变）。

```java
    public static Optional<Integer> searchAge(Employee employee){
        return Optional.ofNullable(employee).map(u -> u.getAge());
    }
```
【说明】
Optional<T> 类型的变量如何使用：

```java
    @Test
    public void test5(){
        Optional<Employee> op = Optional.of(new Employee());
        Employee emp = op.get();
        System.out.println(emp);
    }

    @Test
    public void test6(){
        Optional<Integer> opInt = Optional.of(1);
        System.out.println(opInt.get());
    }
```

## 1.4. orElseGet()和orElse()
orElseGet()方法与orElse()方法类似，区别在于orElseGet()方法的入参为一个Supplier对象，用Supplier对象的get()方法的返回值作为默认值.
```java
    public static String getName(Employee employee){
        return Optional.ofNullable(employee)
                .map(u -> u.getName())
                .orElseGet(() -> "Unknow");
    }
    
    public static String getName2(Employee employee){
        return Optional.ofNullable(employee)
                .map(u -> u.getName())
                .orElse("Unknow");
    }
```

## 1.5. orElseThrow()方法
orElseThrow()方法其实与orElseGet()方法非常相似了，入参都是Supplier对象，只不过orElseThrow()的Supplier对象必须返回一个Throwable异常，并在orElseThrow()中将异常抛出
```java
public static String getGender1(Student student)
    {
        return Optional.ofNullable(student).map(u -> u.getGender()).orElseThrow(() -> new RuntimeException("Unkown"));      
    }
```

# 2. 参考资料
[【java8新特性】Optional详解](https://www.jianshu.com/p/d81a5f7c9c4e)

https://www.jianshu.com/p/745757d373e9