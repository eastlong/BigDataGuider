# Lambda表达式常用场景
## Runnable接口的lambda实现
```java
    public void createRunnableObject() {
        // java8之前
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("No use lambda.");
            }
        }).start();

        // java8之后
        new Thread(() -> System.out.println("Use lambda.")).start();
    }
```

## 数组的循环
```java
    public void listLoop() {
        List<Integer> features = Arrays.asList(1, 2);
        // java8之前
        for (Integer feature : features) {
            System.out.println(feature);
        }

        // java8之后
        features.forEach(n -> System.out.println(n));
        // 引用操作
        features.forEach(System.out::println);
    }
```


# 参考资料
[【java8新特性】Stream API详解](https://www.jianshu.com/p/2b40fd0765c3)
[Optional](https://www.jianshu.com/p/aa15bc9362f1)