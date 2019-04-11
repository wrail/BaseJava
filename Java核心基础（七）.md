# 从源码看集合之List分支

> 对集合进行深度分析，从源码看集合！

在进入分析之前，先来看看Collection的整体结构，就先从这张图开始。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190410215732940.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

可以看到Collction接口分别被List，Set，Queue继承，并且Deque接口有也是继承于Queue接口。

接下来就分模块对Collection进行解读

## Collection

> 在Collection中规定了一系列的规范方法，是整个集合的根接口。它规定着一个集合必须要有的一系列方法。并且它也是继承于iterable（可进行迭代）接口。

说到这里那就把迭代器也顺带说一说

> iterable:集合继承这个接口代表的是此集合是可进行迭代的，需要传入一个iterator。
> iterator：见名知意就是迭代的执行者。

此图在iterable中有一个Iterator并且不能为空，那就说明需要在实现iterable的类中传入iterator才可以使用迭代器。至于怎样进行迭代还是归iterator来管的，因此在iterator中也会有默认的迭代方法。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190410221730847.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190410222226434.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

并且还可以看到迭代器中remove方法是用来删除最近一次迭代的元素，也就是上一次迭代出的元素，因此不能直接执行remove，必须先next后，才能使用remove移除上一个元素，否则就报异常。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190410222318431.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

也正因为如此，在每一种不同的集合中基本都有自己特定实现的迭代器，比如我在AbstractList找它的迭代器实现如下图

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190410223326996.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

看它的ListIterator中对index进行非法判断后返回一个ListItr（是AbstractList的内部类），ListIter是继承于Iter（是对iterator的直接实现，并且是AbstractList的内部类），因此在Iter基础上加上List的特性。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190410223502897.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190410223837296.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

具体的实现如下（是Iter类中的，这样子类就可以使用Iter中的指针操作）

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190410224106619.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

可以看到ListIter根据Iter中的指针操作来实现迭代器的功能

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190410224649555.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

> 关于迭代器就先说到这里，后边会对迭代器进行手动实现和对迭代器的设计模式进行分析。

Collection的抽象类，是对Collection接口的抽象的弱化，会对其中一些方法进行选择性实现。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190410225916474.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

## Collections

> Collections是一个实体类，它完全由操作和静态方法构成，因此可以把它看作一个Collection工具类。

下面就挑一些常用的来聊一聊

下边是一些操作的阈值是一个不可变的量，比如下边第一个是二分查找的阈值，这些在后边用到的时候再说。

```
public class Collections {

    //值得注意的是，这个构造方法是一个私有的，因此这个Collections对象就不能被实例化。
    private Collections() {
    }
    private static final int BINARYSEARCH_THRESHOLD   = 5000;
    private static final int REVERSE_THRESHOLD        =   18;
    private static final int SHUFFLE_THRESHOLD        =    5;
    private static final int FILL_THRESHOLD           =   25;
    private static final int ROTATE_THRESHOLD         =  100;
    private static final int COPY_THRESHOLD           =   10;
    private static final int REPLACEALL_THRESHOLD     =   11;
    private static final int INDEXOFSUBLIST_THRESHOLD =   35;
    ...

```

它里边的方法是各式各样，下边就挑几个常用的来说一说

Collections.sort

```
    //传入List，按照升序排序
    public static <T extends Comparable<? super T>> void sort(List<T> list) {
        list.sort(null);
    }

   //传入一个List和一个Comparator（用于特定排序的比较）
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> void sort(List<T> list, Comparator<? super T> c) {
        list.sort(c);
    }


```
也就是说Collections中的sort还是采用的List.sort（），要能使用默认（升序）的sort必须要实现comparable，这也就和前边对应起来了。

然后调用List的sort，如下可以实现特定的比较方法，因为根据有没有自定义Comparator，没有自定义的话就是上边Collections.sort的第一种sort情况。

```
  //list中的sort
  default void sort(Comparator<? super E> c) {
        //先把List对象转为数组
        Object[] a = this.toArray();
        //然后调用Arrays.sort
        Arrays.sort(a, (Comparator) c);
        //迭代
        ListIterator<E> i = this.listIterator();
        for (Object e : a) {
            i.next();
            i.set((E) e);
        }
    }

```

