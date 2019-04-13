# 从源码看集合之Queue分支

> 在前面看完了Collection的前两大分支，接下来就看Collection的第三分支Queue。

众所周知，Queue接口是队列的实现规范，在它这里规范了队列实现的基本要求。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190413084947188.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

Queue的分支很多而且在平时也经常使用，它的分支遍布我们身边。


* add：添加一个元素，如果队列已满，抛出IllegalStateException

* offer ：添加一个元素并返回，如果添加成功就返回true，否则就false

* remove：移除队头并返回队头元素，如果队空，就抛出NoSuchElementException

* poll：移除队头并返回队头元素，如果队空返回null

* element：返回队头元素，如果队空抛出NoSuchElementException

* peek：返回队头元素，如果队空就返回null

> 上边这些就是在Queue中规定的基本方法。

> Queue又可以分为两种，一种是单向的队列，一种是双向的队列

## AbstractQueue

> AbstractQueue继承于AbstractCollection，并且实现了Queue接口。

它的作用和前边见到过的抽象类作用一样，就是为了聚合功能，在它内部对继承来的方法进行特定的实现（还是利用基本的方法），当然，它的作用也就和基类一样，它可以为继承它的类实现这一类型的基本方法。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190413091304371.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

可以看到它又很多很多的衍生类，这些基本都是单向队列

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190413091717482.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

那些衍生类都被用于不同的场合都有不同的作用，在集合这块我们着重来分析PriorityQueue（优先队列）

### PriorityQueue

> 所谓优先队列，就是在队列中不仅仅只是FIFO，而且还具有优先级排序。

下边是优先队列的UML，它继承于AbstractQueue，并且实现了Serializable接口。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190413092135131.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

先来看看PriorityQueue中的一些属性

```

    private static final long serialVersionUID = -7720805057305804111L;
     
    //默认容量
    private static final int DEFAULT_INITIAL_CAPACITY = 11;

    //非私有的数组,也可以说是一个最小堆
    transient Object[] queue; // non-private to simplify nested class access

    //堆元素的个数
    private int size = 0;

    //比较器，如果不指定比较器就默认为字典
    private final Comparator<? super E> comparator;

    //这个队列被修改的次数
    transient int modCount = 0; 

```

优先队列的构造方法

```
    //构造一：无参，就默认容量11，默认使用字典排序
    public PriorityQueue() {
        this(DEFAULT_INITIAL_CAPACITY, null);
    }
   
    //构造二：传入初始容量参数，默认字典排序
    public PriorityQueue(int initialCapacity) {
        this(initialCapacity, null);
    }

    //构造三：传入特定比较器，使用默认的容量11
    public PriorityQueue(Comparator<? super E> comparator) {
        this(DEFAULT_INITIAL_CAPACITY, comparator);
    }

    //构造四：传入初始容量和特定比较器
    public PriorityQueue(int initialCapacity,
                         Comparator<? super E> comparator) {
        //对指定的输入进行非法操作判断
        if (initialCapacity < 1)
            throw new IllegalArgumentException();
        //可以看出，优先队列的底层是实现是数组
        this.queue = new Object[initialCapacity];
        //替换默认比较器
        this.comparator = comparator;
    }

    //构造五：传入一个特定集合，如果是内部有比较器的集合，就使用它自己的比较器，如果是其它没有比较器的集合就使用默认的比较器
    @SuppressWarnings("unchecked")

    public PriorityQueue(Collection<? extends E> c) {
        //判断是不是SortedSet，方便取内部的比较器并进行构造
        if (c instanceof SortedSet<?>) {
            SortedSet<? extends E> ss = (SortedSet<? extends E>) c;
            this.comparator = (Comparator<? super E>) ss.comparator();
            initElementsFromCollection(ss);
        }
        //判断是不是优先队列，也是取比较器之后再进行构造
        else if (c instanceof PriorityQueue<?>) {
            PriorityQueue<? extends E> pq = (PriorityQueue<? extends E>) c;
            this.comparator = (Comparator<? super E>) pq.comparator();
            initFromPriorityQueue(pq);
        }
        //如果是一个不带排序的集合，那就采取默认的比较器，并且进行构造
        else {
            this.comparator = null;
            initFromCollection(c);
        }
    }

     //构造六：如果可以确定是优先队列就可以传进来快速构造
      @SuppressWarnings("unchecked")
      public PriorityQueue(PriorityQueue<? extends E> c) {
          this.comparator = (Comparator<? super E>)     c.comparator();
          initFromPriorityQueue(c);
      }
 
      //构造七：如果确定是一个SortedSet就可以对其进行快速构造  
     @SuppressWarnings("unchecked")
     public PriorityQueue(SortedSet<? extends E> c) {
          this.comparator = (Comparator<? super E>)c.comparator();
          initElementsFromCollection(c);
    }

```

在上边的构造方法中也有对优先队列的初始化函数，接下来就看看优先队列的初始化函数


