# Java8新特性总结

## Lambda表达式

Lambda表达式是Java8新有的函数式编程的典型，它的语法很简单就是()->xxxx,如果是多行代码就是()->{xxxx}。下边就先来介绍一下Java8中的几个函数式接口。

### 四大核心函数式接口

为了更好的使用函数式编程，Java8内嵌了很多的函数接口，但是都是由四大核心接口衍生出来的。

#### Consumer<T> ：有参数，没有返回值

消费型接口，里面内嵌了void accept(T t) 方法来接收一个类型的东西来消费。

#### Supplier<T>:无参数，有返回值

供给型接口，里面内嵌了 T get()方法，可以类比getter来理解。

#### Function<T,R>:有参数，有返回值

函数式接口，里面内嵌了R apply（T t）方法。

#### Predicate<T>：有参数，返回布尔类型

断言型接口，里面内嵌了boolean test（T t）方法

```Java
 public class lambda1 {
     
     
        //1.Consumer<T>，就是没有返回值的类型就是消费类型的
        public void spend(double money, Consumer<Double> consumer) {
            consumer.accept(money);
        }

        @Test
        public void test01() {
            spend(100, (m) -> System.out.println("消费" + m + "元"));
        }

        //2.Supplier<T> ：供给型接口，带有返回值  
        // Supplier里仅有的get方法，没有返回值，
        // 每次就只要根据自己的策略生成一个值放到函数体种就行

        //案例：产生指定个数的整数放入集合
        public List getNum(int n, Supplier<Integer> supplier) {
            ArrayList<Integer> arrayList = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                arrayList.add(supplier.get());
            }
            return arrayList;
        }

        @Test
        public void test02() {
            List<Integer> list = getNum(10, () -> (int) (Math.random() * 10));
            System.out.println("生成的数字如下：");
            for (Integer l : list) {
                System.out.print("  " + l + " ");
            }
        }

        //3.Function<T,R>函数型接口,有传入，有返回

        //案例：处理字符串
        public String strHandle(String s, Function<String, String> function) {
            return function.apply(s);
        }

        @Test
        public void test03() {

            String s1 = strHandle("xxxxx", (s) -> s.toUpperCase());
            System.out.println(s1);

            String s = strHandle("ssss", (a) -> a.substring(0, 2));
            System.out.println(s);

        }

        //4.Rredicate<T>：断言型接口

        //案例，将满足条件的字符串放入一个集合
        public List<String> filterList(List<String> s, Predicate<String> predicate) {
            List<String> stringList = new ArrayList<>();
            for (String s1 : s) {
                if (predicate.test(s1) == true) {
                    stringList.add(s1);
                }
            }
            return stringList;
        }

        @Test
        public void test04(){
            List<String> stringList = filterList(Arrays.asList("sss", "swfdwfwdf", "fefe", "ss"), (s) -> s.length() > 3);
            for (String s : stringList) {
                System.out.println(s);
            }
        }

    }
```

### 引用

#### 方法引用

方法引用是用来干嘛的，其实方法引用就是变形的Lambda表达式，比Lambda更简洁。

**方法引用的核心就是函数式接口要和使用的方法引用返回值和参数要相同**

#####  对象::实例方法名

```Java
//对象：：实例方法名 (不是静态，因此先得实例化)
    @Test
    public void test01() {

        PrintStream ps = System.out;
        Consumer<String> consumer = (x) -> ps.println(x);
        consumer.accept("ssss");

        Consumer<String> consumer1 = System.out::println;
        consumer1.accept("hahahahah");

        Consumer<String> consumer2 = this::test;
        consumer2.accept("xxxxxx");
    }

    //自定义一个返回值和参数相和Consumer中accept相同的方法，使用方法引用
    void test(String x) {
        System.out.println("Test方法引用" + x);
    }

    //实例二
    class Employee {

        Employee() {
        }

        Employee(String name, int id) {
            this.name = name;
            this.id = id;
        }

        private String name;
        private int id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }


        public void setId(int id) {
            this.id = id;
        }
    }

    @Test
    public void test02() {
        Employee employee = new Employee();
        //因为employee的getter方法和supplier的get方法返回值和参数一致
        Supplier<String> supplier = employee::getName;
        employee.setName("zhangsan");
        System.out.println(supplier.get());
        //employee的setter的方法和Consumer的
        Consumer<String> consumer = employee::setName;
        consumer.accept("wangwu");
        System.out.println(employee.getName());

    }
```