那Arrays的sort是采用的什么实现的呢？

```

 public static <T> void sort(T[] a, Comparator<? super T> c) {
        //对有没有进行自定义比较器进行判断
        if (c == null) {
            sort(a);
        } else {
            //userRequest是一个布尔标记，如果为true的话就采用legacyMergeSort
            if (LegacyMergeSort.userRequested)
                legacyMergeSort(a, c);
            //默认的是TimSort，TimSort是一种高效的排序方法，结合和归并排序和插入排序。这个排序的源码会在以后进行讲解。
            else
                TimSort.sort(a, 0, a.length, c, null, 0, 0);
        }
    }

```

Collections.binarySearch()

Collection中的二分查找，传进去的值要是已经有序的List

```

 //传入一个List，key为要查找的元素
 int binarySearch(List<? extends Comparable<? super T>> list, T key) {
        //判断是否实现了RandomAccess，并且list的size小于阈值
        if (list instanceof RandomAccess || list.size()<BINARYSEARCH_THRESHOLD)
         //比较适用于元素比较少的list，索引二分查找
            return Collections.indexedBinarySearch(list, key);
        else
          //迭代二分查找
            return Collections.iteratorBinarySearch(list, key);
    }


  //索引二分查找
    private static <T>
    int indexedBinarySearch(List<? extends Comparable<? super T>> list, T key) {
        int low = 0;
        int high = list.size()-1;
        //进行二分判断
        while (low <= high) {
            //如果条件满足，（low+high）/2，使用移位操作效率高直接操作二进制
            int mid = (low + high) >>> 1;
            //根据索引找出midValue，这就是为什么要传入有序的List进来，如果List有序，那下标也有序的
            Comparable<? super T> midVal = list.get(mid);
            //让key和midVar比较
            int cmp = midVal.compareTo(key);
            //进行二分操作
            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        //如果high<low了，那就说明没找到！
        return -(low + 1);  // key not found
    }

    //迭代二分查找，这种比价适合链表查找，因此LinkedList都没有实现RandomAccess接口，因为它们没有下标这一说法
    private static <T>
    int iteratorBinarySearch(List<? extends Comparable<? super T>> list, T key)
    {
        int low = 0;
        int high = list.size()-1;
        //得到一个list的迭代器对象
        ListIterator<? extends Comparable<? super T>> i = list.listIterator();
        //条件判断
        while (low <= high) {
            //无符号移位
            int mid = (low + high) >>> 1;
            //从迭代器i中，取第mid个的值（在已经排序好的List中也就是中值）
            Comparable<? super T> midVal = get(i, mid);
            //进行比较
            int cmp = midVal.compareTo(key);
            //二分
            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found
    }

```

Collections.reverse()

对List进行反转操作

```
 
 public static void reverse(List<?> list) {
        //拿到List的大小
        int size = list.size();
        //对大小进行阈值判断不能超过18或者看是不是RandomAccess的子类
        if (size < REVERSE_THRESHOLD || list instanceof RandomAccess) {
            //如果条件满足就进行前后调换 mid是通过size/2，有符号右移（如果负数就高位补1，正数高位补0，这样就保证了符号不变性），不理解为什么这里要用有符号右移（难道size还可能为负数码？或者是为负数还能进行reserve？）
            for (int i=0, mid=size>>1, j=size-1; i<mid; i++, j--)
                //交换
                swap(list, i, j);
        } else {
            //生成list的迭代器对象，主要是针对LinkedList
            ListIterator fwd = list.listIterator();
            //生成一个reverse迭代器对象，并且指定方法返回的第一个索引是size（也就是首地址）
            ListIterator rev = list.listIterator(size);
            for (int i=0, mid=list.size()>>1; i<mid; i++) {
                //使用next进行迭代
                Object tmp = fwd.next();
                //因为规定的首地址是size，因此移动rev的前驱指针，并且将tmp set到rev的前驱指针的位置上
                fwd.set(rev.previous());
                rev.set(tmp);
            }
        }
    }

```

> Collections中的常用方法就到这里


