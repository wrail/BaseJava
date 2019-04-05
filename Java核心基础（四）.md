## Java中动态代理的深入分析

> 说到动态代理，那肯定也是不能离开了静态代理因为它们都是代理，动态代理和静态代理本质上的区别又是什么呢？

### 什么是代理模式？

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190405084755929.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

上边这一张图就足以说清什么是代理模式，在Subject的子类有代理类和实体类，代理类里面有处理事件的基本接口，并在一定程度上有所增强，并且里面也有一个实体类的引用，在处理事件时调用。

众所周知，Java是一个静态的强类型语言，但是有了反射的Java可以说是边的能动了，静态代理就是提前写好的静态类，动态代理是利用反射而动态的处理事件。

> 强类型和弱类型的区别就是在不同的类型转换时是否需要强转。

## 什么是反射？

反射是Java的基础功能，通过反射可以直接对类或对象进行操作，获取属性方法，构造方法，甚至还可以在运行时对类进行修改，赋予程序在运行时自省（introspect，官方用语）的能力。正因为反射的功能如此强大，这才从静态代理走到了动态代理（不能说反射就是动态代理的实现，应该说反射是动态代理实现的重要方法）。

> 引用别人一句话： 反射，它就像是一种魔法，引入运行时自省
能力，赋予了 Java 语言令人意外的活力，通过运行时操作元数据或对象，Java 可以灵活地操作运行时才能确定的信息。而动态代理，则是延伸出来的一种广泛应用于产品开发中的技术，很多繁琐的重复编程，都可以被动态代理机制优雅地解决。

在java.lang.reflect的抽象类下可以看到很多的反射方法

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190405090910458.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

比如说field，method，constructor等等，尤其要注意一下AccessibleObject（翻译过来就是可进入的对象），里面有一个setAccessilbe方法，如下图，这个方法标记的是一个能否进入的新值，也就是相当于我们平时用的public，protect，private。

> 利用Java反射机制我们可以加载一个运行时才得知名称的class，获悉其构造方法，并生成其对象实体，能对其fields设值并唤起其methods。
反射不是编译时，而是运行时。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190405091755982.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

这个方法在日常开发的用途很广泛，比如我们平时用的Lombok，或者在ORM框架中，自动生成getter，setter的逻辑里。

反射另外一方面就是可以绕过API，不被API的一些强制约束所限制，自定义的高性能 NIO（New Input/Output） 框架需要显式地释放DirectBuffer（堆外存），使用反射绕开限制是一种常见办法。

> 值得一提的是在Java9中已经出现了限制反射的策略，只有只有当被反射操作的模块和指定的包对反射调用者模块 Open，才能使用setAssible，但是JDK8的也没被抛弃，在后续版本可能就会采用后者。


## 动态代理

在前面为动态代理的出现做了很多铺垫，动态代理是设计模式中的一种，自我感觉它和装饰者模式很相似，但是又更强大，更通用。通过代理可以让调用者和实现者解耦，比如RPC的调用，框架内部的寻址，序列化等一些重复并且麻烦的工作通过代理可以很简单的使用。

在学SpringAop的时候，SpringAOp的两种动态代理实现的方式就是JDKproxy和cglib，为什么有了JDKProxy还要引入cglic呢？因为JDKProxy只能对实现接口的类进行动态代理，在一些方面很大的限制了动态代理的灵活性。而cglib就不需要必须实现接口。

JDK的动态代理只需要使用Proxy.newProxyInstance并实现一个匿名内部类或者是自定义一个InvocationHandler来进行自定义动态代理。可以参考我博客中的对字符编码拦截的动态代理：[https://blog.csdn.net/qq_42605968/article/details/86566681](https://blog.csdn.net/qq_42605968/article/details/86566681)


cglib 动态代理采取的是创建目标类的子类的方式，因为是子类化，我们可以达到近似使用被调用者本身的效果（接口回调）。创建cglib类的核心对象进行操作，详细可以参考我的博客Aop中手动写cglib：[https://blog.csdn.net/qq_42605968/article/details/86694900](https://blog.csdn.net/qq_42605968/article/details/86694900)在文章中又详细的注释，这里就不过多的介绍了。

在AOP中把动态代理体现的淋漓尽致，如下图

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190405105333778.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

> 所谓动态代理，就是实现阶段不用关心代理谁，而是在运行阶段才指定代理哪个一个对象（不确定性）。如果是自己写代理类的方式就是静态代理（确定性）。