##### 类::静态方法名

```Java
 //类::静态方法名（因为已经是静态了，所以就不需要实例化）
    @Test
    public void test03() {

        Comparator<Integer> comparator = (x, y) -> Integer.compare(x, y);
        int compare = comparator.compare(3, 2);
        System.out.println(compare);

        //函数式接口的唯一方法的参数和返回值要和写的方法要一致（Integer::compare）
        Comparator<Integer> comparator1 = Integer::compare;
        System.out.println(comparator1.compare(5, 6));
    }
```

##### 类::实例方法名

```Java
  //类::实例方法名（如果类中的这个方法和函数式接口返回值和参数匹配的话就可以）
    @Test
    public void test04() {

        //lambda表达式写法
        BiPredicate<String, String> bp0 = (x, y) -> x.contains(y);
        //引用写法
        BiPredicate<String, String> bp = String::contains;
        System.out.println(bp.test("xxx", "xx"));

        BiPredicate<String, String> bp1 = String::equals;
        System.out.println(bp1.test("x", "x"));
    }

```

#### 构造器引用

语法    ClassName::new

```Java
 //构造器引用___________________________________________________________________
    @Test
    public void test05() {
        //使用lambda表达式来引用无参构造器
        Supplier<Employee> supplier = () -> new Employee();
        Employee employee = supplier.get();

        //通过构造器引用来使用无参构造器
        Supplier<Employee> supplier1 = Employee::new;
        Employee employee1 = supplier1.get();

        //使用lambda表达式来引用有参构造器
        BiFunction<String, Integer, Employee> function = (x, y) -> new Employee(x, y);
        Employee employee2 = function.apply("beiba", 5);
        System.out.println(employee2.name + "  " + employee2.id);

        //使用构造器引用来使用有参构造器
        BiFunction<String, Integer, Employee> function1 = Employee::new;
        Employee employee3 = function1.apply("wnagwu", 87);
        System.out.println(employee3.name + "  " + employee3.id);
    }
```



#### 数组引用

语法      Type::new

```Java
//数组引用___________________________________________________________________
    @Test
    public void test07() {
        //使用lambda表达式
        Function<Integer,String[]> function = (x)->new String[x];
        String[] apply = function.apply(10);
        System.out.println(apply.length);
        //使用数组引用
        Function<Integer,String[]> function1 = String[]::new;
        System.out.println(function1.apply(20).length);
    }
```

## Stream

Stream流就是在数据流动中对数据进行一系列处理，而产生一个新流。流和NIO中的Channel相似，不会存储数据。

![1559319020268](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1559319020268.png)

Stream流的工作流程

![1559318989093](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1559318989093.png)

Stream流的操作离不开，创建，处理，和结束（在中间操作完成要结束流操作，然后再转化成我们想要的类型）。

### Stream的四种创建方法

```Java
//Stream的创建——————————————————————
    @Test
    public void test01(){
        //1.通过Collection系列提供的stream方法
        List<Object> list = new ArrayList<>();
        Stream<Object> stream = list.stream();

        //2.通过Arrays中的stream方法获得[数组流]
        Integer[] integers = new Integer[10];
        Stream<Integer> stream1 = Arrays.stream(integers);

        //3.通过Stream的of方法创建流
        Stream<String> xx = Stream.of("xx", "xx");

        //4.创建无限流
        //迭代
        //public static<T> Stream<T> iterate(final T seed, final UnaryOperator<T> f)   UnaryOperator是Function的子类，输入什么返回什么
        
        Stream<Integer> iterate = Stream.iterate(0, (x) -> x + 2);

    }
```