在下面分的模块中没有Queue，因此Queue在特殊用途会有特殊的实现。

## List模块

> List集合是Collection的一个重要子类，它里面有很多我们常用的实现方法，接下来会一步步的进入源码分析它们各自的实现。

现通过下图分析一下List接口的构成，它继承了Collection，并且在Collection的基础上添加了属于自己的抽象方法，如addAll，get等等。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190410230146918.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

接下来再看看它的List接口的弱抽象AbstractList

刚刚在上边看到过AbstractList，再AbstractList中继承了AbstractCollection并且实现了List接口。并且对List接口进行了实现作为List的基类。并且也可以看到AbstractList中对List的实现其实是一个假实现（对于一些特定结构的方法实现了也没有意义，因为这些特定方法不是通用的，一些通用的方法在抽象类基本都会进行实现的），主要的实现还是得AbstractList的子类来实现。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190410231112855.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

### ArrayList

ArrayList的结构图，继承于AbstractList，实现可克隆，随机存取（其只要List集合实现这个接口，就能支持快速随机访问，里面其实就只是一个标志），序列化的接口。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190410232038453.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

> 一篇解释RandomAccess很通俗易懂的博客（虽然没我上边RandomAccess将的好）：https://blog.csdn.net/weixin_39148512/article/details/79234817

先来看一看ArrayList的构造方法

```
   //第一种构造方法，指定初始化容量（就个c语言中的指定容量分配空间一样）
   public ArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        }
    }

    //第二种，不指定初始化空间的话就使用默认的空间为10
    public ArrayList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }
   
    //第三种，传入一个集合
    public ArrayList(Collection<? extends E> c) {
        //先转化为数组，因为ArrayList底层是数组
        elementData = c.toArray();
        //条件判断
        if ((size = elementData.length) != 0) {
            // c.toArray might (incorrectly) not return Object[] 
            if (elementData.getClass() != Object[].class)
                //如果返回的不是一个数组，那就拷贝一份 ，Object[].class就是当前数组对象的class，也就是传进来的参数的数组形式的字节码对象，防止字节码转化出错。
                elementData = Arrays.copyOf(elementData, size, Object[].class);
        } else {
            // replace with empty array.
            this.elementData = EMPTY_ELEMENTDATA;
        }
    }


```

下边是对ArrayList中的容量进行预先设置大小和计算容量函数

```

 //该方法的作用是预先设置Arraylist的大小，这样可以大大提高初始化速度。 
 public void ensureCapacity(int minCapacity) {
        //使用正则给minExpand赋值
        int minExpand = (elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA)
            // any size if not default element table
            ? 0
            // larger than default for default empty table. It's already
            // supposed to be at default size.
            : DEFAULT_CAPACITY;

        if (minCapacity > minExpand) {
            ensureExplicitCapacity(minCapacity);
        }
    }

    private static int calculateCapacity(Object[] elementData, int minCapacity) {
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            return Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        return minCapacity;
    }


```

ArrayList的toArray

```
 //底层是利用的拷贝,返回此对象数组
 public Object[] toArray() {
        return Arrays.copyOf(elementData, size);
    }

 //规定了泛型数组，因此返回的也是此泛型的数组，也是利用的对象拷贝
 public <T> T[] toArray(T[] a) {
        if (a.length < size)
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(elementData, size, a.getClass());
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

```
Arrays中的拷贝函数

```
   //根据传进去的数组在字节码上进行拷贝，返回的是一个Object[]
   public static <T> T[] copyOf(T[] original, int newLength) {
        return (T[]) copyOf(original, newLength, original.getClass());
    }


```
get和set

因为底层是数组，所以get和set很简单

```
   public E get(int index) {
        rangeCheck(index);

        return elementData(index);
    }

    public E set(int index, E element) {
        rangeCheck(index);

        E oldValue = elementData(index);
        elementData[index] = element;
        return oldValue;
    }


```

add方法