```
   //初始化一：使用传进来的优先队列进行初始化
   private void initFromPriorityQueue(PriorityQueue<? extends E> c) {
        //如果是一个优先队列
        if (c.getClass() == PriorityQueue.class) {
            //把这个优先队列转化为数组并赋值给前边说到过的Object[] queue
            this.queue = c.toArray();
            //并且大小也传进新的优先队列
            this.size = c.size();
        } else {
            //如果不是一个优先队列的化，那就退回initFromColllection
            initFromCollection(c);
        }
    }

    //初始化二：使用普通集合来初始化
    private void initElementsFromCollection(Collection<? extends E> c) {
        //先转化为数组
        Object[] a = c.toArray();
        //如果是数组的话，那就对它数组进行拷贝
        if (a.getClass() != Object[].class)
            a = Arrays.copyOf(a, a.length, Object[].class);
        int len = a.length;
        //不允许有空元素
        if (len == 1 || this.comparator != null)
            for (int i = 0; i < len; i++)
                if (a[i] == null)
                    throw new NullPointerException();
        this.queue = a;
        this.size = a.length;
    }

    //初始化三：先调用初始化元素，然后在整个树中建立一个不变的堆
    private void initFromCollection(Collection<? extends E> c) {
        initElementsFromCollection(c);
        heapify();
    }


```
优先队列的扩充（是根据二叉树的插入来实现的）

```
   //扩充函数，传入一个最小的扩充量，以便于在空间不足的情况下取舍
   private void grow(int minCapacity) {

        int oldCapacity = queue.length;
        // Double size if small; else grow by 50%
        //如果oldCapacity小于64，那newCapacity就等于2*oldCapacity+2
        //如果oldCapacity大于64，那newCapacity就等于1.5+oldCapicity
        int newCapacity = oldCapacity + ((oldCapacity < 64) ?
                                         (oldCapacity + 2) :
                                         (oldCapacity >> 1));
        //如果新的容量大于最大值，也就是Integer.MAX_VALUE - 8
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            //根据minCapacity和hugeCapacity来指定新容量，最大容量为Integer.MAX_VALUE （0x7fffffff）
            newCapacity = hugeCapacity(minCapacity);
        //然后拷贝到Object[]中
        queue = Arrays.copyOf(queue, newCapacity);
    }
     
    //对大容量进行操作
    private static int hugeCapacity(int minCapacity) {
        //小于0，抛异常
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        //如果最小扩充量大于数组规定最大值，那就给它最大的空间（Integer的大小），否则就是MAX_ARRAY_SIZE(Integer大小-8)
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }

```
还有一些remove，index等一些简单方法就不多说了，下边就再来来看看在优先队列中定义有堆的方法


讲到这里就必须说一说，优先队列的实现也就是最小堆，实际上也就是二叉树，二叉树的父节点都要比子节点小，这样就组成了一个最小堆，当然也有最大堆。



```

    //上浮，使用比较器比较
    private void siftUp(int k, E x) {
        if (comparator != null)
            //构造器不空，如果父节点小于子节点就交换（使用comparator的compare方法）
            siftUpUsingComparator(k, x);
        else
            //否则就使用（comparable的comparaTo方法）
            siftUpComparable(k, x);
    }

    @SuppressWarnings("unchecked")
    private void siftUpComparable(int k, E x) {
        Comparable<? super E> key = (Comparable<? super E>) x;
        while (k > 0) {
            //得到父节点
            int parent = (k - 1) >>> 1;
            Object e = queue[parent];
            //使用Comparable的compareTo
            if (key.compareTo((E) e) >= 0)
                break;
            queue[k] = e;
            k = parent;
        }
        queue[k] = key;
    }

    @SuppressWarnings("unchecked")
    private void siftUpUsingComparator(int k, E x) {
        while (k > 0) {
            //得到父节点
            int parent = (k - 1) >>> 1;
            Object e = queue[parent];
            //如果父节点小于等于子节点就交换
            if (comparator.compare(x, (E) e) >= 0)
                break;
            queue[k] = e;
            k = parent;
        }
        queue[k] = x;
    }

    //下浮，它的作用和siftUp相反，它是把父节点大于子节点的给沉下来，具体步骤和上边差不多
    private void siftDown(int k, E x) {
        if (comparator != null)
            //使用比较器
            siftDownUsingComparator(k, x);
        else
            //使用默认的Comparable的方法
            siftDownComparable(k, x);
    }

    @SuppressWarnings("unchecked")
    private void siftDownComparable(int k, E x) {
        Comparable<? super E> key = (Comparable<? super E>)x;
        int half = size >>> 1;        // loop while a non-leaf
        while (k < half) {
            //得到左孩子
            int child = (k << 1) + 1; // assume left child is least
            Object c = queue[child];
            //得到右孩子
            int right = child + 1;
            if (right < size &&
                ((Comparable<? super E>) c).compareTo((E) queue[right]) > 0)
                c = queue[child = right];
            if (key.compareTo((E) c) <= 0)
                break;
            queue[k] = c;
            k = child;
        }
        queue[k] = key;
    }

    @SuppressWarnings("unchecked")
    private void siftDownUsingComparator(int k, E x) {
        int half = size >>> 1;
        while (k < half) {
            int child = (k << 1) + 1;
            Object c = queue[child];
            int right = child + 1;
            if (right < size &&
                comparator.compare((E) c, (E) queue[right]) > 0)
                c = queue[child = right];
            if (comparator.compare(x, (E) c) <= 0)
                break;
            queue[k] = c;
            k = child;
        }
        queue[k] = x;
    }

    //对最小堆中的节点维护，让它保持最小堆的状态
    @SuppressWarnings("unchecked")
    private void heapify() {
        for (int i = (size >>> 1) - 1; i >= 0; i--)
            siftDown(i, (E) queue[i]);
    }


```