### Stream的中间处理操作

有了Stream流，就可以进行下一步的中间处理操作了，学完Stream后你会感觉Stream流的操作就和操作Sql语句一样简单。

多个中间处理操作连接起来就像是工厂中的流水线，知道到达终点（也就是终止操作），在没有终止操作不会执行，而是在终止操作时一次性全部处理。也称它为“惰性求值”。

#### Stream的筛选于切片

* filter——接收一个lambda表达式，根据条件过滤
* limit——设置获取的最多元素不超过limit
* skip——跳过元素，如果不足n个就是返回null，和limit是互补的
* distinct——筛选，**通过对象的hashcode和equals去除重复元素**

```Java
public class Student {
    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    private int id;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private String name;

    @Override
    public String toString() {
        return "[id="+id+",name="+name+"]";
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return id == student.id &&
                name.equals(student.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}

```



```Java

  List<Student> list = Arrays.asList(
            new Student(1, "zhangsan"),
            new Student(2, "lisi"),
            new Student(3, "wangwu"),
            new Student(3, "wangwu")
    );


    //执行过程就是一个内部迭代的过程，由StreamAPI来迭代，不需要自己来迭代
    //filter
    @Test
    public void test01() {
        Stream<Student> stream = list.stream().filter((x) -> {
            //没有终止操作，中间操作不会执行的
            System.out.println("执行Filter");
            return x.getId() > 1;
        });
        //终止操作
        stream.forEach(System.out::println);
    }

    //limit：限制最大满足量
    @Test
    public void test02() {
        list.stream().limit(2).forEach(System.out::println);


        //可以看到虽然有三个满足条件但是只输出两个
        list.stream().filter((x) -> x.getId() > 0).limit(2).forEach(System.out::println);
    }

    //skip
    @Test
    public void test03() {
        list.stream().skip(2).forEach(System.out::println);
    }

    //Distinct,使用distinct必须要重写hashcode和equals方法
    @Test
    public void test04() {

        list.stream()
                .filter((x) -> x.getId() > 1)
                .distinct()
                .forEach(System.out::println);

    }
```



#### Stream的映射

* map——接收一个lambda，接收一个函数做参数，该函数会被应用到每一个元素，并映射为新的元素。
* flatMap——接收一个函数作为参数，将一个流中的所有子流连接成一个流。

map和flatMap的关系就和add和addAll的关系

```Java
    List<String> list2 = Arrays.asList("sss", "aa", "cccx");

    @Test
    public void test05() {
        list.stream()
                //把x替换为了x.getName().toUpperCase()
                .map((x) -> x.getName().toUpperCase())
                .forEach(System.out::println);
        list.stream()
                .map(Student::toString)
                .forEach(System.out::println);

//        {{s,s,s},{a,a},{c,c,c,x}}
//
//        Stream<Stream<Character>> streamStream = list2.stream()
//                .map(Stream2::getCharacter);
//        streamStream.forEach((x)->x.forEach(System.out::println));

        //FlatMap就是为了解决嵌套Stream的，把本来需要foreach的流合并在了一起
        //{s,s,s,a,a,c,c,c,x}
        //Map和FlatMap就相当于是list.add(list)和list.addAll(list),add是将参数的list对象加进去，有嵌套，但是addAll是将元素加进去，没有嵌套
        Stream<Character> characterStream = list2.stream().flatMap(Stream2::getCharacter);
        characterStream.forEach(System.out::println);

    }

    public static Stream<Character> getCharacter(String s) {
        List<Character> list1 = new ArrayList<>();
        for (Character c : s.toCharArray()) {
            list1.add(c);
        }
        return list1.stream();
    }

```

#### Stream的排序

