# Java核心基础总结和一些易混淆点

## Error和Exception的关系和区别

关系： Error和Exception都是继承于Throwable类

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190401093514439.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

1. Error： 是指在正常情况下，不大可能出现的情况，绝大部分的 Error 都会导致程序（比如 JVM自身）处于非正常的、不可恢复状态。 因此，这些异常就不用捕获，因为它导致程序出问题了。比如栈溢出，内存溢出等都是它的子类。

2. Exception：是程序正常运行中，可以预料的意外情况，可能并且应该被捕获，进行相应处理。
 
 * 可检查异常（checked）：可检查异常在源代码里必须显式地进行捕获处理，使用try catch。
 
 * 不可检查异常（unchecked）：不检查异常就是所谓的运行时异常，类似NullPointerException、ArrayIndexOutOfBoundsException 之类，通常是可以编码避免的逻辑错误，不会在编译期强制要捕获。

异常处理的基本原则：

1. 尽量不要使用通用Exception处理，这样可能会使代码变得难懂，使用特定异常的捕获，也会进一步减少一些我们不想看到的异常，让我们处理错误变得清晰。

2. 切记不要生吞异常（比如直接continue），不要根据自己主观来判断会不会出现异常，否则这样导致的结果可能无法处理。 

2. 可以在在适当时刻根据需求抛出异常，比如有些异常在抛出时就可以多次进行适当处理，如果不行就进行处理，有些异常就必须处理，有些异常就必须抛给上一级进行统一处理。

> 在大型项目中往往会在性能和编码规范中进行适当选择，因为过多的try catch会造成很大的系统损耗，new Exception（每实例化一个Exception都会对当前的栈进行一个快照）也会加大对系统资源的消耗。

> 思考:NoClassDefFoundError和ClassNOtFoundException有什么区别？

1. 从本质来说NoClassDefFoundError是一个Error，而ClassNotFoundException是一个异常。

2. 前者是在类编译时存在，在运行时不存在，有可能的原因是打包漏掉了一些东西，或者引用的时候每引用完整。而后者是由于找不到类了，有可能是在使用反射的时候没有写对类的路径，或者就是在别处对这个类已经有了一次引用加载了（某个类加载器已经将它加载到内存了）而导致找不到。

> 类加载器就不在此多介绍了，如果介绍的话篇幅就太长了，可以自己下去了解了解。

## final、finally、 finalize 有什么不同

我相信有好多人学了好长时间Java都不一定能把这个说的十分透彻（当然，这只是一部分人）。其实这三个没有一点关系，就是单纯外观有一丢丢相似。

* final 可以用来修饰类、方法、变量，分别有不同的意义，final 修饰的 class 代表不可以继承扩展，final 的变量是不可以修改的，final 的方法也是不可以重写的（override）。

* finally 则是 Java 保证重点代码一定要被执行的一种机制。我们可以使用 try-finally 或者 try catch-finally 来进行一些事后处理比如：关闭 JDBC 连接、保证 unlock 锁等动作。

* finalize 是基础类 java.lang.Object 的一个方法，它的设计目的是保证对象在被垃圾收集前完成特定资源的回收。finalize 机制现在已经不推荐使用，并且在 JDK 9 开始被标记为deprecat。

在日常的学习开发中，在需要使用final的地方尽量使用final关键字，一方面是保证数据的安全（对于系统常量例如一些只读数据），另一方面使它不可变也减少了系统的开销（在多线程中不用进行线程同步）。



### final

在我当时看书的时候在书上见到一个实例刚好有很巧在我看的这位大佬写的文章讲里提到

```
 final List<String> strList = new ArrayList<>();
 strList.add("wrial"); 

```

你认为上边这个程序有问题吗？在前面声明了final，然后给strlist添加元素会报错？

然而并不会报错，为什么呢？因为final修饰的只是一个对象的引用，而对象对自己的属性进行操作也就是此对象的行为是不受约束的。（比如我们把另一个list赋值给它，这样就不可以）

那怎样让List也成为不可变的呢？

