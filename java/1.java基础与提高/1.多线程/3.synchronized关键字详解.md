<!-- TOC -->

- [1. 多线程同步](#1-多线程同步)
    - [1.1. 为什么要引入同步机制](#11-为什么要引入同步机制)
    - [1.2. 程序实例](#12-程序实例)
    - [1.3. 解决方法](#13-解决方法)
- [2. synchronized关键字详解](#2-synchronized关键字详解)
    - [2.1. 是否使用synchronized关键字的不同](#21-是否使用synchronized关键字的不同)
    - [2.2. 多个方法的多线程情况](#22-多个方法的多线程情况)
    - [2.3. 静态的同步方法](#23-静态的同步方法)
    - [2.4. synchronized块](#24-synchronized块)
- [3. 总结](#3-总结)
    - [3.1. 是否使用synchronized关键字的不同](#31-是否使用synchronized关键字的不同)
    - [3.2. 多个方法的多线程情况](#32-多个方法的多线程情况)
    - [3.3. 静态的synchronized方法](#33-静态的synchronized方法)
    - [3.4. synchronized块](#34-synchronized块)

<!-- /TOC -->
# 1. 多线程同步
## 1.1. 为什么要引入同步机制
&emsp;&emsp;在多线程环境中，可能会有两个甚至更多的线程试图同时访问一个有限的资源。必须对这种潜在资源冲突进行预防。

&emsp;&emsp;解决方法：**在线程使用一个资源时为其加锁即可**。

访问资源的第一个线程为其加上锁以后，其他线程便不能再使用那个资源，除非被解锁。
## 1.2. 程序实例

<details>
<summary>取钱的程序例子 </summary>

```java
public class FetchMoneyTest {
    public static void main(String[] args){
        Bank bank = new Bank();
        Thread t1 = new MoneyThread(bank);// 从银行取钱
        Thread t2 = new MoneyThread(bank);// 从取款机取钱
        t1.start();
        t2.start();
    }
}

public class Bank {
    private int money = 1000;

    public synchronized int getMoney(int number){
        if(number < 0){
            return -1;
        }else if(number > money){
            return -2;
        }else if(money < 0){
            return -3;
        }else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            money = money - number;
            System.out.println("Left Money:" + money);
            return number;
        }
    }
}

public class MoneyThread extends Thread{
    private Bank bank;
    public MoneyThread(Bank bank){
        this.bank = bank;
    }
    @Override
    public void run(){
        System.out.println("Get Money:" + bank.getMoney(800));
    }
}
```
</details>

【运行结果】
```
Left Money: 200
800
Left Money: -600
800
```
【说明】
&emsp;&emsp;getMoney()方法中有一些逻辑判断，进入最后一个else语句块后，有一个简短的休眠，那么在第一个线程休眠的过程中，第二个线程也成功进入了这个else语句块（因为存款的钱还没有取走），当两个线程结束休眠后，不再进行逻辑判断而是直接将钱取走，所以两个线程都取到了800元钱，此时money为负600。  
&emsp;&emsp;需要注意这里并不能确定哪一个线程是第一个线程，哪一个线程是第二个线程，先后顺序是不定的。
## 1.3. 解决方法
解决办法：在getMoney()方法上加上关键字`synchronized`。即程序改动后如下：（只是加了一个关键字）　
```
 public synchronized int getMoney(int number){

 }
```
【程序运行结果】
```
Left Money: 200
800
-2
```
&emsp;&emsp;表明第一次取款800元后，剩余200元，当另一个线程再去取的时候，已经不能再取钱了。即一个线程开始执行取钱的方法之后就阻止了其他线程再去执行这个方法，直到本线程结束，其他线程才有访问权利。
# 2. synchronized关键字详解
&emsp;&emsp;多线程的同步机制对资源进行加锁，使得在同一个时间，只有一个线程可以进行操作，同步用以解决多个线程同时访问时可能出现的问题。**同步机制可以使用synchronized关键字实现。当synchronized关键字修饰一个方法的时候，该方法叫做同步方法**。**当synchronized方法执行完或发生异常时，会自动释放锁**。下面通过一个例子来对synchronized关键字的用法进行解析。

## 2.1. 是否使用synchronized关键字的不同
```java
public class ThreadTest {
    public static void main(String[] args){
        Example example = new Example();
        Thread t1 = new Thread2(example);
        Thread t2 = new Thread2(example);
        t1.start();
        t2.start();
    }
}

public class Example {
    public synchronized void execute(){
        for(int i=0;i<5;i++){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Hello: " + i);
        }
    }
}

public class Thread2 extends Thread {
    private Example example;

    public Thread2(Example example) {
        this.example = example;
    }
    @Override
    public void run(){
        example.execute();
    }
}
```
是否在execute()方法前加上synchronized关键字，这个例子程序的执行结果会有很大的不同。

加上synchronized关键字的运行结果：
```
Hello: 0
Hello: 1
Hello: 2
Hello: 3
Hello: 4
Hello: 0
Hello: 1
Hello: 2
Hello: 3
Hello: 4
```
不加synchronized关键字的运行结果：
```
Hello: 0
Hello: 0
Hello: 1
Hello: 1
Hello: 2
Hello: 2
Hello: 3
Hello: 3
Hello: 4
Hello: 4
```

结论：

如果不加synchronized关键字，则两个线程同时执行execute()方法，输出是两组并发的。

如果加上synchronized关键字，则会先输出一组0到9，然后再输出下一组，说明两个线程是顺次执行的。

## 2.2. 多个方法的多线程情况
将程序改动一下，Example类中再加入一个方法execute2()。之后再写一个线程类Thread2，Thread2中的run()方法执行的是execute2()。Example类中的两个方法都是被synchronized关键字修饰的。
```java
public class ThreadTest {
     public static void main(String[] args){
        Example example = new Example();
        Thread t1 = new Thread1(example);
        //Thread t2 = new Thread1(example);
        Thread t2 = new Thread2(example);
        t1.start();
        t2.start();
     }
}

class Example{
    public synchronized void execute(){
        
        for (int i = 0; i < 20; ++i){
            try{
                //Thread.sleep(500);
                Thread.sleep((long) Math.random() * 1000);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            System.out.println("Hello: " + i);
        }
    }
    
    public synchronized void execute2(){
        for (int i = 0; i < 20; ++i){
            try{
                Thread.sleep((long) Math.random() * 1000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("World: " + i);
        }
    }

}

class Thread1 extends Thread{
    private Example example;
    public Thread1(Example example){
        this.example = example;
    }
    @Override
    public void run(){
        example.execute();
    }
}

class Thread2 extends Thread{
    private Example example;
    public Thread2(Example example){
        this.example = example;
    }
    @Override
    public void run(){
        example.execute2();
    }
}
```
&emsp;&emsp;如果去掉synchronized关键字，则两个方法并发执行，并没有相互影响。但是如例子程序中所写，即便是两个方法：

&emsp;&emsp;执行结果永远是执行完一个线程的输出再执行另一个线程的。
【说明】
&emsp;&emsp;如果一个对象有多个synchronized方法，某一时刻某个线程已经进入到了某个synchronized方法，那么在该方法没有执行完毕前，其他线程是无法访问该对象的任何synchronized方法的。
【结论】
&emsp;&emsp;当synchronized关键字修饰一个方法的时候，该方法叫做同步方法。

&emsp;&emsp;Java中的每个对象都有一个**锁（lock）**，或者叫做**监视器（monitor）**，当一个线程访问某个对象的synchronized方法时，*将该对象上锁，其他任何线程都无法再去访问该对象的synchronized方法了*（这里是指所有的同步方法，而不仅仅是同一个方法），直到之前的那个线程执行方法完毕后（或者是抛出了异常），才将该对象的锁释放掉，其他线程才有可能再去访问该对象的synchronized方法。

&emsp;&emsp;注意这时候是给对象上锁，如果是不同的对象，则各个对象之间没有限制关系。

&emsp;&emsp;尝试在代码中构造第二个线程对象时传入一个新的Example对象，则两个线程的执行之间没有什么制约关系。

## 2.3. 静态的同步方法
当一个synchronized关键字修饰的方法同时又被static修饰，之前说过，**`非静态的同步方法会将对象上锁`，但是静态方法不属于对象，而是属于类，它会将这个方法所在的类的Class对象上锁。**一个类不管生成多少个对象，它们所对应的是同一个Class对象。
```java
package com.demo;

public class ThreadTest {
     public static void main(String[] args){
        Example example = new Example();
        Thread t1 = new Thread1(example);
        // 此处即便传入不同的对象，静态方法同步仍然不允许多个线程同时执行
        example = new Example();
        Thread t2 = new Thread2(example);
        t1.start();
        t2.start();
     }
}

class Example{
    public synchronized static void execute(){
        
        for (int i = 0; i < 20; ++i){
            try{
                //Thread.sleep(500);
                Thread.sleep((long) Math.random() * 1000);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            System.out.println("Hello: " + i);
        }
    }
    
    public synchronized static void execute2(){
        for (int i = 0; i < 20; ++i){
            try{
                Thread.sleep((long) Math.random() * 1000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("World: " + i);
        }
    }
}
class Thread1 extends Thread{
    private Example example;
    public Thread1(Example example){
        this.example = example;
    }
    @Override
    public void run(){
        example.execute();
    }
}

class Thread2 extends Thread{
    private Example example;
    public Thread2(Example example){
        this.example = example;
    }
    @Override
    public void run(){
        example.execute2();
    }
}
```
&emsp;&emsp;所以如果是静态方法的情况（execute()和execute2()都加上static关键字），即便是向两个线程传入不同的Example对象，这两个线程仍然是互相制约的，必须先执行完一个，再执行下一个。

结论：

&emsp;&emsp;如果某个synchronized方法是static的，那么当线程访问该方法时，它锁的并不是synchronized方法所在的对象，而是synchronized方法所在的类所对应的Class对象。Java中，无论一个类有多少个对象，这些对象会对应唯一一个Class对象，因此当线程分别访问同一个类的两个对象的两个static synchronized方法时，它们的执行顺序也是顺序的，也就是说一个线程先去执行方法，执行完毕后另一个线程才开始。  

## 2.4. synchronized块
synchronized可以用来修饰代码块。
```java
synchronized(object){　     
}
```
表示线程在执行的时候会将object对象上锁。（注意这个对象可以是任意类的对象，也可以使用this关键字）。
```java
package com.demo;

public class ThreadTest {

     public static void main(String[] args){
         
        Example example = new Example();

        Thread t1 = new Thread1(example); 
        Thread t2 = new Thread2(example);
        t1.start();
        t2.start();
     }
}

class Example{
    private Object object = new Object();
    public void execute(){
        synchronized(object){
            for (int i = 0; i < 20; ++i){
                try{
                    //Thread.sleep(500);
                    Thread.sleep((long) Math.random() * 1000);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                System.out.println("Hello: " + i);
            }
        }
    }
    
    public void execute2(){
        synchronized(object){
             for (int i = 0; i < 20; ++i){
                try{
                    Thread.sleep((long) Math.random() * 1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("World: " + i);
            }
        }
    }
}

class Thread1 extends Thread{
    private Example example;
    public Thread1(Example example){
        this.example = example;
    }
    @Override
    public void run(){
        example.execute();
    }

}

class Thread2 extends Thread{
    private Example example;
    public Thread2(Example example){
        this.example = example;
    }
    @Override
    public void run(){
        example.execute2();
    }
}
```
```
package com.demo;

public class ThreadTest {

     public static void main(String[] args){
         
        Example example = new Example();

        Thread t1 = new Thread1(example); 
        Thread t2 = new Thread2(example);

        t1.start();
        t2.start();
     }
}

class Example{
    
    private Object object = new Object();
    
    public void execute(){
        
        synchronized(object){
            for (int i = 0; i < 20; ++i){
                try{
                    //Thread.sleep(500);
                    Thread.sleep((long) Math.random() * 1000);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                System.out.println("Hello: " + i);
            }
        }
        
    }
    
    public void execute2(){
        
        synchronized(object){
             for (int i = 0; i < 20; ++i){
                try{
                    Thread.sleep((long) Math.random() * 1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("World: " + i);
            }
        }
       
    }

}

class Thread1 extends Thread{
    
    private Example example;

    public Thread1(Example example){
        this.example = example;
    }

    @Override
    public void run(){
        example.execute();
    }

}

class Thread2 extends Thread{
    
    private Example example;

    public Thread2(Example example){
        this.example = example;
    }

    @Override
    public void run(){
        example.execute2();
    }

```

```
Hello: 0
Hello: 1
Hello: 2
Hello: 3
Hello: 4
Hello: 5
Hello: 6
Hello: 7
Hello: 8
Hello: 9
Hello: 10
Hello: 11
Hello: 12
Hello: 13
Hello: 14
Hello: 15
Hello: 16
Hello: 17
Hello: 18
Hello: 19
World: 0
World: 1
World: 2
World: 3
World: 4
World: 5
World: 6
World: 7
World: 8
World: 9
World: 10
World: 11
World: 12
World: 13
World: 14
World: 15
World: 16
World: 17
World: 18
World: 19
```

 例子程序4所达到的效果和例子程序2的效果一样，都是使得两个线程的执行顺序进行，而不是并发进行，当一个线程执行时，将object对象锁住，另一个线程就不能执行对应的块。

 synchronized方法实际上等同于用一个synchronized块包住方法中的所有语句，然后在synchronized块的括号中传入this关键字。当然，如果是静态方法，需要锁定的则是class对象。

可能一个方法中只有几行代码会涉及到线程同步问题，所以synchronized块比synchronized方法更加细粒度地控制了多个线程的访问，只有synchronized块中的内容不能同时被多个线程所访问，方法中的其他语句仍然可以同时被多个线程所访问（包括synchronized块之前的和之后的）。

注意：被synchronized保护的数据应该是私有的。

# 3. 总结

## 3.1. 是否使用synchronized关键字的不同
&emsp;&emsp;如果不加synchronized关键字，则两个线程同时执行execute()方法，输出是两组并发的。

&emsp;&emsp;如果加上synchronized关键字，则会先输出一组0到9，然后再输出下一组，说明两个线程是顺次执行的。
## 3.2. 多个方法的多线程情况
&emsp;&emsp;当synchronized关键字修饰一个方法的时候，该方法叫做同步方法。

&emsp;&emsp;Java中的每个对象都有一个锁（lock），或者叫做监视器（monitor），当一个线程访问某个对象的synchronized方法时，将该对象上锁，其他任何线程都无法再去访问该对象的synchronized方法了（这里是指所有的同步方法，而不仅仅是同一个方法），直到之前的那个线程执行方法完毕后（或者是抛出了异常），才将该对象的锁释放掉，其他线程才有可能再去访问该对象的synchronized方法。

&emsp;&emsp;注意这时候是给对象上锁，如果是不同的对象，则各个对象之间没有限制关系。

&emsp;&emsp;尝试在代码中构造第二个线程对象时传入一个新的Example对象，则两个线程的执行之间没有什么制约关系。
## 3.3. 静态的synchronized方法
&emsp;&emsp;如果某个synchronized方法是static的，那么当线程访问该方法时，它锁的并不是synchronized方法所在的对象，而是synchronized方法所在的类所对应的Class对象。Java中，无论一个类有多少个对象，这些对象会对应唯一一个Class对象，因此当线程分别访问同一个类的两个对象的两个static synchronized方法时，它们的执行顺序也是顺序的，也就是说一个线程先去执行方法，执行完毕后另一个线程才开始。

## 3.4. synchronized块
&emsp;&emsp;synchronized方法实际上等同于用一个synchronized块包住方法中的所有语句，然后在synchronized块的括号中传入this关键字。当然，如果是静态方法，需要锁定的则是class对象。

&emsp;&emsp;可能一个方法中只有几行代码会涉及到线程同步问题，所以synchronized块比synchronized方法更加细粒度地控制了多个线程的访问，只有synchronized块中的内容不能同时被多个线程所访问，方法中的其他语句仍然可以同时被多个线程所访问（包括synchronized块之前的和之后的）。

&emsp;&emsp;注意：被synchronized保护的数据应该是私有的。

&emsp;&emsp;synchronized方法是一种粗粒度的并发控制，某一时刻，只能有一个线程执行该synchronized方法；

&emsp;&emsp;synchronized块则是一种细粒度的并发控制，只会将块中的代码同步，位于方法内、synchronized块之外的其他代码是可以被多个线程同时访问到的。