* Sorted（）——自然排序(Comparable里的Compare to（）)
* Sorted（Comparator com）——定制自己的排序

```Java
   @Test
    public void test06() {
        list.stream().map((x) -> x.getId()).sorted().forEach(System.out::println);

        list.stream().sorted(
                (x1, x2) -> {
                    if (x1.getId() == x2.getId()) {
                        return x1.getName().compareTo(x2.getName());
                    } else {
                        return x1.getId() - x2.getId();
                    }
                }
        ).forEach(System.out::println);

    }
```

### Stream的终止操作

Stream的终止操作是必不可少的，没有终止操作所有的处理都是无效。

#### 查找和匹配

* allMatch——检查是否匹配所有元素
* anyMatch——检查至少匹配一个元素
* noneMatch——检查是否没有匹配所有元素
*  findFirst——返回当前流的第一个元素
* findAny——返回当前流的任何一个元素
*  count——返回流中元素总个数
* max——返回流中最大值
*  min——返回流中最小值

```Java
public class Employee {


    private int id;
    private String name;
    private int status;

    public Employee(int id, String name, int status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id == employee.id &&
                status == employee.status &&
                Objects.equals(name, employee.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

```



```

/*
 * 终止操作
 *
 * */
public class Stream3 {


    List<Employee> list = Arrays.asList(
            new Employee(1, "zhangsan", 0),
            new Employee(2, "lisi", 1),
            new Employee(3, "wangwu", 3),
            new Employee(4, "wu", 2),
            new Employee(5, "wusss", 0),
            new Employee(6, "ss", 0)
    );

    @Test
    public void test01() {

        boolean b = list.stream().allMatch((e) -> e.getStatus() == 0);
        System.out.println(b);
        boolean c = list.stream().anyMatch((e) -> e.getStatus() == 0);
        System.out.println(c);
        long count = list.stream().count();
        System.out.println(count);

        //Optional容器类，防止空指针异常
        Optional<Employee> optional = list.stream()
        .sorted((x1, x2) -> Integer.compare(x1.getId(), x2.getId()))
        .findFirst();
        System.out.println(optional.get());

        Optional<Employee> optional1 = list.parallelStream().filter((e) -> e.getStatus() == 0).findAny();
        System.out.println(optional1.get());


    }


}
```

#### 归约

 reduce（T identity，BinaryOperator）/reduce（BinaryOperator），参数中的identity是初始运算的值，当然也可以不指定（返回的是Optional），BinaryOperator，是二元操作，比如加减乘除什么的都是

```Java
 @Test
    public void test02() {
        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);

        //处理流程：先根据初始值1（identity）和流中的第一个数组做二元操作，然后把算出来的这个值有跟下一个值做二元操作，直到结束。
        Integer integer = integers.stream().reduce(1, (x, y) -> x * y);
        System.out.println(integer);


        //处理流程和带identity的差不多，下边处理的是对员工id的加操作
        // 但是为什么返回值不同，是因为带有identity的reduce（）就不用考虑会出现空值现象，不带identity有可能会出现空值，所以返回值是Optional
        //map和reduce一般连起来用的比较多，也一般称为  map——reduce  模式（因Google用它来网络搜索而闻名）
        Optional<Integer> reduce = employees.stream().map(Employee::getId).reduce(Integer::sum);
        System.out.println(reduce.get());

    }
```

#### 收集

使用collect（）方法，接收一个Collector接口的实现（收集器），Collector接口的实现决定对流的收集方式，可以自定义，当然Collectors中由很多的静态方法供使用