```
//第一种不指定位置到数组末端
public boolean add(E e) {
        //进行容量判断
        ensureCapacityInternal(size + 1);
        //加入到数组末端 
        elementData[size++] = e;
        return true;
    }
//第二种指定位置（因为不是插到尾部所以需要拼接拷贝）
    public void add(int index, E element) {
        //如果index>size或者index<0都会抛出异常
        rangeCheckForAdd(index);
        //检查空间
        ensureCapacityInternal(size + 1); 
        System.arraycopy(elementData, index, elementData, index + 1,
                         size - index);
        elementData[index] = element;
        //容量+1 
        size++;
    }


```

remove


```
 //第一种，移除并返回被移除的元素
 public E remove(int index) {
        //进行非法检查
        rangeCheck(index);
 
        modCount++;
        //取出index的值
        E oldValue = elementData(index);
        //下标最大-1
        int numMoved = size - index - 1;
        //进行拼接拷贝  
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
        //赋值为空，加快GC
        elementData[--size] = null; // clear to let GC do its work
        //返回移除元素
        return oldValue;
    }
//第二种，返回布尔类型，流程和上边差不多
    public boolean remove(Object o) {
        if (o == null) {
            for (int index = 0; index < size; index++)
                if (elementData[index] == null) {
                    //就是不用返回被删除元素，直接删除达到快速的效果
                    fastRemove(index);
                    return true;
                }
        } else {
            for (int index = 0; index < size; index++)
                if (o.equals(elementData[index])) {
                    fastRemove(index);
                    return true;
                }
        }
        return false;
    }


```
fastRemove

```
 //注意是一个private的，源码就是remove第一种的一部分
 private void fastRemove(int index) {
        modCount++;
        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
        elementData[--size] = null; // clear to let GC do its work
    }


```

addAll

```

 //第一种，和add方法大致相同
 public boolean addAll(Collection<? extends E> c) {
        //转为数组
        Object[] a = c.toArray();
        int numNew = a.length;
        ensureCapacityInternal(size + numNew);  // Increments modCount
        //拷贝
        System.arraycopy(a, 0, elementData, size, numNew);
        size += numNew;
        return numNew != 0;
    }

 //第二种，在指定位置添加一个集合进去，和add的第二种一样还是利用的拷贝。
    public boolean addAll(int index, Collection<? extends E> c) {
        rangeCheckForAdd(index);

        Object[] a = c.toArray();
        int numNew = a.length;
        ensureCapacityInternal(size + numNew);  // Increments modCount

        int numMoved = size - index;
        if (numMoved > 0)
            System.arraycopy(elementData, index, elementData, index + numNew,
                             numMoved);

        System.arraycopy(a, 0, elementData, index, numNew);
        size += numNew;
        return numNew != 0;
    }

```

在ArrayList种存在一个子类，那就是subList，什么是subList，它其实可以认为是生成一个List的视图。这样我们就先从ArrayList里的subList方法说起。

ArrayList的subList方法

```
  //生成一个从 formindex到toIndex的一个List视图
  public List<E> subList(int fromIndex, int toIndex) {
        //非法检查
        subListRangeCheck(fromIndex, toIndex, size);
        //根据当前的List（this）作为parent生成一个subList
        return new SubList(this, 0, fromIndex, toIndex);
    }


```

再看看ArrayList里的subList子类

```
 private class SubList extends AbstractList<E> implements RandomAccess {

        //视图的父节点
        private final AbstractList<E> parent;
        //父节点偏移量
        private final int parentOffset;
        //偏移量
        private final int offset;
        //大小
        int size;
        //构造方法，指定父节点和偏移量等
        SubList(AbstractList<E> parent,int offset, int fromIndex, int toIndex) {
            this.parent = parent;
            this.parentOffset = fromIndex;
            this.offset = offset + fromIndex;
            this.size = toIndex - fromIndex;
            this.modCount = ArrayList.this.modCount;
        }
        //在subList里的方法（如get，set，getall等）和前面get，set实现都差不多
        
        //set方法
        public E set(int index, E e) {
            rangeCheck(index);
            checkForComodification();
            E oldValue = ArrayList.this.elementData(offset + index);
            ArrayList.this.elementData[offset + index] = e;
            return oldValue;
        }

        //get方法
        public E get(int index) {
            rangeCheck(index);
            checkForComodification();
            return ArrayList.this.elementData(offset + index);
        }

        //add方法
        public void add(int index, E e) {
            rangeCheckForAdd(index);
            checkForComodification();
            parent.add(parentOffset + index, e);
            this.modCount = parent.modCount;
            this.size++;
        }

        //remove方法
        public E remove(int index) {
            rangeCheck(index);
            checkForComodification();
            E result = parent.remove(parentOffset + index);
            this.modCount = parent.modCount;
            this.size--;
            return result;
        }

        ....
        ....

}

```

