# 从源码看集合之Set分支

> Collection大体分为两种实现方式，一种是List，另一种就是Set，接下来就对Set进行分析。

## Set的结构分析

先来看看Set接口的继承关系，是继承于Collection接口的

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190412153729855.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

并且可以从下边构成图来看，它并没有规定自己特有的接口，里边的接口方法都是继承来的

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190412153912781.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

可以看到AbstractSet是继承于AbstractCollection，并实现了Set接口。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190412154231590.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

在AbstractSet进行重写了下边几个方法
```
  //重写了Object的equals方法
  public boolean equals(Object o) {
        //如果o等于当前对象，返回true
        if (o == this)
            return true;
        //如果o不是一个Set类型的，就返回false
        if (!(o instanceof Set))
            return false;
        //将o向上转为Collection，提供更多的调用方法
        Collection<?> c = (Collection<?>) o;
        //大小不相等，返回false
        if (c.size() != size())
            return false;
        try {
            //用containsAll进行测试
            return containsAll(c);
        } catch (ClassCastException unused)   {
            return false;
        } catch (NullPointerException unused) {
            return false;
        }
    }

    //重写了Object的hashCode方法   
    public int hashCode() {
        int h = 0;
        Iterator<E> i = iterator();
        //根据Set中所有元素的hashCode之和生成新的hashcode
        while (i.hasNext()) {
            E obj = i.next();
            if (obj != null)
                h += obj.hashCode();
        }
        return h;
    }

    //重写了AbstractCollection的removeAll
    public boolean removeAll(Collection<?> c) {
        //判空
        Objects.requireNonNull(c);
        //删除的标志
        boolean modified = false;
        //下面的代码就是说，把一个集合中所有和当前Set集合相同的元素都一个个迭代删除
        if (size() > c.size()) {
            for (Iterator<?> i = c.iterator(); i.hasNext(); )
                modified |= remove(i.next());
        } else {
            for (Iterator<?> i = iterator(); i.hasNext(); ) {
                if (c.contains(i.next())) {
                    i.remove();
                    modified = true;
                }
            }
        }
        return modified;
    }

```

## HashSet

HashSet继承于AbstractSet，并且实现了Set，Serialzable，Cloneable接口。HashSet实现Set，因此就不允许有重复元素。HashSet和其他Collection一样，也实现类序列化和克隆接口。它的底层是HashMap，它只是把HashMap的键进行了使用，HashMap的值是一个默认不变的值。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190412161516374.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

HashSet的构造方法

```
    
    //构造一：直接就是一个HashMap
    public HashSet() {
        map = new HashMap<>();
    }
    
    //构造二：传入一个Collection，然后使用HashMap的构造方法（传入Capacity在size*0.75和16选出最大值）
    public HashSet(Collection<? extends E> c) {
        map = new HashMap<>(Math.max((int) (c.size()/.75f) + 1, 16));
        addAll(c);
    }

    //构造三:根据传入初始容量和负载因子来构造HashMap
    public HashSet(int initialCapacity, float loadFactor) {
        map = new HashMap<>(initialCapacity, loadFactor);
    }

    //构造三：根据传入初始Capacity，默认为16，并且默认的loadFactor为0.75.
    public HashSet(int initialCapacity) {
        map = new HashMap<>(initialCapacity);
    }

    //构造四：根据传入初始容量，和负载因子，dummy参数起一个标识的作用，只是用给LinkedHahSet使用的
    HashSet(int initialCapacity, float loadFactor, boolean dummy) {
        map = new LinkedHashMap<>(initialCapacity, loadFactor);
    }

```

可以在下边看到，HashSet就是对HashMap的一个封装，它的底层还是由HashMap实现的。因此，在这块就先说这么多，在说HashMap的时候好好分析。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190412193447635.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

LinkedHashSet

> 它和HashSet有什么不同呢？HashSet是无序的，不能保证输出的顺序和存入的顺序一致，LinkedHashSet就可以保证输入的顺序是什么样的，输出就是什么样的，保证了顺序。