```Java
 @Test
    public void test03() {

        //收集所有员工的名字，并且转化为List
        List<String> names = employees.stream().map(Employee::getName).collect(Collectors.toList());
        names.forEach(System.out::println);

        //收集到HashSet里
        HashSet<Integer> hashSet = employees.stream().map(Employee::getId).collect(Collectors.toCollection(HashSet::new));
        hashSet.forEach(System.out::println);

    }

    @Test
    public void test04() {
        //收集总数
        Long count = employees.stream().collect(Collectors.counting());
        System.out.println(count);

        //收集ID的平均值
        Double avg = employees.stream().collect(Collectors.averagingInt(Employee::getId));
        System.out.println(avg);

        //总和
        Integer sum = employees.stream().collect(Collectors.summingInt(Employee::getId));
        System.out.println(sum);

        //最大值，最小值,如果只需要单项或多项可以使用map过滤
        Optional<Employee> employee = employees.stream().collect(Collectors.maxBy((x, y) -> Integer.compare(x.getId(), y.getId())));
        System.out.println(employee);

        //分组
        Map<Integer, List<Employee>> listMap = employees.stream().collect(Collectors.groupingBy(Employee::getStatus));
        Set<Integer> keySet = listMap.keySet();
        for (Integer integer : keySet) {
            listMap.get(integer).forEach(System.out::println);
        }
        //多级分组,第一层分组是根据status分组的，因此Map第一层的key是integer，第二层是根据返回的信息分的，因此是String
        Map<Integer, Map<String, List<Employee>>> mapMap = employees.stream()
                .collect(Collectors.groupingBy(Employee::getId, Collectors.groupingBy((e) -> {
                    if (e.getId() <= 2) {
                        return "第一段";
                    } else if (e.getId() < 5) {
                        return "第二段";
                    } else {
                        return "第三段";
                    }
                })));
        System.out.println("多级分组");
        System.out.println(mapMap);
    }

    //分片（分区），满足条件的一个区，不满足的一个区，当然也可以多级分片
    @Test
    public void test05() {

        Map<Boolean, List<Employee>> part = employees.stream().collect(Collectors.partitioningBy((x) -> x.getId() > 3));
        System.out.println(part);

    }


    //连接函数
    @Test
    public void test07(){
        String collect = employees.stream().map(Employee::getName).collect(Collectors.joining());
        System.out.println(collect);
        String collect1 = employees.stream().map(Employee::getName).collect(Collectors.joining("，"));
        System.out.println(collect1);
        String collect2 = employees.stream().map(Employee::getName).collect(Collectors.joining("，","====","===="));
        System.out.println(collect2);

    }
```

#### 运算汇总函数

使用带有summarizing（），里面汇总由常用的数学运算，比如求平均，总和，最大，最小等等

```Java
   //一个汇总的函数，里面包含平均值，最大，最小等等
    @Test
    public void test06() {
        IntSummaryStatistics summaryStatistics = employees.stream().collect(Collectors.summarizingInt(Employee::getId));
        System.out.println(summaryStatistics.getAverage());
        System.out.println(summaryStatistics.getMax());
    }

```



### 并行流和串行流

并行流把一个内容分为多个模块，并且用不同的线程分别处理每个数据块的流。可以通过parallel（）和sequential（）在并行流和顺序流中切换。

当然，这就很自然的和Java并发中的Fork-Join联系在了一起，以为Fork-Jion也是将内容分为最下模块，分配给多个cpu去并行处理，并且它采取的是**“工作窃取模式”**，因为每个进程/线程工作速度和工作状态不同，先工作完的会随机从未工作完的任务的队尾中拿一个任务去工作。

#### 使用Fork-Join框架和串行，并行对比

使用Fork-Join框架必须要继承一个Recursive（英文：递归的，循环的）类，RecursiveAction和RecursiveTask<V>的区别就是，RecursiveTask是带有返回值的，而前者没有返回值。

![1559470082262](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1559470082262.png)

