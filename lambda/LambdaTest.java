package lambda;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class LambdaTest {

    interface IEat {
        void eat();
    }

    /*
     * 无参的lambda
     * */
    class IEatImpl implements IEat {
        @Override
        public void eat() {
            System.out.println("普通->eat");
        }
    }

    @Test
    public void TestEat() {

        new IEatImpl().eat();
        new IEat() {
            @Override
            public void eat() {
                System.out.println("匿名->eat");
            }
        }.eat();

        //1.不会再生成类
        // 2.代码简洁
        //3.实现的接口只能有一个方法，接口中的静态方法和默认方法对lambda无影响
        IEat iEat = () -> System.out.println("lambda->eat");
        iEat.eat();

    }

    /*
     * 有参的lambda表达式
     * 如果要在变量上加修饰符，如final
     * 就必须把参数写全 IEat2 iEat2 = (final String name,final String type) -> System.out.println(name + "正在吃" + type);
     * */
    interface IEat2 {
        void eat(String name, String type);
    }

    @Test
    public void TestEat2() {
        IEat2 iEat2 = (String name, String type) -> System.out.println(name + "正在吃" + type);
        iEat2.eat("李四", "稀饭");
        IEat2 iEat3 = (name, type) -> System.out.println(name + "正在吃" + type);
        iEat2.eat("王五", "米饭");
        IEat2 iEat4 = (name, type) -> {
            System.out.println("语句块开始");
            System.out.println(name);
            System.out.println(type);
            System.out.println("语句块结束");
        };
    }

    /*
     * 带返回值的
     * */
    interface IEat3 {
        String eat(String name, String type);
    }

    @Test
    public void TestEat3() {
        IEat3 eat3 = (name, type) -> {
            System.out.println("lambda内:" + name + "正在吃" + type);
            return "返回:" + name + "正在吃" + type;
        };
        IEat3 eat4 = (name, type) -> name == null ? "name null" : "name not null";
        String eat1 = eat4.eat(null, "");
        System.out.println(eat1);
        String eat = eat3.eat("赵磊", "快餐");
        System.out.println(eat);
    }
    /*
     * lambda在Thread和Comparable中的使用
     * */

    @Test
    public void TestOldRunable() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Thread will start!--->>old achieve method");
            }
        }).start();
        //lambda方式
        new Thread(() -> System.out.println("Thread will be start!--->>lambda expression")).start();

    }

    @Test
    public void TestComparable() {
        Student[] students = {new Student("张三", 001),
                new Student("张四", 002),
                new Student("张五", 006)
        };
        Arrays.sort(students, new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                return o2.id - o1.id;
            }
        });
        System.out.println("traditional:" + Arrays.toString(students));
        //lambda
//        Comparator<Student> comparator = (o1, o2) -> o1.getId() - o2.getId();
//        Arrays.sort(students, comparator);
        Arrays.sort(students, (o1, o2) -> o1.getId() - o2.getId());
        System.out.println("lambda:" + Arrays.toString(students));

    }

    //list
    @Test
    public void TestList() {
        //foreach
        List<Integer> list = Arrays.asList(1, 2, 3, 5, 888);
        for (Integer i : list
        ) {
            System.out.println(i);
        }
        //lambda1
        list.forEach(a -> System.out.println(a));

        //lambda2
        list.forEach(System.out::println);
    }


}
