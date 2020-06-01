# 实际场景
## 需求背景
在实际工作中，写UT是日常的工作之一。为保证UT覆盖率，我们会针对使用的方法写UT。假设在一个类中，出现了一个私有方法，那么该如何写UT？换句话说，该如何调私有方法？

```java
public class PrivateMethod {
    private int add(int a, int b){
        return a + b;
    }
}

public class PrivateMethodTest  {
    @Test
    public void testAdd() throws Exception {
        Class<PrivateMethod> class1 = PrivateMethod.class;
        Object instance = class1.newInstance();
        Method method = class1.getDeclaredMethod("add", new Class[]{int.class, int.class});
        method.setAccessible(true);
        Object result = method.invoke(instance, new Object[]{1, 2});
        System.out.println(result);
    }
}
```
# 反射入门
## 什么是反射
Java反射就是**在运行状态中，对于任意一个类，都能够知道这个类的所有属性和方法；对于任意一个对象，都能够调用它的任意方法和属性；并且能改变它的属性**。而这也是Java被视为**动态（或准动态**，为啥要说是准动态，因为一般而言的动态语言定义是程序运行时，允许改变程序结构或变量类型，这种语言称为动态语言。从这个观点看，Perl，Python，Ruby是动态语言，C++，Java，C#不是动态语言。）语言的一个关键性质。

## 反射能做什么
我们知道**反射机制允许程序在运行时取得任何一个已知名称的class的内部信息，包括包括其modifiers(修饰符)，fields(属性)，methods(方法)等，并可于运行时改变fields内容或调用methods**。那么我们便可以更灵活的编写代码，代码可以在运行时装配，无需在组件之间进行源代码链接，**降低代码的耦合度**；还有动态代理的实现等等；但是需要注意的是反射使用不当会造成很高的资源消耗！

## 反射具体实现
```java
public class Person {
    // 私有属性
    private String name = "hadoop";
    // 公有属性
    public int age = 18;

    public Person() {
    }

    /**
     * 私有方法
     */
    private void say() {
        System.out.println("private say()...");
    }
    
    /**
     * 公有方法
     * work
     */
    public void work() {
        System.out.println("public work()...");
    }
}


public class ReflexDemo {
    /**
     * 1. Class 的三种方式
     */
    @Test
    public void getPersonClass() throws Exception {
        Person p1 = new Person();
        // 通过对象调用 getClass() 方法来获取
        Class c1 = p1.getClass();
        // 直接通过 类名.class 的方式得到;该方法最为安全可靠，程序性能更高
        // 这说明任何一个类都有一个隐含的静态成员变量 class
        Class c2 = Person.class;

        // 通过 Class 对象的 forName() 静态方法来获取，用的最多，
        // 但可能抛出 ClassNotFoundException 异常
        Class c3 = Class.forName("com.betop.dostrengthen.reflex.Person");

        // 1.获得类完整的名字
        String className = c2.getName();
        System.out.println(className);

        // 2. 获得类的public类型的属性
        Field[] fields = c2.getFields();
        System.out.println("====== c2.getFields() ======");
        for(Field field: fields){
            System.out.println(field);
        }

        // 3. 获得类的所有属性。包括私有的
        Field[] allFields = c2.getDeclaredFields();
        System.out.println("====== c2.getDeclaredFields() ======");
        for(Field field: allFields){
            System.out.println(field);
        }

        // 4. 获取类public方法
        Method[] methods = c2.getMethods();
        System.out.println("====== c2.getMethods() ======");
        for(Method method: methods){
            System.out.println(method.getName());
        }

        // 5. 获取类的所有方法
        Method[] allMethods = c2.getDeclaredMethods();
        System.out.println("====== c2.getDeclaredMethods() ======");
        for(Method method: allMethods){
            System.out.println(method.getName());
        }

        System.out.println("====== ======");
        // 6. 获取类的public属性
        Field f1 = c2.getField("age");
        System.out.println("====== 获取类的public属性 ======");
        System.out.println("f1:" + f1);

        // 7. 获取类的私有属性
        Field f2 = c2.getDeclaredField("name");
        // 启用和禁用访问安全检查的开关，值为 true，则表示反射的对象在使用时应该取消 java 语言的访问检查；反之不取消
        f2.setAccessible(true);
        System.out.println("====== 获取类的私有属性 ======");
        System.out.println("f2:" + f2);

        // 8. 创建这个类的一个对象
        Object p2 = c2.newInstance();
        // 使用反射机制可以打破封装性，导致了java对象的属性不安全。
        f2.set(p2,"Bob");
        System.out.println(f2.get(p2));
    }
}
```

