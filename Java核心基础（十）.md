# 深度了解Map

> 在前面对Collection有了一些认识，Collection和Map是密不可分的两大集合模块。

Map结构总览，在Map中定义的对Map操作的函数，并且在里边也定义了一个子类（子接口）Entity。Map不像Collection集合那样，Map的结构是一个k，v结构。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190414123517945.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

在Entity中也定义了对Entity操作的基本方法，接下来就一步步的进入Map吧。

## AbstractMap

在AbstractMap中对Map的一些通用方法进行了实现，比如equals，keyset，values等等，下边就以几个常用的方法为例

先看一看在其中声明的属性keySet是一个Set类型，values是一个Collection类型。有没有考虑过为什么keySet声明为Set，而Values声明为Collection，因为在Set中不能有重复的元素，这也就是为什么在Map中k不能重复（仅仅是我的理解）。

```
    transient Set<K>        keySet;
    transient Collection<V> values;


```
几个常用的通用方法

```
 public abstract Set<Entry<K,V>> entrySet();

//判断Map中是否存在这个值
public boolean containsValue(Object value) {
        //取得保存entity的Set的迭代器对象
        Iterator<Entry<K,V>> i = entrySet().iterator();
        if (value==null) {
            while (i.hasNext()) {
                Entry<K,V> e = i.next();
                if (e.getValue()==null)
                    return true;
            }
        } else {
            while (i.hasNext()) {
                Entry<K,V> e = i.next();
                if (value.equals(e.getValue()))
                    return true;
            }
        }
        return false;
    }

    //判断是够包含某个key和上边一样
    public boolean containsKey(Object key) {
        Iterator<Map.Entry<K,V>> i = entrySet().iterator();
        if (key==null) {
            while (i.hasNext()) {
                Entry<K,V> e = i.next();
                if (e.getKey()==null)
                    return true;
            }
        } else {
            while (i.hasNext()) {
                Entry<K,V> e = i.next();
                if (key.equals(e.getKey()))
                    return true;
            }
        }
        return false;
    }

    //使用某个k得到对应的V
    public V get(Object key) {
        //得到entrySet的迭代器
        Iterator<Entry<K,V>> i = entrySet().iterator();
        if (key==null) {
            while (i.hasNext()) {
                Entry<K,V> e = i.next();
                if (e.getKey()==null)
                    return e.getValue();
            }
        } else {
            while (i.hasNext()) {
                Entry<K,V> e = i.next();
                if (key.equals(e.getKey()))
                    return e.getValue();
            }
        }
        return null;
    }


    //根据k删除键值对并返回V
    public V remove(Object key) {
        Iterator<Entry<K,V>> i = entrySet().iterator();
        Entry<K,V> correctEntry = null;
        if (key==null) {
            while (correctEntry==null && i.hasNext()) {
                Entry<K,V> e = i.next();
                if (e.getKey()==null)
                    correctEntry = e;
            }
        } else {
            while (correctEntry==null && i.hasNext()) {
                Entry<K,V> e = i.next();
                if (key.equals(e.getKey()))
                    correctEntry = e;
            }
        }

        V oldValue = null;
        if (correctEntry !=null) {
            oldValue = correctEntry.getValue();
            i.remove();
        }
        return oldValue;
    }


    //根据传入一个Map生成新的Map
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
            put(e.getKey(), e.getValue());
    }
      
    //得到一个keySet（只包含K的Set）
    public Set<K> keySet() {
        Set<K> ks = keySet;
        //第一次调用这个方法时
        if (ks == null) {
            //new 一个AbstractSet
            ks = new AbstractSet<K>() {

                //在AbstractSet内部实现迭代器的方法
                public Iterator<K> iterator() {
  
                    //相当于在ks中增加了获取迭代器的方法
                    return new Iterator<K>() {
                        private Iterator<Entry<K,V>> i = entrySet().iterator();

                        public boolean hasNext() {
                            return i.hasNext();
                        }

                        public K next() {
                            return i.next().getKey();
                        }

                        public void remove() {
                            i.remove();
                        }
                    };
                }

                public int size() {
                    return AbstractMap.this.size();
                }

                public boolean isEmpty() {
                    return AbstractMap.this.isEmpty();
                }

                public void clear() {
                    AbstractMap.this.clear();
                }

                public boolean contains(Object k) {
                    return AbstractMap.this.containsKey(k);
                }
            };
            //最后将ks返回
            keySet = ks;
        }

        //不是第一次就直接返回
        return ks;
    }

    //Map中值的集合
    public Collection<V> values() {
        Collection<V> vals = values;
        //如果是第一次调用
        if (vals == null) {
            //先创建一个AbstractCollection
            vals = new AbstractCollection<V>() {
         
                //AbstractCollection内获取迭代器的方法
                public Iterator<V> iterator() {

                    //在方法内返回一个已实现好的迭代器实例
                    return new Iterator<V>() {
                        private Iterator<Entry<K,V>> i = entrySet().iterator();

                        public boolean hasNext() {
                            return i.hasNext();
                        }

                        public V next() {
                            return i.next().getValue();
                        }

                        public void remove() {
                            i.remove();
                        }
                    };
                }

                public int size() {
                    return AbstractMap.this.size();
                }

                public boolean isEmpty() {
                    return AbstractMap.this.isEmpty();
                }

                public void clear() {
                    AbstractMap.this.clear();
                }

                public boolean contains(Object v) {
                    return AbstractMap.this.containsValue(v);
                }
            };
            //返回vals
            values = vals;
        }
        //如果不是第一次，那就直接返回
        return vals;
    }


```

