# 几类常用的集合框架对比

## Vector、ArrayList、LinkedList

> 我们可能经常用vector，ArrayList，Linklist，但是你是真的了解它的底层实现吗？接下来就深度了解一下Vectory，ArrayList和LinkedList。

### 简单功能对比

* Vector

Vector是早期的线程安全的动态数组，因为线程安全因此它的开销也比较大，它的内部是采用对象数组来实现的，可以自动的扩充容量（当数组满了的时候），每次扩充的大小是原来大小的一倍。

* ArrayList

ArrayList是由动态数组来实现的，并且它也不是线程安全的，因此它的效率就比Vector高很多，并且ArrayList也可以自动扩容，它每次扩容的大小是原来的50%。

* LinkedList

LinkedList是由Java的双向链表实现的，并且它也不是线程安全的，正因为它是双向链表，因此它就不用考虑扩充问题。

几种应用场景：

如果是对于随机访问要求比较高的话，并且插入删除较少（除过在尾部插入删除），那就应该在Vector和ArrayList中选择。

如果是对于数据的插入，删除需求较多，对随机访问需求较少，那就采用LinkedList作为实现。

## Hashtable、HashMap、TreeMap、LinkedHashMap、CurrentHashMap

> 这几种集合框架在日常也会经常用到，在于实现不同的功能来选择不同的实现。下边就对这几个进行简单的对比。

* HashTable

HashTable是Java早期的哈希表的实现，是同步的，因此它的开销也比较大，并且在HashTable中不能由空的键和值。

* HashMap

HashMap是现在更广泛的哈希表的实现，它和HashTable实现的功能大致形同，比较明显的区别就是HashMap是不同步的并且支持空的键和值，它的get和put操作在大多数情况下都可以达到常数级别。它的实现是散列表+红黑树。负载因子为0.75.散列容量大于64且链表大于8时，转为红黑树。

* TreeMap

TreeMap是一种基于红黑树的一种提供顺序访问的Map，它和HashMap不同，它的get和put，remove时间复杂度都是O（log（n）），具体的排序可以根据Comparable接口来决定。

* LinkedHashMap

LinkedHashMap允许键和值为空，并且大多方法也和HashMap中方法相同，并且在HashMap基础内部重写了某些方法，维护了双向链表。

* CurrentHashMap

CurrentHashMap的键和值都不能为空，底层是散列表加红黑树，是线程安全的，利用CAS算法和一些其他操作加锁实现的。

## HashSet、linkedHashSet、TreeSet

* HashSet底层是哈希表+红黑树，可以为空，也就是对HashMap进行了封装。

* LinkedHashSet底层是由哈希表+双向链表，可以为空，它的父类是HashSet，因为HashSet是由HashMap封装而来的，所以它和LinkedHashMap也是高度相似。

* TreeSet

说了前几个都和Map有关系，这个也和TreeMap有关系，底层是一个TreeMap，因此也不为空，可以进行排序。

> 顺便说一说，在网上流传的重写equals和hashcode，这个东西如果是新手解除Java的话就很无解，在加上有些人不分情况的乱说导致感觉这俩的关系很难搞定。

从本质上说，无论是重写eqluals还是重写hashcode，目的都是为了对目标对象进行等价判断。

那问题来了，为什么有时候要让重写equals方法，有时候又让重写equals和hashcode方法，这是怎么一回事啊？

使用重写hashcode的方法其实是对比较对象的一种优化，需要比较什么东西，就用它的特征变量来生成哈希码，然后通过比较哈希码来确定是不是同一个对象，如果哈希码不同，那就不用判断了，肯定不一样，如果哈希码相同，接下来就到equals出场了，因此在一些Map中，Set等，就可以使用重写hashcode来优化对象的比较。

总的来说：

* 在普通情况下，使用equals方法进行自定义对象判断
* 在一些采用哈希的情况下，就可以使用特定值的哈希码来高效的完成任务。

这一部分，网上例子很多我也就不写实例了，找了一个比较不错的可以用来参考

https://www.cnblogs.com/keyi/p/7119825.html

> 下来一篇就对集合这一块结合源码详细的分析一下。