```Java

/*
 * 使用Fork-Join来计算大数字
 * */
public class ForkJoinCal extends RecursiveTask<Long> {

    private Long start;
    private Long end;

    public ForkJoinCal(long start, Long end) {
        this.start = start;
        this.end = end;
    }

    public static final long THRESHOLD = 10000;

    @Override
    protected Long compute() {
        long length = end - start;
        if (length < THRESHOLD) {

            long sum = 0;
            for (long i = start; i < end; i++) {
                sum += i;

            }
            return sum;
        } else {
            long middle = (start + end) / 2;
            ForkJoinCal left = new ForkJoinCal(start, middle);
            left.fork();//拆分子任务，同时压入线程队列
            ForkJoinCal right = new ForkJoinCal(middle, end);
            right.fork();
            return left.join() + right.join();
        }

    }
}


public class ForkJoinTest {


    //使用Fork-Join框架来计算
    @Test
    public void test01() {
        Instant start = Instant.now();

        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinTask<Long> task = new ForkJoinCal(0, 100000000L);
        Long sum = pool.invoke(task);
        System.out.println(sum);

        Instant end = Instant.now();
        System.out.println("耗费" + Duration.between(start, end).toMillis() + "毫秒");//92

    }

    @Test
    public void test02() {
        Instant start = Instant.now();
        Long sum = 0L;
        for (Long i = 0L; i < 100000000L; i++) {
            sum += i;
        }
        System.out.println(sum);
        Instant end = Instant.now();
        System.out.println("耗费" + Duration.between(start, end).toMillis() + "毫秒");//1223

    }

    //Java8并行流，parallel底层其实就是Fork-Join
    @Test
    public void test03() {

        Instant start = Instant.now();
        //rangeClosed包含边界，range不包含边界
        long sum = LongStream.range(0, 100000000L)
                .parallel()
                .reduce(0, Long::sum);
        System.out.println(sum);
        Instant end = Instant.now();
        System.out.println("耗费" + Duration.between(start, end).toMillis() + "毫秒");//43

    }
}

```



可以发现并行流和Fork-Join的运行速度都要快于串行，但是随之而来的是CPU占用率爆满。

## Optional

Optional<T>是一个容器类，Optional可以尽可能的避免空指针异常。以前直接是null，现在可以构造一个空的Optional对象，也可以设置默认值。

```Java

public class OptionalTest {

    /*
     * Optional常用的方法
     * Optional.of(T t):创建一个Optional实例
     * Optional.empty():创建一个空的Optional实例
     * Optional.ofNullable(T t):若t不为null，创建Optional实例，否则创建空实例
     * isPresent（）：判断是否包含值
     * orElse（T t）：如果调用对象包含值，返回该值，否则返回t
     * orElseGet（Supplier s）：如果调用对象包含值，返回该值，否则返回s获得的值
     * map（Function f）：如果有值对其处理，并返回处理后的Optional，否则返回Optional.empty()
     * flatMap（Function mapper）：和map用法相似，要求返回的值是Optional
     *
     *
     * */

    @Test
    public void test01() {
        //传空值就会报错
        Optional<Integer> op = Optional.of(1);
        System.out.println(op.get());
        //Optional不是为了防止空指针异常，而是为了快速定位空指针的位置
        Optional<Integer> op1 = Optional.of(null);
        System.out.println(op1.get());
    }

    @Test
    public void test02() {

        Optional<Object> empty = Optional.empty();

        //如果empty中有值就是empty中的值，如果没有就是后边指定的默认值（1）
        Object o = empty.orElse(1);
        System.out.println(o);

        //和orElse的区别就是可以在函数式接口中写很多的逻辑操作
        Object o1 = empty.orElseGet(() -> 2);
        System.out.println(o1);

        //是of和empty的综合
        Optional<Object> optional = Optional.ofNullable("xxx");
        if (optional.isPresent()) {
            System.out.println(optional.get());
        }

    }

    @Test
    public void test03() {
        Optional<Stu> op = Optional.ofNullable(new Stu(1, "zhangsan"));
        Optional<Integer> integer = op.map((x) -> x.getId());
        System.out.println(integer.get());

        //flatMap必须返回一个Optional类型的元素，更进一步避免空指针异常
        Optional<String> optional = op.flatMap((e) -> Optional.of(e.getName()));
        System.out.println(optional.get());

    }
}

```