## 利用反射获取父类私有属性
1. 父类
```java
public class Parent {
    public String publicField = "parent_publicField";
    protected String protectField = "parent_protectField";
    String defaultField = "parent_defaultField";
    private String privateField = "parent_privateField";
}
```

2. 子类
```java
public class Son extends Parent {
}
```

3. 实现获取父类私有private属性
```java
public class ReflectionTest {
    @Test
    public void testGetParentField() throws Exception {
        Class c1 = Class.forName("com.betop.dostrengthen.reflex.Son");
        // 获取父类私有属性值
        System.out.println(getFieldValue(c1.newInstance(),"privateField"));
    }

    public static Field getDeclaredField(Object obj,String fieldName) {
        Field field = null;
        Class c = obj.getClass();
        for(; c != Object.class ; c = c.getSuperclass()){
            try {
                field = c.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            }catch (Exception e){
                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                //如果这里的异常打印或者往外抛，则就不会执行c = c.getSuperclass(),最后就不会进入到父类中了
            }
        }
        return null;
    }

    public static Object getFieldValue(Object object,String fieldName) throws Exception{
        Field field = getDeclaredField(object,fieldName);

        return field.get(object);
    }
}
```

## 反射的应用场景
* 工厂模式：Factory类中用反射的话，添加了一个新的类之后，就不需要再修改工厂类Factory了
* 数据库JDBC中通过Class.forName(Driver).来获得数据库连接驱动
* 分析类文件：毕竟能得到类中的方法等等
* 访问一些不能访问的变量或属性：破解别人代码。

反射被广泛地用于那些需要在**运行时检测或修改程序行为的程序中**。这是一个相对高级的特性，只有那些语言基础非常扎实的开发者才应该使用它。


## 反射的优缺点

### 反射的优点
* 可扩展性 ：应用程序可以利用全限定名创建可扩展对象的实例，来使用来自外部的用户自定义类。反射提高了Java程序的灵活性和扩展性，降低耦合性，提高自适应能力。它允许程序创建和控制任何类的对象，无需提高硬编码目标类
* Java反射技术应用领域很广，如软件测试、JavaBean等
* 许多流行的开源框架例如Struts、Hibernate、Spring在实现过程中都采用了该技术

### 反射的缺点

尽管反射非常强大，但也不能滥用。如果一个功能可以不用反射完成，那么最好就不用。在我们使用反射技术时，下面几条内容应该牢记于心。

* 性能开销 ：反射涉及了动态类型的解析，所以 JVM 无法对这些代码进行优化。因此，反射操作的效率要比那些非反射操作低得多。我们应该避免在经常被执行的代码或对性能要求很高的程序中使用反射。

* 安全限制 ：使用反射技术要求程序必须在一个没有安全限制的环境中运行。如果一个程序必须在有安全限制的环境中运行，如 Applet，那么这就是个问题了。

* 内部暴露 ：由于反射允许代码执行一些在正常情况下不被允许的操作（比如访问私有的属性和方法），所以使用反射可能会导致意料之外的副作用，这可能导致代码功能失调并破坏可移植性。反射代码破坏了抽象性，因此当平台发生改变的时候，代码的行为就有可能也随着变化。






# 参考资料
[通过反射测试私有方法](https://blog.csdn.net/xifeijian/article/details/8762729)  
[反射详解](https://www.cnblogs.com/ysocean/p/6516248.html)