ArrayList.sort()

底层还是利用的Arrays.sort

```
  public void sort(Comparator<? super E> c) {
        //modCount是记录被修改的次数
        final int expectedModCount = modCount;
        //排序
        Arrays.sort((E[]) elementData, 0, size, c);
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
        modCount++;
    }

```

trimToSize

是用来在一些急需内存的情况下，缩减内存的方案，比如我们初始申请10个空间，我们插入11个数，它就会动态增长空间，因此空间占用到达了15，但是实际只用了11，因此它为了节约内存就把多余的空间剔除了。

(别人测试博客链接)[https://blog.csdn.net/f641385712/article/details/82347045]

```

  public void trimToSize() {
        modCount++;
        if (size < elementData.length) {
            elementData = (size == 0)
              ? EMPTY_ELEMENTDATA
              : Arrays.copyOf(elementData, size);
        }
    }

```

> 在ArrayList中也实现了ArrayListSpliterator，Splititerator
是JDK8出的迭代器，可以对channel，stream，Collections等进行迭代，并且支持并行。这个在Collection中都基本由实现，在这块先不说，后边单独说一说。

### Vector

Vector继承于AbstractList，并且也实现了List<E>, RandomAccess, Cloneable, java.io.Serializable接口。底层也是通过数组实现。

Vector的UMl图

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190411205433651.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

Vector的几种构造方法,这些构造方法和ArrayList都是大同小异

```
   //构造1：通过输入初始容量和如果超出容量每次增加的容量大小，如果capacityIncrement<=0,那根据默认如果超出每次 增加一倍容量
   public Vector(int initialCapacity, int capacityIncrement) {
        super();
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        this.elementData = new Object[initialCapacity];
        this.capacityIncrement = capacityIncrement;
    }

   //构造2：只传初始化大小，内部默认capacityincrement为0，也就是说如果超出Vector大小的话，每次增加一倍容量
   public Vector(int initialCapacity) {
        this(initialCapacity, 0);
    }
    //构造3：什么都不传，默认生成空间是10
    public Vector() {
        this(10);
    }
    //构造4：传入一个Collection，和ArrayList的构造方法基本相似
    public Vector(Collection<? extends E> c) {
        //先转为数组
        elementData = c.toArray();
        //得到长度
        elementCount = elementData.length;
        // c.toArray might (incorrectly) not return Object[] (see 6260652)
        //也就是说在数组转化中可能会出错，如果出错了那就拷贝一份给elementData
        if (elementData.getClass() != Object[].class)
            elementData = Arrays.copyOf(elementData, elementCount, Object[].class);
    }

```

copyInto（）

实现的功能就是将Vector拷贝为其他的特定的Array。在后边其实你也会发现在Vector中的方法基本都带有Synchronize，保证的线程安全。

```
 public synchronized void copyInto(Object[] anArray) {
        System.arraycopy(elementData, 0, anArray, 0, elementCount);
    }


```
trimToSize()

和ArrayList的一样，是用来在一些急需内存的情况下，缩减内存的方案，比如我们初始申请10个空间，我们插入11个数，它就会动态增长空间，因此空间占用到达了20，但是实际只用了11，因此它为了节约内存就把多余的空间剔除了。

```
 public synchronized void trimToSize() {
        //记录修改的次数
        modCount++;
        //如果实际空间小于总空间
        int oldCapacity = elementData.length;
        if (elementCount < oldCapacity) {
        //那就把实际的空间重新拷贝到elementData
            elementData = Arrays.copyOf(elementData, elementCount);
        }
    }


```

ensureCaoacity和ArryList差不多，都是预先设置容量，提高初始化速度，不用再一直进行扩充

```

  public synchronized void ensureCapacity(int minCapacity) {
        if (minCapacity > 0) {
            modCount++;
            ensureCapacityHelper(minCapacity);
        }
    }
   //根据最小的容量来增加容量，也就是在ensureCapacity计算后然后进行最小的容量增加
    private void ensureCapacityHelper(int minCapacity) {
        // overflow-conscious code
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }

```
grow

进行扩充操作

```
private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
        //如果capacity增量大于0就使用增量扩充
        int newCapacity = oldCapacity + ((capacityIncrement > 0) ?
                                         capacityIncrement : oldCapacity);
        //如果小于等于0就扩大一倍
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        elementData = Arrays.copyOf(elementData, newCapacity);
    }


```

lastIndexOf

用来组合查找上一次Object o 出现的位置

```

    //只传入要查找的对象的话，默认从最后一个开始找
    public synchronized int lastIndexOf(Object o) {
        return lastIndexOf(o, elementCount-1);
     }

    //传入对象，并传入从index开始从后往前找
    public synchronized int lastIndexOf(Object o, int index) {
        if (index >= elementCount)
            throw new IndexOutOfBoundsException(index + " >= "+ elementCount);

        if (o == null) {
            for (int i = index; i >= 0; i--)
                if (elementData[i]==null)
                    return i;
        } else {
            //倒序查找
            for (int i = index; i >= 0; i--)
                if (o.equals(elementData[i]))
                    return i;
        }
        return -1;
    }

```

> Vector这些已经分析的差不多了 现在已经不太用了，因此稍微了解一下底层的扩充机制和设计方法就ok


接下来看一看继承于Vector的Stack

> Stack是一种常见的数据结构，它在很多时候很有用，因此把它也就放在的util包下

下边是栈的一些方法和成员变量，可以看到栈的初始化很简单什么都没有代表一个空栈

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190411200806652.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

下边的栈UML图，它是基于Vector的一个类,因此也可以断定这个栈是根据数组来实现的

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190411200913969.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

可以在上边看出包含的一些栈的基本操作，并在继承Vector后进一步增强。


栈的基本操作

push（E）

```
    //入栈
    public E push(E item) {
        //在末尾加一个元素
        addElement(item);

        return item;
    }
```

```
//这是Vector的方法，在末尾加一个元素
public synchronized void addElement(E obj) {
        modCount++;
        //根据最小元素增加容量，也就是按需扩充，十分适合Stack
        ensureCapacityHelper(elementCount + 1);
        elementData[elementCount++] = obj;
    }


```

pop


```
    //出栈并返回出栈元素
    public synchronized E pop() {
        E       obj;
        //数组长度
        int     len = size();
        //取栈顶元素
        obj = peek();
        //移除栈顶
        removeElementAt(len - 1);

        return obj;
    }

```

peek

```
    //取栈顶元素
    public synchronized E peek() {
        int     len = size();

        if (len == 0)
            throw new EmptyStackException();
        return elementAt(len - 1);
    }

```
empty

```
    //判断栈空
    public boolean empty() {
        return size() == 0;
    }

```
search

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190411204841879.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)


