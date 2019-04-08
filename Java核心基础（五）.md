# 谈谈int和Integer的区别

> 在深入JVM的专栏里也提到过，Java并不是一个纯粹的面向对象，因为它的底层的8种数据类型（int，char，long，short，double，float，boolean，byte）并不是对象。比如Integer，它是int的包装类，又比如Charactor是char的包装类。

接下来就深入的了解简单类型和包装类的区别（以int和Integer为例）

> 在此之前有没有听到过自动装箱和拆箱的概念呢？

## 自动拆箱和装箱

```

Integer a=1;//这就是一个自动装箱，如果没有自动装箱的话，需要这样Integer a=new Integer(1)
int b=a;//这就是一个自动拆箱，如果没有自动拆箱的话，需要这样：int b=a.intValue()

```

自动装箱和拆箱保证不同的写法在运行时等价，它们发生在编译阶段，也就是生成的字节码是一致
的。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190407230159224.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

> 在 Java 5 中新增了静态工厂方法 valueOf，在调用它的时候会利用
个缓存机制，带来了明显的性能改进。这个值默认缓存是 -128 到 127 之间。

![在这里插入图片描述](https://img-blog.csdnimg.cn/2019040722484574.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)


我们可以很清晰的看到valueOf方法，使用了缓存机制，超出缓存才new Integer。那为什么说自动装箱和拆箱要讲到valueOf方法，这是因为在对代码反编译后发现，编译成class文件时，JVM会默认的使用valueof方法。

```

反编译输出：
Integer integer = 1;
int unboxing = integer ++;1: invokestatic #2 
//Methodjava/lang/Integer.valueOf:(I)Ljava/lang/Integer8: invokevirtual #3 // Methodjava/lang/Integer.intValue

```

> 看一个经典的例子

```
 public static void main(String[] args) {

        Integer i1 = 100;
        Integer i2 = 100;
        Integer i3 = 200;
        Integer i4 = 200;
        System.out.println(i1 == i2);
        System.out.println(i3 == i4);
    }

```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190407230251382.png)

为什么同样的定义方式，第一个是true，而第二个是false。可以看看前面的截图缓冲区的默认的最大和最小值。如果还是不理解的话看看下边的例子就会恍然大悟

```
 public static void main(String[] args) {

        Integer i1 = 100;
        Integer i2 = 100;
        Integer i3 = 127;
        Integer i4 = 127;
        Integer i5 = 128;
        Integer i6 = 128;
        Integer i7 = -128;
        Integer i8 = -128;
        Integer i9 = -129;
        Integer i10 = -129;
        System.out.println(i1 == i2);
        System.out.println(i3 == i4);
        System.out.println(i5 == i6);
        System.out.println(i7 == i8);
        System.out.println(i9 == i10);
    }

```

输出结果：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190407231939248.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

可以看出如果超过缓存区就不再是同一个对象（上边源码中逻辑），就重新new一个对象。

下边这几个都和Integer相似

* Boolean，缓存了 true/false 对应实例，确切说，只会返回两个常量实例Boolean.TRUE/FALSE。
* Short，同样是缓存了 -128 到 127 之间的数值。
* Byte，数值有限，所以全部都被缓存。
* Character，缓存范围’\u0000’ 到 ‘\u007F

那又有人问Double，Float呢？

> 你可以试一下，是不可能相等的，为什么呢？这个按照常理也可以分析过来，带有精度的数组重复率是真的低，而且没必要。

耍嘴皮子谁都会，那就深入源码看看呗

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190407232530167.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

在它的valueOf方法中就没有缓存这一说！

> 刚刚在看源码的时候有没有注意到它的缓存的high并没有写死，因此我们可以使用-XX:AutoBoxCacheMax参数来修改最大化存限度！

## 知晓包装原理优化代码性能

> 知道了在大部分的普通数据类型都会又缓存区，那么如果我的数据几十万，几百万，甚至几千万，那这样岂不是开销很大？

因此我们就可以针对这一点进行优化，使用原始数据类型、数组甚至本地代码实现等，在性能极度敏感的场景往往具有比较大的优势，用其替换掉包装类、动态数组（如 ArrayList）等可以作为性能优化的备选项。但是在一般写代码时并不需要这样来写。

> 插叙，看到这一块就随便说一说

```
  /**
     * The value of the Double.
     *
     * @serial
     */
    private final double value;

```

不管是那种类型的包装类，value都是private final ，这也就体现到了包装类的安全性（信息的安全），在并发中也依然是安全的，因为它是private，并且是final不变的。