## 新Interface

Java8中对接口进行了改变，可以增添自定义的默认接口实现，当然这也会造成一些问题，要是继承而来的方法和接口默认的方法一样，又或者是好几个接口中有默认方法是一样的，到底是执行那个呢？

下边就是接口中的“类优先原则”和“覆盖”。

![1559490131511](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1559490131511.png)



实例如下：

```Java

//父类
public class ParentClass {
    public String getName() {
        return "parent";
    }

}


//接口1
public interface AddInterface1 {

    default String getName(){
        return "interface1";
    }

}


//接口2
public interface AddInterface2 {

    default String getName(){
        return "interface2";
    }

}

//测试子类1
public class SubClass extends ParentClass implements AddInterface1, AddInterface2 {

    public static void main(String[] args) {
        SubClass subClass = new SubClass();
        String name = subClass.getName();
        System.out.println(name);//结果：parent
        
    }

}
//测试子类2

//也就是说，如果不同接口有相同默认方法的话，就得自己实现

public class SubClass implements AddInterface1, AddInterface2 {

    public static void main(String[] args) {
        SubClass subClass = new SubClass();
        String name = subClass.getName();
        System.out.println(name);//结果：我是为了覆盖接口的，不然会报错
    }

    @Override
    public String getName() {
        return "我是为了覆盖接口的，不然会报错";
    }
}

```

## 时间API

LocalDate，LocalTime，LocalDateTime都是**不可变对象**，分别表锁使用ISO-8601日历系统的日期，时间，日期和时间。

### 旧的时间API

旧的时间API不支持高并发，会导致线程不安全问题，因此在Java8中有了新的时间API，下来先简单看一看旧的时间API导致的线程不安全问题。

```Java

/*
 * 旧的不安全处理时间函数
 * */
public class NotSafeOldDate {

    public static void main(String[] args) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        ExecutorService pool = Executors.newFixedThreadPool(10);

        Callable<Date> callable = new Callable<Date>() {
            @Override
            public Date call() throws Exception {
                return sdf.parse("20190603");
            }
        };
        List<Future<Date>> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            list.add(pool.submit(callable));
        }
        for (Future<Date> future:list
             ) {
            try {
                System.out.println(future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
    }
}

```

![1560142374474](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1560142374474.png)

对旧的时间API进行线程安全处理,使用ThreadLocal进行处理，将每一个格式转换放在ThreadLocal中仅供当前线程使用，就可以保证线程安全。

```Java
/*
 * 使用ThreadLocal对SimpleDateFormat进行隔离
 *
 * */
public class DateFormatThreadLocal {
    private static ThreadLocal<DateFormat> map = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMdd");
        }
    };
    //从ThreadLocal中取
    public static Date dateConvert(String source) throws ParseException {
        return map.get().parse(source);

    }

    public static void main(String[] args) {

        ExecutorService pool = Executors.newFixedThreadPool(10);

        List<Future<Date>> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            list.add(pool.submit(  new Callable<Date>() {
                @Override
                public Date call() throws Exception {
                    return DateFormatThreadLocal.dateConvert("20190603");
                }
            }));
        }
        for (Future<Date> future:list) {
            try {
                System.out.println(future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        pool.shutdown();

    }

}
```

![1560145286143](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1560145286143.png)

**自定义格式表**

下面是自定义格式表

![1560146046963](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1560146046963.png)



### 新的时间API

测试新的时间API可不可以在并发环境下使用，使用DateTimeFormatter进行测试

可以通过下面三种方法进行使用。

![1560146352286](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1560146352286.png)

