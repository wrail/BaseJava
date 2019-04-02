## String，StringBuffer，stringBuilder的区别和联系

### String

String在Java基础中充当着很重要的角色，里面有生成和管理字符串的逻辑。基本在每一种语言String都是必不可少的。它也是典型的Immutable类，它的所有属性都是final的，由于它是Immutable类，因此对它的修改操作都会让它产生新的String对象。正因为如此，频繁的对字符串进行操作会导致性能一定程度的下降。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190402083455826.png)

何以见得它是一个Immutable类，在图片中的value决定了它的大小，并且声明为final。

> 因为它是一个典型的Immutable类，因此它在拷贝上体现出它足够的效率，因为它是持久不变的。也不需要额外的复制数据。

### StringBuffer

前文中提到String进行频繁的修改会对效率有影响，因此也就出现了StringBuffer，它是专门来处理append或者是add方法的，把字符串增加到已有的字符串序列的末尾或者指定位置。并且它也是线程安全的，因此也会一定程度带来一些系统开销。因此，在没有线程安全的条件下还是使用下边的StringBuilder。当然线程安全并不是乱说的，看看下边源码中的方法都是带有synchronized关键字的。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190402084402189.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

### StringBuilder

这是源码中对StringBuilder的注释。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190402084052208.png)

可以从源码的注释看出，它不是一个线程安全的，在能力上和 StringBuffer 没有本质区别，但是它去掉了线程安全的部分，有效减小了开销，是绝大部分情况下进行字符串拼接的首选。接下来看看StringBuilder的源码，可以发现它的方法都没有实现synchronzied关键字。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190402084720617.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

> 为了实现字符序列问题，StringBuffer和StringBuilder底层使用的是char/byte（在JDK9以前是使用的char，在9以后使用的Byte），并且二者都继承了 AbstractStringBuilder因此一些基本操作都是相同的就仅仅是安全问题的差别。

在JDK中也给出了默认最大size最大

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190402090041919.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190402090200729.png)

那要进行字符串拼接效率高必定要用空间来换效率，因此必须要有多余的空位，可以让字符串很快速的插入进来并且不用重新分配空间。但是这个初始化空间得多大才能在不浪费资源的前提下尽量提高效率，在JDK中默认的初始值是16，扩容会产生多重开销，因为要抛弃原有数组，创建新的（可以简单认为是倍数）数组，还要进行 arraycopy。

进入arraycopy中可以看到，扩容后的大小接近原来的二倍。

![在这里插入图片描述](https://img-blog.csdnimg.cn/2019040209122753.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190402090634229.png)

> 如果你深入过其他语言或者其他数据库等底层，你会发现它们的底层对字符串的设计有异曲同工之妙，比如Redis中的SDS也大致采用这种方法，因为Redis更加注重效率，因此Redis的底层设计能稍微的详细一点。

### 底层设计层面

* 自动编译转换

但是你想想，如果我们在进行字符串拼接时用append，拼接很多次，使用了很多的append，这样就会降低代码的可读性，因此在字符串拼接的底层默认的实现了优化方法。

```
public class StringConcat {

 public static void main(String[] args) {

 String myStr = "aa" + "bb" + "cc" + "dd"; 

 System.out.println("My String:" + myStr); 

 } 

 }

```

把上边代码分别在JDK8和JDK9上编译然后再反编译

* 在 JDK 8 中，字符串拼接操作会自动被 javac 转换为 StringBuilder 操作

* JDK 9 里面则是因为 Java 9 为了更加统一字符串操作优化，提供StringConcatFactory，作为一个统一的入口。

> javac自动编译提供的优化不一定是最好，但在一般条件下也是不错了。我用的JDK8对JDK9的没有进行验证，但是从专业人士那查的也不会错。

* 字符串缓存

为什么会有字符串缓存这个概念？

常见应用进行堆转储（Dump Heap），然后分析对象组成，会发现平均25% 的对象是字符串，并且其中约半数是重复的。

由此就开始了演变过程

最初，Java 6 以后提供了 intern() 方法，目的是提示 JVM 把相应字符串缓存起来，以备重
复使用，但是它把这些东西就放在了永久代中，很容易造成OOM。再后来设计者也发现了这个问题，后来就将这些东西放在堆里，并且随着JDK的迭代，默认缓存也在不断增大。

-XX:+PrintStringTableStatistics此命令可以打印出具体数据在console中，-XX:StringTableSize=N此命令可以手动在JVM调大小。