下边是LInkedListSet的UML图，可以看到它是继承于HashSet，也实现类serializable接口和Cloneable和set接口。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190412200744118.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

它里面的方法也基本都是使用HashSet的方法，就提供了几个构造方法。

```

    //构造一：传入初始容量和负载因子，默认调用父类的初始化函数
    public LinkedHashSet(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor, true);
    }

    //构造二：传入初始容量，默认负载因子为0.75
    public LinkedHashSet(int initialCapacity) {
        super(initialCapacity, .75f, true);
    }
    //构造三：空参默认初始容量16，负载因子0.75
    public LinkedHashSet() {
        super(16, .75f, true);
    }

   //构造四：传入一个特定集合，初始容量为max（2*c.size(),11），默认负载因子为0.75
    public LinkedHashSet(Collection<? extends E> c) {
        super(Math.max(2*c.size(), 11), .75f, true);
        addAll(c);
    }


```
在这也就可以看到了在HashSet中专门流出来一个构造方法带有dummy，是为了供LinkedHashMap使用的。

因此LinkedHashMap的实现也在说Map的时候讲。

## SortedSet

SortedSet的底层实现是红黑树实现的，正因为它继承Set，因此它也是不能重复的。并且它还在Set上更进一步，它是一个有序的集合。


下边是SortedSet的UMl，SortedSet是继承于Set接口的一个接口。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190412195414353.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

在它里边定义了一些SortedSet的基本方法名和参数。

SortedSet接口中定义的

```
comparator()//自己定义比较器，对内部元素排序
first()//第一个元素
headSet(E e)//e之前的元素，不包括e
last()//最后一个元素
spliterator()//Java8新增，可分割迭代器
subSet(E e1, E e2)//e1和e2之间的元素
tailSet(E e)//e之后的元素，包括e 

```
SortSet的子类NavigableSet，并在SortedSet的基础上又加了一些方法，并且没有进行实现。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190412203417564.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/201904122038208.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

在TreeSet中还会见到它的。

## TreeSet

> TreeSet的底层是TreeMap实现的，可实现排序，并且不能为空，那为什么在HashSet等一些里面都可以又空值为什么在TreeSet里就不能有空值呢？原因很简单，空值在默认的比较器下不能确定它比谁大或者是比谁小。

如果要在TreeSet中使用空值的话，就在**构造的时候**，传入特定的比较器

方法一:被比较的对象(如学生类)实现Comparable接口,重写CompareTo()方法.--(不允许空值)
方法二:TreeSet的构造方法,传入Comparator接口的实现类,它是重写了compare()方法.--(允许空值)

这些详细的都会在后边介绍到。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190412204238676.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

TreeSet的构造方法

```
    
    private transient NavigableMap<E,Object> m;
    
    //构造一：传入一个实现类NavigableMap的参数，NavigableMap会在后边Map模块说到
    TreeSet(NavigableMap<E,Object> m) {
        this.m = m;
    }
    //构造二：如果不传参数的话，默认是一个TreeMap
    public TreeSet() {
        this(new TreeMap<E,Object>());
    }

    //构造三：传入比较器
    public TreeSet(Comparator<? super E> comparator) {
        this(new TreeMap<>(comparator));
    }
    //构造四：先使用this（）构造出一个TreeMap实例，然后调用addAll函数将传入的特定集合转化为TreeSet
    public TreeSet(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    //构造五：传入一个SortedSet，也就是实现了SortedSet接口的对象
    public TreeSet(SortedSet<E> s) {
        //使用当前传进来的SortedSet的比较器
        this(s.comparator());
        addAll(s);
    }

```

它对Set，NavigableSet和SortedSet的方法都进行了实现，实现这些函数也都很简单，都是调用封装好的函数，并且采用的比较器是默认的比较器（字典比较排序）

它主要是对继承的接口进行了实现，并且加了在TreeMap的基础上加了自己的构造方法。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190412210910524.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)


发现好多的实现都在Map中，因此这就是为什么很多人把Map和集合发放在一块，Map和集合密不可分。

> 下一篇就对第三个模块Deque进行分析，之后就对map进行深度的分析！