```

    public synchronized int search(Object o) {
      //根据Vector中的方法，从后向前遍历查找，找到返回下标，没找到返回-1
      int i = lastIndexOf(o);
        //如果i>=0说明了查找到了（返回的i是查找到此元素在数组中的下标）就返回栈中的位置（是相对于栈顶来说的，栈顶为1，越往栈底就越大）
        if (i >= 0) {
            return size() - i;
        }
        //说明没查到
        return -1;
    }

```

### AbstractSequentialList

> 其实看到这个名字都可以见名知意，一连串的那不就是链表么（这个类是LinkedList的抽象类），AbstractSequentiaList和其他RandomAccess主要的区别是AbstractSequentiaList的主要方法都是通过迭代器实现的，因此对于随机访问由要求的不建议优先使用此类，应优先使用AbstractList。

AbstractSequentialList的UML图

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190411205333200.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

并且它由唯一一个子类，那就是LinkedList，这个等会再说，先说说AbstractSeqentialList是干嘛的

接下来就从源码看看它的设计方案，简单的说就是可以让迭代器进行增删改查操作了

```
public abstract class AbstractSequentialList<E> extends AbstractList<E> {
    
    //构造方法
    protected AbstractSequentialList() {
    }

    //返回指定index的元素
    public E get(int index) {
        try {
            return listIterator(index).next();
        } catch (NoSuchElementException exc) {
            throw new IndexOutOfBoundsException("Index: "+index);
        }
    }

    //替换原来index这个位置上的值，并返回被替代的值
    public E set(int index, E element) {
        try {
            ListIterator<E> e = listIterator(index);
            E oldVal = e.next();
            e.set(element);
            return oldVal;
        } catch (NoSuchElementException exc) {
            throw new IndexOutOfBoundsException("Index: "+index);
        }
    }

    //在index这个位置加添加一个元素，其余元素后移
    public void add(int index, E element) {
        try {
            listIterator(index).add(element);
        } catch (NoSuchElementException exc) {
            throw new IndexOutOfBoundsException("Index: "+index);
        }
    }
    //删除在index的元素
    public E remove(int index) {
        try {
            ListIterator<E> e = listIterator(index);
            E outCast = e.next();
            e.remove();
            return outCast;
        } catch (NoSuchElementException exc) {
            throw new IndexOutOfBoundsException("Index: "+index);
        }
    }


    //在index这个位置加一个集合
    public boolean addAll(int index, Collection<? extends E> c) {
        try {
            boolean modified = false;
            ListIterator<E> e1 = listIterator(index);
            Iterator<? extends E> e2 = c.iterator();
            while (e2.hasNext()) {
                e1.add(e2.next());
                modified = true;
            }
            return modified;
        } catch (NoSuchElementException exc) {
            throw new IndexOutOfBoundsException("Index: "+index);
        }
    }

    //在此类iterator的底层实现是ListIterator
    public Iterator<E> iterator() {
        return listIterator();
    }
}


```
> 这个抽象类也可以说就是为LinkedList服务的，因为在这个类中没有RandomAccess，因此它是对iterator的增强，为了更好的服务LinkedList。