在里边是是实现的equals，hashcode，和toString对然很简单，但是很有借鉴意义，可以在自己的代码中使用这样的思想

```
  //equals三部曲：先看是不是自己，然后看是不是同类，然后再看具体
  public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof Map))
            return false;
        Map<?,?> m = (Map<?,?>) o;
        if (m.size() != size())
            return false;

        try {
            Iterator<Entry<K,V>> i = entrySet().iterator();
            while (i.hasNext()) {
                Entry<K,V> e = i.next();
                K key = e.getKey();
                V value = e.getValue();
                if (value == null) {
                    if (!(m.get(key)==null && m.containsKey(key)))
                        return false;
                } else {
                    if (!value.equals(m.get(key)))
                        return false;
                }
            }
        } catch (ClassCastException unused) {
            return false;
        } catch (NullPointerException unused) {
            return false;
        }

        return true;
    }

    //哈希码，可以看出在此hashcode使用的是将每一个键值对的哈希码相加
    public int hashCode() {
        int h = 0;
        Iterator<Entry<K,V>> i = entrySet().iterator();
        while (i.hasNext())
            h += i.next().hashCode();
        return h;
    }

    //toString，这也就是我们为什么在遍历的时候出现的都是我们很方便看的东西
    public String toString() {
        Iterator<Entry<K,V>> i = entrySet().iterator();
        if (! i.hasNext())
            return "{}";

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;) {
            Entry<K,V> e = i.next();
            K key = e.getKey();
            V value = e.getValue();
            sb.append(key   == this ? "(this Map)" : key);
            sb.append('=');
            sb.append(value == this ? "(this Map)" : value);
            if (! i.hasNext())
                return sb.append('}').toString();
            sb.append(',').append(' ');
        }
    }


```
## HashMap解读

> HashMap是用的比较多的Map，我们生活中用的很多，但是你真的了解它吗？

HashMap也是老样子是继承了它的抽象类，并实现了序列化和可克隆的接口。你以为它和前边的Collection一样吗？这你就大错特错了，HashMap是很复杂的，接下来就对HashMap进行解读。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190414161033693.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