```
List<String> unmodifiableList = List.of("hello", "world");
 unmodifiableStrList.add(" append hello ");

```
在使用了List.of的list中添加元素会报错。

下边是引用大佬的一段话

Immutable 在很多场景是非常棒的选择，某种意义上说，Java 语言目前并没有原生的不可变支
持，如果要实现 immutable 的类，我们需要做到：

* 将 class 自身声明为 final，这样别人就不能扩展来绕过限制了。
* 将所有成员变量定义为 private 和 final，并且不要实现 setter 方法。
* 通常构造对象时，成员变量使用深度拷贝来初始化，而不是直接赋值，这是一种防御措施，
因为你无法确定输入对象不被其他人修改。
* 如果确实需要实现 getter 方法，或者其他可能会返回内部状态的方法，使用 copy-on-write原则，创建私有的 copy。

这段话很有深度，有的人还可能没有听过Java中还有Immutable这个东西，我在读文章之前也没有听到过，Immutable就是不可改变的，它的这种不可改变就相当于镶嵌在内存中，没有任何人或者任何事将它改变，这也就是不要实现setter的原因了，在初始化或者获取状态时也要十分严谨。

想起上边copy我还想起了一件有趣的事情，记得有一次在写匿名内部类的时候偶然间需要调用一下外边的成员变量，但是死活不能调用，我就在想为什么不能用呢？后来去网上查了一下，它的调用原理也是copy，将final定义的外部成员copy一份到匿名内部类中使用。




### finally


说到finally，它也就是仅仅在try catch中充当一个善后者的工作，那finally一定会被执行吗？答案是否定的，比如在catch中退出，无限循环，或者是kill Thread都会让finally不能执行。所以，一定不要认为try catch 中finally是一定会被执行的。

### finalize


finalize似乎已经是被Java抛弃了，反正我写Java这么长时间是没用过这个关键字，但是它真的已经被抛弃了吗？

finalize的缺点：

1. finalize的执行是和JDK的垃圾回收相关的，并且有人在实验中验证，一旦实现了非空的 finalize 方法，就会导致相应对象回收呈现数量级上的变慢，大概是 40~50 倍的下降。为什么呢？因为finalize在执行过程中需要一些特殊的权限，因此它的对象就会是”带有权限的公民“，JVM 要对它进行额外处理。成为了快速回收的阻碍者，可能导致你的对象经过多个垃圾收集周期才能被回收。
2. 可能会造成oom（out of memory），由于finalize拖慢了整体的垃圾回收速度（System.runFinalization () 告诉 JVM 积极一点，可能会有效，但是不能根除这个问题），有可能会由于清理不及时而导致oom问题。
3. 使用finalize最为程序最后的守门员也不是很合理，因为finalize 还会掩盖资源回收时的出错信息。因此在平时使用一些资源的时候最好在结束进行手动关闭。

finalize真的没优点了吗：
并不是，在 Java 中调用非 Java 代码，在非 Java 代码中若调用了
C的 malloc 来分配内存，如果不调用 C 的free 函数，会导致内存泄露。所以需要在finalize中调用它。

新的JDK已经不推荐使用finalize那有什么可以代替它呢？

Java 平台目前在逐步使用 java.lang.ref.Cleaner 来替换掉原有的 finalize 实现。Cleaner 的实现利用了幻象引用（PhantomReference），这是一种常见的所谓 post-mortem 清理机制。它比finalize 更加轻量、更加可靠。它吸取了 finalize 里的教训，每个 Cleaner 的操作都是独立的，它有自己的运行线程，所以可以避免意外死锁等问题。

但是Cleaner真的就很好能解决这一问题吗？从可预测性的角度来判断，Cleaner 或者幻象引用改善的程度仍然是有限的，如果由于种种原因导致幻象引用堆积，同样会出现问题。

> 什么是幻象引用？称为虚引用，你不能通过它访问对象。对象引用仅仅是提供了一种确保对象被finalize以后，做某些事情的机制。幻象引用正如它表面意思，它不是一个真实的引用。
> 什么是post-mortem？它就是时候要进行的一些处理。