接下来就到来LinkedList了

LinkedList也是Java.util包下的

Linked的UML

![在这里插入图片描述](https://img-blog.csdnimg.cn/2019041121393081.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

可以发现和前边的List最明显的区别就是有了内部类Node（节点），实现方式是以链表方式实现的

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190411213837738.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

内部类Node

```
 
  private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;
        //构造方法，前驱指针，Node的值，和后继指针有这几个才算是一个完整的Node节点
        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }


```

LinkedList的几种构造方法

```
   //无参，创造一个新的空链表
   public LinkedList() {
      }
    // 传入一个特定的集合，转换为LinkedList
    public LinkedList(Collection<? extends E> c) {
        this();
        addAll(c);
    }


```
测试传入一个集合的构造方法，结果是正确的

```

public class Linkedlist {

    public static void main(String[] args) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(1);
        arrayList.add(2);
        arrayList.add(3);
        LinkedList linkedList = new LinkedList(arrayList);
        for (int i = 0; i < linkedList.size(); i++) {
            System.out.println(linkedList.get(i));
        }
    }

}


```
Link（连接）操作

```
    //头插
    private void linkFirst(E e) {
        //将头节点赋给f
        final Node<E> f = first;
        //创建新节点
        final Node<E> newNode = new Node<>(null, e, f);
        //头插
        first = newNode;
        if (f == null)
            last = newNode;
        else
            f.prev = newNode;
        size++;
        modCount++;
    }

    //尾插
    void linkLast(E e) {
        //将尾节点给l
        final Node<E> l = last;
        final Node<E> newNode = new Node<>(l, e, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
        size++;
        modCount++;
    }

    //在succ前，插入一个值为e的节点
    void linkBefore(E e, Node<E> succ) {
        // assert succ != null;
        //succ不能为空，因为如果为空的话，它就没有前驱（为空）
        final Node<E> pred = succ.prev;
        final Node<E> newNode = new Node<>(pred, e, succ);
        succ.prev = newNode;
        if (pred == null)
            first = newNode;
        else
            pred.next = newNode;
        size++;
        modCount++;
    }


```

unlink（删除）操作

```
  //删除头节点，并返回删除的元素
  private E unlinkFirst(Node<E> f) {
        // assert f == first && f != null;
        final E element = f.item;
        final Node<E> next = f.next;
        //给取消指针链接的节点赋值为空，加快gc
        f.item = null;
        f.next = null; // help GC
        //更换首节点
        first = next;
        if (next == null)
            last = null;
        else
            next.prev = null;
        size--;
        modCount++;
        //返回被删除的头节点中的元素
        return element;
    }

    //删除尾节点并返回被删除节点的数据
    private E unlinkLast(Node<E> l) {
        // assert l == last && l != null;
        final E element = l.item;
        final Node<E> prev = l.prev;
        //赋空值加快gc
        l.item = null;
        l.prev = null; // help GC
        //更新尾节
        last = prev;
        if (prev == null)
            first = null;
        else
            prev.next = null;
        size--;
        modCount++;
        return element;
    }

    //删除节点并返回删除节点中的数据
    E unlink(Node<E> x) {
        // assert x != null;
        final E element = x.item;
        final Node<E> next = x.next;
        final Node<E> prev = x.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            x.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }

        x.item = null;
        size--;
        modCount++;
        return element;
    }

```
get,set,add

```

   //获取指定索引的元素，一般在遍历中用的多
   public E get(int index) {
        checkElementIndex(index);
        return node(index).item;
    }

    //修改指定index的节点的值，并返回被替换的值
    public E set(int index, E element) {
        checkElementIndex(index);
        Node<E> x = node(index);
        E oldVal = x.item;
        x.item = element;
        return oldVal;
    }

    //在指定索引添加一个值，其余往后移
    public void add(int index, E element) {
        checkPositionIndex(index);

        if (index == size)
            linkLast(element);
        else
            linkBefore(element, node(index));
    }


```
比如用一个实例来说明上边几个方法

```
  ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(1);
        arrayList.add(2);
        arrayList.add(3);
        LinkedList linkedList = new LinkedList(arrayList);
        for (int i = 0; i < linkedList.size(); i++) {
            System.out.println(linkedList.get(i));
        }
        linkedList.set(0, 4);
        System.out.println("修改后——————————");
        for (int i = 0; i < linkedList.size(); i++) {
            System.out.println(linkedList.get(i));
        }
        linkedList.add(0,5);
        System.out.println("给index0添加后——————————");
        for (int i = 0; i < linkedList.size(); i++) {
            System.out.println(linkedList.get(i));
        }


```
结果如下

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190411222026682.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

```
 
    //将LinkedList转换为数组
    public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (Node<E> x = first; x != null; x = x.next)
            result[i++] = x.item;
        return result;
    }

   //返回有泛型约束的数组
    public <T> T[] toArray(T[] a) {
        if (a.length < size)
            a = (T[])java.lang.reflect.Array.newInstance(
                                a.getClass().getComponentType(), size);
        int i = 0;
        Object[] result = a;
        for (Node<E> x = first; x != null; x = x.next)
            result[i++] = x.item;

        if (a.length > size)
            a[size] = null;

        return a;
    }

    // 使用输出流写出
    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException {
        // Write out any hidden serialization magic
        s.defaultWriteObject();

        // Write out size
        s.writeInt(size);

        // Write out all elements in the proper order.
        for (Node<E> x = first; x != null; x = x.next)
            s.writeObject(x.item);
    }

    //使用输入流读入
    @SuppressWarnings("unchecked")
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
        // Read in any hidden serialization magic
        s.defaultReadObject();

        // Read in size
        int size = s.readInt();

        // Read in all elements in the proper order.
        for (int i = 0; i < size; i++)
            linkLast((E)s.readObject());
    }


```

可以看出在LinkedList中也实现了Deque双端队列的方法，因此双端队列在JDK中使用的是链表。因为Deque是Queue的子类，因此Deque中也包含Queue的方法。因此在LinkedList相当于把普通队列和双端队列都进行了实现。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190411222418633.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)



这些方法也都很简单，都是用上边的命令组合起来的，因此就不多重复了。

> 为了避免篇幅过长，把Set分支放在了下一篇