HashMap组成结构，可以看出HashMap中有很多的内部类，还有自己内部的方法。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190414161641694.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)


HashMap中的属性

```
 
    //在序列化时使用的序列号
    private static final long serialVersionUID = 362498820763181265L;
     
    //默认的初始容量为16个桶节点
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; 

    //最大容量为2的30次方
    static final int MAXIMUM_CAPACITY = 1 << 30;

    //默认的负载因子0.75，因此在默认情况下，16*0.75=12，当键值对大于12，就会引发扩容    
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
 
    //某个桶节点数量大于8就可能转化为红黑树
    static final int TREEIFY_THRESHOLD = 8;

    //当某个桶节点数量小于6的时候就会换为链表
    static final int UNTREEIFY_THRESHOLD = 6;

    //在变成树之前，对键值对的个数进行判断，如果大于64才能转化为树。
    static final int MIN_TREEIFY_CAPACITY = 64;

    //桶节点数组
    transient Node<K,V>[] table;

    //键值对集合
    transient Set<Map.Entry<K,V>> entrySet;

    //HaspMap的大小
    transient int size;

    //修改次数
    transient int modCount;
   
    //如果扩容的话就记录下一次容量的大小
    int threshold;

    //负载因子
    final float loadFactor;

```

看完这么多是不是有很多的疑问呢?那么就先提前剧透一下吧

> 在Java8以后的解决冲突的方法是链地址法+平衡树

1. 为什么初始化为16个桶，因为如果桶节点过大的话，遍历效率就慢了，如果桶节点过小的话，容易引发扩容。
2. 为什么默认扩容的负载因子是0.75，而不是0.85...，因为这是根据数学概率分析的并且在实际场合中使用的。如果负载因子设置过大的话，那很有可能会导致HashMap的效率降低，因为它很有可能会如线性表一般。
3. 为什么桶的节点大于8就可能（因为还要对键值对的数量进行判断）换为红黑树，刚刚也说过，如果哈希函数不合理，或者是负载因子设置不合理，就会导致线性表增多，从而达不到散列的目的，因此在Java中为了解决这一问题，就采用了红黑树来代替过长的链表。

有这么多的理论不知道清楚了没，但是我们还可以根据JDK作者的注释再来一波离散数学

> 负载因子计算公式：负载因子 = 总键值对数 / 箱子个数

因此负载因子就可以用来表示桶的空和满，如果负载因子很大了的话，那就说明太满了，如果很小，那就说明太空了。

> 上边说到的负载因子和红黑树的转换，是根据泊松分布来计算出来的（原谅我的概率论没有学的很好，不然我可能就是作者了。。。概率论果然很叼啊）

仅存的记忆，这是一种离散性随机分布，数学期望=np。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190414165227736.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

下图是JDK源码中作者的注释，桶的个数和默认大小的平均阈值（理论是λ=0.5），并且考虑粒度等因素，就选择0.75.再大于8的时候λ已经小到忽略不计了，因此选择在8的时候扩容。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190414165019342.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

看完这些，就感觉JDK作者真的牛逼，闲话不多说了，继续往下看吧。

先来看看内部类Node

```
     //实现了Entry就说明它也是一个键值对类型的节点
   static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> next;
        //Node的构造
        Node(int hash, K key, V value, Node<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
  ...
   }

```

TreeNode红黑树节点

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190414172116404.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

```

static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
     
        //父节点
        TreeNode<K,V> parent; 
        //左孩子
        TreeNode<K,V> left;
        //右孩子
        TreeNode<K,V> right;
        //前驱
        TreeNode<K,V> prev;  
        //判断是不是红节点的标志
        boolean red;
        //构造方法
        TreeNode(int hash, K key, V val, Node<K,V> next) {
            super(hash, key, val, next);
        }
    ....
}

```

说实话，HashMap这部分要相信写出来的话没有好几天是不行的，因此我挑的是一些比较常见的，也是对于新手来说比较不好理解的。