> 优先队列就到这里的了，可以通过上浮和下沉来调整优先级。

## Deque

Deque是Queue的子类，直接继承于Queue，并且在Queue的基础上加了自己特有的方法接口。

可以看到在Queue的基础上加入了双端的特性方法

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190413111755326.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

队列在JDK中有两种实现方式，一个是基于数组的，另外一个是基于链表的

### ArrayDeque

先看看它的属性

```

    //用来存储双端队列的数组
    transient Object[] elements; // non-private to simplify nested class access
    
    //双端队列的头
    transient int head;

    //双端队列的尾
    transient int tail;
    
    //最小初始容量为8，注意在双端队列不能出现队满的状态，并且每次扩充要是2的倍数
    private static final int MIN_INITIAL_CAPACITY = 8;


```

构造方法

```
    //构造一：默认构造长度为16的数组
    public ArrayDeque() {
        elements = new Object[16];
    }

    //构造二：指定大小，分配的空间不能小于8
    public ArrayDeque(int numElements) {
        allocateElements(numElements);
    }

    //构造三：传入一个集合
    public ArrayDeque(Collection<? extends E> c) {
        allocateElements(c.size());
        addAll(c);
    }

```

分配空间,这也就是为什么它的倍数要是2的倍数，因为它使用的是移位操作。这个算法展现了作者强悍的二进制功底。

```

 //计算大小
 private static int calculateSize(int numElements) {

        //最小初始容量
        int initialCapacity = MIN_INITIAL_CAPACITY;
        //当指定的容量大于最小初始容量，进行一次二进制移位扩充
        if (numElements >= initialCapacity) {
            initialCapacity = numElements;
            initialCapacity |= (initialCapacity >>>  1);
            initialCapacity |= (initialCapacity >>>  2);
            initialCapacity |= (initialCapacity >>>  4);
            initialCapacity |= (initialCapacity >>>  8);
            initialCapacity |= (initialCapacity >>> 16);
            //重要的一步，在所有位为1的时候+1，取得进位，并且保证是最高位
            initialCapacity++;

            if (initialCapacity < 0)   // Too many elements, must back off
                initialCapacity >>>= 1;// Good luck allocating 2 ^ 30 elements
        }
        return initialCapacity;
    }

    //扩充
    private void allocateElements(int numElements) {
        elements = new Object[calculateSize(numElements)];
    }

```

这个是一个很好的算法，因此我给出测试代码和我自己的纸质分析

```
 public static void main(String[] args) {

        int initialCapacity = 18;
        System.out.println("初始值："+initialCapacity);

        System.out.println(Integer.toBinaryString(initialCapacity) + "(initialCapacity)");

        initialCapacity |= (initialCapacity >>> 1);

        System.out.println(Integer.toBinaryString(initialCapacity) + "(initialCapacity)");

        initialCapacity |= (initialCapacity >>> 2);

        System.out.println(Integer.toBinaryString(initialCapacity) + "(initialCapacity)");

        initialCapacity |= (initialCapacity >>> 4);

        System.out.println(Integer.toBinaryString(initialCapacity) + "(initialCapacity)");

        initialCapacity |= (initialCapacity >>> 8);

        System.out.println(Integer.toBinaryString(initialCapacity) + "(initialCapacity)");

        initialCapacity |= (initialCapacity >>> 16);

        initialCapacity++;
        System.out.println(initialCapacity);

    }


```
初始值为18（结果不对）

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190413165344132.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

初始值为16（结果正确）

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190413165258638.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

手动分析结果

![在这里插入图片描述](https://img-blog.csdnimg.cn/201904131657599.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

其余的方法都很简单包括它实现Deque接口的方法

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190413170059806.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)


### 基于LinkedList

那就只能说CurrentLinkedDeque，但是它不在集合下，在utils下，下次在分析utils的时候再对它进行分析

它的UML，可以看出继承于AbstractCollection，实现类Deque和Serializable 

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190413170712592.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

Queue分支就先到分析到这里。

