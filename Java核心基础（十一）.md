# Synchronized和ReentrantLock
> Synchronized在人们印象中还是很早以前的重量型锁，其实也并不是这样，接下来浅浅剖析一下Synchronized和ReentrantLock的相同和差异以及适合使用的场景。

## 简单回顾一下线程安全

要保证线程安全必须要满足下面几个基本性质

1.原子性：一个线程的相关操作作为一个原子模块执行，不会受到其他线程的干扰。（加锁）

2.可见性：也就是一个线程修改了共享变量中的某些值，要立即更新到内存，保证其他线程不会出现错误。（votile）

3.有序性：防止指令重排，在JVM中由很多指令重排的操作，比如happens-before等等 

## 一些的基本术语

### JVM安全点

这是从一个私人博客中找到的感觉通俗易懂的解释：[原文连接](https://www.ezlippi.com/blog/2018/01/safepoint.html)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190527204051923.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190527204258605.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

### 什么是CAS？

CAS算法 即compare and swap（比较与交换），是一种有名的无锁算法。无锁编程，即不使用锁的情况下实现多线程之间的变量同步，也就是在没有线程被阻塞的情况下实现变量的同步，所以也叫非阻塞同步（Non-blocking Synchronization）。CAS算法涉及到三个操作数

1. 需要读写的内存值 V
2. 进行比较的值 A
3. 写入的新值 B

### 什么是重入锁？

它是表示当一个线程试图获取一个它已经
获取的锁时，这个获取动作就自动成功，这是对锁获取粒度的一个概念，也就是锁的持有是以线
程为单位而不是基于调用次数。Java 锁实现强调再入性是为了和 pthread 的行为进行区分。

### 公平锁和非公平锁（非抢占和抢占）

* 公平锁：  新进程发出请求，如果此时一个线程正持有锁，或有其他线程正在等待队列中等待这个锁，那么新的线程将被放入到队列中被挂起。

    相当于一堆嗜睡的低血糖病人排队看医生，进去的病人门一关，外面的人便排队候着打瞌睡，轮到他时再醒醒进去

* 非公平锁: 新进程发出请求，如果此时一个线程正持有锁，新的线程将被放入到队列中被挂起，但如果发出请求的同时该锁变成可用状态，那么这个线程会跳过队列中所有的等待线程而获得锁。
    
    相当于排队看医生，进去的病人门一关，外面的人便排队候着打瞌睡，这时新人来了，碰巧门一开，外面的人还没完全醒来，他就乘机冲了进去。

> 在Synchronized中只有非公平锁，在ReentrantLock中可以进行公平锁和非公平锁之间选择。

为什么非公平锁要快于公平锁？

在恢复等待队列线程会浪费时间，而抢占模式并不会出现该问题。

公平锁是一个减少饥饿的有效方法，但是线程饥饿这种情况基本不会出现，因此Java默认的调度策略都已经很好的进行了处理，因此公平和非公平显得没有那么重要了。当程序中必须要使用公平锁时才有必要切换为公平锁！

### 自旋锁

#### 自旋锁概念

是指当一个线程在获取锁的时候，如果锁已经被其它线程获取，那么该线程将循环等待，然后不断的判断锁是否能够被成功获取，直到获取到锁才会退出循环。也可以用我们操作系统的一个在信号量那一章的一个名词来形容——忙等（busy waiting）。

#### 自旋锁存在的问题

如果你看过很多并发包中的源码，你会发现自旋锁是很多的，它们的实现一般都是一个for（；；），这样就可以处于等待，当然while（true）也没问题。因此它会存在一些很明显的问题，如下：

1. 如果某个线程持有锁的时间过长，就会导致其它等待获取锁的线程进入循环等待，消耗CPU。使用不当会造成CPU使用率极高。

2. 上面Java实现的自旋锁不是公平的，即无法满足等待时间最长的线程优先获取锁。不公平的锁就会存在“线程饥饿”问题。

#### 自旋锁的优点

非自旋锁在获取不到锁的时候会进入阻塞状态，从而进入内核态，当获取到锁的时候需要从内核态恢复，需要线程上下文切换。 （线程被阻塞后便进入内核（Linux）调度状态，这个会导致系统在用户态与内核态之间来回切换，严重影响锁的性能）

自旋不会使线程状态发生切换，因为进程状态切换要从用户态到管态再到用户态的切换，它会在那一直自旋阻止进入管态，也就保持了高响应。

### 偏向锁，轻量级锁，重量级锁

![在这里插入图片描述](https://img-blog.csdnimg.cn/2019052720081624.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

## Synchronized

Synchronized很好用，它的三种使用方法在[Synchronized的使用](https://github.com/wrail/MultiThread/blob/master/Java%E5%A4%9A%E7%BA%BF%E7%A8%8B%E5%B9%B6%E5%8F%91%EF%BC%88%E4%BA%8C%EF%BC%89.md)中详细介绍过。

带有Synchronized的程序通过JavaP反编译后可以看到实现Synchronized的机制其实就是**monitor**,如下**使用了monitorenter和monitorexit来控制同步，是jvm依赖操作系统互斥（mutex）来实现的。**

```
11: astore_1
12: monitorenter
13: aload_0
14: dup
15: getfield #2 // Field sharedState:I
18: dup_x1
…
56: monitorexit

```

在1.6之前synchronized是完全依赖操作系统，也只能实现相同的重量级锁，但是在1.6后，提供了三种不同的 Monitor 实现，也就是常说的三种不同的锁：偏向锁（Biased Locking）、轻量级锁和重量级锁，大大改进了其性能。

为什么说改进了性能呢？

第一阶段：

它对锁进行了自动的升级和降级处理，在没有竞争时就会默认使用偏向锁。JVM会利用CAS对对象头上的Mark Word部分设置线程ID，以表示这个对象偏向于当前线程，不涉及真正的互斥锁。**也就是说虽然可能有很多线程，但是只有一个线程去访问某个同步代码块，它就在对象头中对这个线程进行标记。**

第二阶段：

如果它不满足偏向锁的要求，也就是说这个代码块不是仅有一个线程访问了，又来了几个线程，因此偏向锁就没用了，因此就得先撤销偏向锁，并切换到轻量级锁的实现。轻量锁依赖CAS操作Mark Word来试图获取锁，如果获取到就使用普通的轻量级锁，否则，进一步升级为重量级锁。

下面是一个简单的MarkWord结构和偏向后的结构，MarkWord在JVM部分有详细的阐述。
![在这里插入图片描述](https://img-blog.csdnimg.cn/2019052720491785.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

> 网上都说是Java的锁只能升而不能降级，根据杨晓峰的文章写到锁是会发生降级的，当JVM进入安全点（SafePoint）的时候，会对闲置的Monitor进行检查，然后试图降级。

下边这两个图是在简书上看到比较不错的：[原文地址](https://www.jianshu.com/p/36eedeb3f912)

![Synchronized原理图](https://upload-images.jianshu.io/upload_images/4491294-e3bcefb2bacea224.png)

简化版图

![](https://upload-images.jianshu.io/upload_images/4491294-345a15342fad119a.png)

## ReentrantLock

ReentrantLock是一个相对于Synchronized比较灵活的锁，而且功能上更强大一点，并且ReentrantLock是可以看到代码的，不像Synchronized直接是在JVM上，但是ReentrantLock灵活的代价就是比Synchronized稍微麻烦一点。下来就来看看ReentrantLock。