HashMap的几种构造方法

```

   //构造一：自定义初始大下和负载因子，在我看来没有自己的计算不要轻易的改变默认的负载因子
   public HashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);
        //赋值给记录负载因子的变量
        this.loadFactor = loadFactor;
        //计算并记录下一次扩容的大小，以2的幂次方进行扩容
        this.threshold = tableSizeFor(initialCapacity);
    }
     
    //这里穿插一下计算容量的方法，有没有感觉很眼熟，没错上一篇我们详细的分析了它为什么能快速扩容为2倍，但是条件是必须是2的幂次方，这就不多说了
    static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    //构造二：使用默认的负载因子，初始桶大小自定义 
    public HashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    //构造三：无参构造，采用默认的负载因子和桶数量
    public HashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted
    }

    //构造四：传入一个Map，将它转为HashMap使用默认的负载因子
    public HashMap(Map<? extends K, ? extends V> m) {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        putMapEntries(m, false);
    }
    
    //放入Map的键值对
    final void putMapEntries(Map<? extends K, ? extends V> m, boolean evict) {
        int s = m.size();
        //不是空的Map
        if (s > 0) { 
            //存在桶节点
            if (table == null) { // pre-size
                //算出桶节点的个数
                float ft = ((float)s / loadFactor) + 1.0F;
                //如果小于最大桶个数就是这个值了
                int t = ((ft < (float)MAXIMUM_CAPACITY) ?
                         (int)ft : MAXIMUM_CAPACITY);
                //如果threshold小于t，那就说明没进行扩容
                if (t > threshold)
                    //给下一次扩容做准备，给threshold赋值
                    threshold = tableSizeFor(t);
            }
            //如果threshold大于t并且s大于threshold，就进行resize也就是进行重新分配（在Redis中就是rehash）
            else if (s > threshold)
                resize();
            //将键值对放进去
            for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
                K key = e.getKey();
                V value = e.getValue();
                putVal(hash(key), key, value, false, evict);
            }
        }
    }



```

HashMap就先介绍到这里，这里并不是终点，这里仅仅是开始，因为有了HashMap的基本概念，下一步深入HashMap就稍微容易点，对HashMap的深入，也就不断成了数据结构于算法的深入，因此，我就写这么多，一些操作性的东西，写起来真的很麻烦，但是你可能一看就懂。

## TreeMap

> TreeMap是TreeSet的底层实现。是基于红黑树的，并且存储的是键值对。根据键来进行比较，不管是使用那种比较器。

TreeMap的UML，TreeMap是继承于AbstractMap并且实现了序列化，克隆，还有NavigableMap接口。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190415192823444.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

NavigableMap为它提供了排序的方法接口

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190415193038949.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

在它里边都定义了一些为TreeMap实现功能排序比较的方法，还有一些常用的方法。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190415215725598.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

在TreeMap中也有很多内部类，内部类的优点就是不用在每次使用的时候取new一个对象。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190415220121352.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQyNjA1OTY4,size_16,color_FFFFFF,t_70)

TreeMap的一些声明变量

```

    //构造器
    private final Comparator<? super K> comparator;
   
    //树的根节点，并且可以发现它是一个Entry类型的
    private transient Entry<K,V> root;
   
    //TreeMap的大小
    private transient int size = 0;

    //树被修改的次数
    private transient int modCount = 0;
    
    //键值对集合
    private transient EntrySet entrySet;
    //排序键的集合
    private transient KeySet<K> navigableKeySet;
    //navigableMap
    private transient NavigableMap<K,V> descendingMap;
    
    //红黑常量赋值
    private static final boolean RED   = false;
    private static final boolean BLACK = true;
   
    //序列号
    private static final long serialVersionUID = 919286545866124006L;
     
    //防止key和value的集合
    transient Set<K>        keySet;
    transient Collection<V> values;

```