```Java
/*
 * Java8的时间函数
 * */
public class newLocalDate {


    public static void main(String[] args) {

        //新的时间处理函数
        //DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        ExecutorService pool = Executors.newFixedThreadPool(10);

        Callable<LocalDate> callable = new Callable<LocalDate>() {
            @Override
            public LocalDate call() throws Exception {
                return LocalDate.parse("20190603", dtf);
            }
        };
        List<Future<LocalDate>> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            list.add(pool.submit(callable));
        }
        for (Future<LocalDate> future : list
        ) {
            try {
                System.out.println(future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
        pool.shutdown();
    }

}

```

LocalDate,LocalTime,LocalDateTime使用方法基本相似，在下面进行演示。

```Java
import org.junit.jupiter.api.Test;
import java.time.*;
import java.time.temporal.TemporalAdjusters;

public class TestLocalDateTime {

    @Test
    public void test01() {

        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);


        LocalDateTime time = LocalDateTime.of(2019, 06, 03, 17, 02, 38);
        System.out.println(time);

        LocalDateTime plusDays = now.plusDays(2);
        System.out.println(plusDays);

        LocalDateTime minusDays = plusDays.minusDays(2);
        System.out.println(minusDays);

        System.out.println(minusDays.getYear());
        System.out.println(minusDays.getMonth());

    }

    //Instance:时间戳（以Unix元年：1970年1月1日 00：00：00到某个时间的毫秒值）
    @Test
    public void test02() {
        Instant now = Instant.now();//获取UTC时区（世界协调时间），和我们本地时间差八个小时
        System.out.println(now);

        //加8个小时偏移量，就是当前时间
        OffsetDateTime offsetDateTime = now.atOffset(ZoneOffset.ofHours(8));
        System.out.println(offsetDateTime);

        long epochMilli = now.toEpochMilli();
        System.out.println("时间戳：" + epochMilli);

        Instant instant = Instant.ofEpochSecond(0);
        System.out.println("Unix时间戳原点：" + instant);

        Instant second = Instant.ofEpochSecond(1);
        System.out.println("Unix时间原点偏移1s:" + second);

    }

    //Duration:计算两个时间之间的间隔
    //Period：计算两个日期之间的间隔
    @Test
    public void test03() {
        Instant now = Instant.now();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Instant now1 = Instant.now();

        Duration between = Duration.between(now, now1);
        System.out.println(between.toMillis());
        System.out.println("___________________________");

        LocalTime time = LocalTime.now();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LocalTime now2 = LocalTime.now();

        Duration duration = Duration.between(time, now2);
        System.out.println(duration.toMillis());
    }

    @Test
    public void test04() {

        LocalDate localDate1 = LocalDate.of(2019, 6, 3);
        LocalDate localDate2 = LocalDate.of(2021, 11, 27);
        Period between = Period.between(localDate1, localDate2);
        System.out.println(between);
        System.out.println(between.getYears());
        System.out.println(between.getMonths());
        System.out.println(between.getDays());

    }

}

```

使用时间API是实现一个周内时间矫正器

```Java

    //时间矫正器
    //TemporalAdjuster:时间矫正器
    //TemporalAdjusters：通过静态方法提供了大量的常用TemporalAdjuster的实现
    @Test
    public void test05() {

        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);

        LocalDateTime localDateTime = now.withDayOfMonth(10);
        System.out.println(localDateTime);

        LocalDateTime with = now.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
        System.out.println(with);

        //自定义一个时间矫正器，返回距离下一个工作日还有几天
        LocalDateTime time = now.with((l) -> {

            LocalDateTime ldt = (LocalDateTime) l;
            if (ldt.getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
                return ldt.plusDays(3);
            } else if (ldt.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                return ldt.plusDays(2);

            } else {
                return ldt.plusDays(1);
            }

        });
        System.out.println("下一个工作日是："+time.getYear()+"年"+time.getMonth()+"月"+time.getDayOfMonth()+"日,是"+time.getDayOfWeek());


    }

```

![1560146440272](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1560146440272.png)

## 总结

Java8给了一种很便捷的函数式编程方法，是一个思想的提升。