内部类Entry

```
 
 static final class Entry<K,V> implements Map.Entry<K,V> {
        K key;
        V value;
        //左节点
        Entry<K,V> left;
        //右节点
        Entry<K,V> right;
        //父节点
        Entry<K,V> parent;
        //默认是黑节点，也就是说根节点就是黑的
        boolean color = BLACK;

        //构造方法
        Entry(K key, V value, Entry<K,V> parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
        }
  
        .......
 }
 
```

TreeMap的构造方法

```
    
    //构造一：默认是一个空的比较器，也就是默认比较器
    public TreeMap() {
        comparator = null;
    }

    //构造二：传入一个自定义的比较器
    public TreeMap(Comparator<? super K> comparator) {
        this.comparator = comparator;
    }
    
    //构造三：如果传入一个没有实现比较的Map，那就把Map放入TreeMap并且采用默认的构造器。
    public TreeMap(Map<? extends K, ? extends V> m) {
        comparator = null;
        putAll(m);
    }
    //传入一个SortedMap并且使用它原本的比较器
    public TreeMap(SortedMap<K, ? extends V> m) {
        comparator = m.comparator();
        try {
            buildFromSorted(m.size(), m.entrySet().iterator(), null, null);
        } catch (java.io.IOException cannotHappen) {
        } catch (ClassNotFoundException cannotHappen) {
        }
    }


```

putAll

```

  //传入一个Map
  public void putAll(Map<? extends K, ? extends V> map) {
        int mapSize = map.size();
 
        //判断是不是一个SortedMap
        if (size==0 && mapSize!=0 && map instanceof SortedMap) {
        //如果是的话就把比较器拿过来
            Comparator<?> c = ((SortedMap<?,?>)map).comparator();
            if (c == comparator || (c != null && c.equals(comparator))) {
                ++modCount;
                try {
                    //将SortedMap转为TreeMap
                    buildFromSorted(mapSize, map.entrySet().iterator(),
                                    null, null);
                } catch (java.io.IOException cannotHappen) {
                } catch (ClassNotFoundException cannotHappen) {
                }
                return;
            }
        }
        //否则就调用AbstractMap的putAll
        super.putAll(map);
    }

  

```

buildFromSorted

```
 
  //传入size和迭代器，输入流，值
  private void buildFromSorted(int size, Iterator<?> it,
                                 java.io.ObjectInputStream str,
                                 V defaultVal)
        throws  java.io.IOException, ClassNotFoundException {
        this.size = size;
        //调用下边的方法构造树
        root = buildFromSorted(0, 0, size-1, computeRedLevel(size),
                               it, str, defaultVal);
    }

    
    //使用二分递归建树
    /*从一个有序序列中添加建树
			思路：如有序列 1,2,3,4,5,6,7,8 ,9,10,
			以最中间的数作为根结点，然后将序列分成两组，(1,2,3,4) (6,7,8,9,10)
			以同理的方法，在第一组序列中找出最中间的树作为根结点，建立一个子树，该子树作为整个树的左子树，在第二个序列中找出最中间的树作为根结点，孩子子
			树作为整个树的右树，以此递归下去
			最终形成的树是在叶子结点以上是一个满二叉树，所以满足红黑树的性质，叶子结点不满足，所以把叶子结点都染成红色

     */
    @SuppressWarnings("unchecked")
    private final Entry<K,V> buildFromSorted(int level, int lo, int hi,
                                             int redLevel,
                                             Iterator<?> it,
                                             java.io.ObjectInputStream str,
                                             V defaultVal)
        throws  java.io.IOException, ClassNotFoundException {
        /*
         * Strategy: The root is the middlemost element. To get to it, we
         * have to first recursively construct the entire left subtree,
         * so as to grab all of its elements. We can then proceed with right
         * subtree.
         *
         * The lo and hi arguments are the minimum and maximum
         * indices to pull out of the iterator or stream for current subtree.
         * They are not actually indexed, we just proceed sequentially,
         * ensuring that items are extracted in corresponding order.
         */

        if (hi < lo) return null;

        int mid = (lo + hi) >>> 1;

        Entry<K,V> left  = null;
        if (lo < mid)
            left = buildFromSorted(level+1, lo, mid - 1, redLevel,
                                   it, str, defaultVal);

        // extract key and/or value from iterator or stream
        K key;
        V value;
        if (it != null) {
            if (defaultVal==null) {
                Map.Entry<?,?> entry = (Map.Entry<?,?>)it.next();
                key = (K)entry.getKey();
                value = (V)entry.getValue();
            } else {
                key = (K)it.next();
                value = defaultVal;
            }
        } else { // use stream
            key = (K) str.readObject();
            value = (defaultVal != null ? defaultVal : (V) str.readObject());
        }

        Entry<K,V> middle =  new Entry<>(key, value, null);

        // color nodes in non-full bottommost level red
        if (level == redLevel)
            middle.color = RED;

        if (left != null) {
            middle.left = left;
            left.parent = middle;
        }

        if (mid < hi) {
            Entry<K,V> right = buildFromSorted(level+1, mid+1, hi, redLevel,
                                               it, str, defaultVal);
            middle.right = right;
            right.parent = middle;
        }

        return middle;
    }


```

常用方法

```

//加一个K，V

public V put(K key, V value) {
        //得到根
        Entry<K,V> t = root;
        //如果还没有树的话就新建树
        if (t == null) {
            //对两个方法的键进行比较，返回一个int
            compare(key, key); // type (and possibly null) check

            root = new Entry<>(key, value, null);
            size = 1;
            modCount++;
            return null;
        }
        int cmp;
        Entry<K,V> parent;
        // split comparator and comparable paths
        Comparator<? super K> cpr = comparator;
        if (cpr != null) {
            do {
                parent = t;
                //返回k之间的比较结果
                cmp = cpr.compare(key, t.key);
                //小于0，也就是key小于t.key，放在左边
                if (cmp < 0)
                    t = t.left;
                //如果大于0，就放在右边
                else if (cmp > 0)
                    t = t.right;
                //否则就更新值
                else
                    return t.setValue(value);
            } while (t != null);
        }
        //如果 传入的比较器为空，就是默认的比较器，还是一样的流程
        else {
            if (key == null)
                throw new NullPointerException();
            @SuppressWarnings("unchecked")
                Comparable<? super K> k = (Comparable<? super K>) key;
            do {
                parent = t;
                cmp = k.compareTo(t.key);
                if (cmp < 0)
                    t = t.left;
                else if (cmp > 0)
                    t = t.right;
                else
                    return t.setValue(value);
            } while (t != null);
        }
        Entry<K,V> e = new Entry<>(key, value, parent);
        if (cmp < 0)
            parent.left = e;
        else
            parent.right = e;
        fixAfterInsertion(e);
        size++;
        modCount++;
        return null;
    }

    //对key进行比较，如果前者大于后者就返回1，小于返回-1，等于返回0  
    final int compare(Object k1, Object k2) {
        return comparator==null ? ((Comparable<? super K>)k1).compareTo((K)k2)
            : comparator.compare((K)k1, (K)k2);
    }

    //根据k来移除节点，并返回Value
    public V remove(Object key) {
        Entry<K,V> p = getEntry(key);
        if (p == null)
            return null;

        V oldValue = p.value;
        //删除键值对
        deleteEntry(p);
        return oldValue;
    }

    //清除所有
    public void clear() {
        modCount++;
        size = 0;
        root = null;
    }



```

对集合的分析就到这里吧，虽然这里只分析了一些常用的，但是掌握这些常用的，分析其他源码也都是一个道理，不难，因此就到这结束，也有很多地方没有分析到，到后边边复习基础，边分析吧！