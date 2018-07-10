##Guava Ordering的使用
##参考
[官方说明文档-OrderingExplained](https://github.com/google/guava/wiki/OrderingExplained)
[Guava强大的”流畅风格比较器”-翻译版](http://ifeve.com/google-guava-ordering/)
[ Guava ---- Ordering排序工具-简单的使用](http://blog.csdn.net/wenniuwuren/article/details/46361119)
[Guava学习笔记：Ordering犀利的比较器](https://www.cnblogs.com/peida/p/Guava_Ordering.html)

##概述
排序器Ordering是Guava流畅风格比较器Comparator的实现，它可以用来为构建复杂的比较器，以完成集合排序的功能。
从实现上说，Ordering实例就是一个特殊的Comparator实例。Ordering把很多基于Comparator的静态方法（如Collections.max）包装为自己的实例方法（非静态方法），并且提供了链式调用方法，来定制和增强现有的比较器。

##简单使用

###创建方式
#### Ordering.natural() 
对可排序类型做自然排序，如数字按大小，日期按先后排序
```
  List<Integer> list = Arrays.asList(1, 5, 3, 8, 2);
  Collections.sort(list, Ordering.natural());
```
####Ordering.usingToString
按对象的字符串形式做字典排序
```
 List<String> listString = Lists.newArrayList("wang", "test", "a");
 Collections.sort(listString, Ordering.usingToString());
```
####Ordering.from(Comparator)
使预先存在的比较器进行排序
```
List<Worker> workers = Lists.newArrayList(worker, worker2, worker22, worker3);
Ordering<Worker> orderWorker = Ordering.from(new WorkerIdComparator());
Collections.sort(workers, orderWorker);
```

###链式调用方法：通过链式调用，可以由给定的排序器衍生出其它排序器
####reverse
获取语义相反的排序器
```
@Test
public void testOrderReverse() {
    List<Integer> list = Arrays.asList(1, 5, 3, 8, 2);
    Collections.sort(list, Ordering.natural().reverse());
    log.info("获取最大的元素" + Ordering.natural().max(list).toString());
    log.info("排序后" + list.toString());
}
```

####nullsFirst nullsLast
使用当前排序器，但额外把null值排到最前面，最后,原生的JDKCollections.sort会出现异常的，如果排序元素为空
原生调用
```
/**
     * 排序中出现，空指针会异常的！
     */
    @Test(expected = NullPointerException.class)
    public void testJDKOrderIssue() {
        List<Integer> list = Arrays.asList(1, 5, null, 3, 8, 2);
        log.info("排序前" + list.toString());
        Collections.sort(list); // 出现异常...
    }
```
使用Guava Ordering
```
/**
 * 将空值放置在最前面的情况
 */
@Test
public void testOrderNaturalByNullFirst() {
    List<Integer> list = Arrays.asList(1, 5, null, 3, 8, 2);
    Collections.sort(list, Ordering.natural().nullsFirst());
    log.info("空在最前面-排序后" + list.toString());
}
```
####多字段排序 compound(Comparator)
合成另一个比较器，以处理当前排序器中的相等情况。
```
public class WorkerIdComparator implements Comparator<Worker> {
    @Override
    public int compare(Worker worker1, Worker worker2) {
        return Ints.compare(worker1.getWorkId(), worker2.getWorkId());
    }
}

public class WorkerNameComparator implements Comparator<Worker> {
    @Override
    public int compare(Worker worker1, Worker worker2) {
        return worker1.getName().compareTo(worker2.getName());
    }
}


/**
 * http://blog.csdn.net/wenniuwuren/article/details/46361119
 * guava 多参数排序
 * http://ifeve.com/google-guava-ordering/
 * Ordering.from 的使用
 */
@Test
public void testFrom() {
    Worker worker = new Worker(1, "wangji");
    Worker worker2 = new Worker(11, "guava");
    Worker worker22 = new Worker(11, "aaa");
    Worker worker3 = new Worker(111, "wangji3");
    List<Worker> workers = Lists.newArrayList(worker, worker2, worker22, worker3);
    Ordering<Worker> orderWorker = Ordering.from(new WorkerIdComparator());
    Collections.sort(workers, orderWorker);
    log.info("单参数：" + workers.toString());

    // compound 相等的情况下，考虑第二个参数进行比较
    Ordering<Worker> orderManyWorker = Ordering.from(new WorkerIdComparator()).compound(new WorkerNameComparator()).reverse();
    Collections.sort(workers, orderManyWorker);
    log.info("多参数：" + workers.toString());

}
```
###运用排序器：Guava的排序器实现有若干操纵集合或元素值的方法
####isOrdered
判断可迭代对象是否已按排序器排序：允许有排序值相等的元素。
```
/**
 * isOrdered(Iterable)	判断可迭代对象是否已按排序器排序：允许有排序值相等的元素。
 */
@Test
public void testOrderNatural() {
    List<Integer> list = Arrays.asList(1, 5, 3, 8, 2);
    Collections.sort(list);
    boolean order = Ordering.natural().isOrdered(list); //是否为按照这样的顺序排好序的！自然的排序
    log.info("排好序的：" + (order == true ? "是的" : "不是"));
}
```

####min(E, E),min(E, E, E, E...),min(Iterable)
返回两个参数中最小的那个。如果相等，则返回第一个参数。
返回多个参数中最小的那个。如果有超过一个参数都最小，则返回第一个最小的参数
返回迭代器中最小的元素。如果可迭代对象中没有元素，则抛出NoSuchElementException。
max同理，这样的操作节省了一点时间和精力
```
/**
 * 返回元素中的最大的值,同理最小的
 */
@Test
public void testMax() {
    List<Integer> list = Arrays.asList(1, 5, 3, 8, 2);
    log.info("获取最大的元素" + Ordering.natural().max(list).toString()); //获取最大的元素8

    log.info("获取最大的元素" + Ordering.natural().reverse().max(list).toString());//获取最大的元素1
}
```

####greatestOf
获取可迭代对象中最大的k个元素,简化我们的操作的工作量
```
/**
 * 返回一个排序对象中的最大的多少个元素-是不稳定的。
 */
@Test
public void testGreaTestOf() {
    List<Integer> list = Arrays.asList(1, 5, 3, 8, 2);
    List<Integer> listMaxtOfK = Ordering.natural().greatestOf(list, 3);//获取可迭代对象中最大的k个元素。
    log.info("获取最大的k个元素：" + listMaxtOfK.toString());

    List<Integer> listMaxtOfMinik = Ordering.natural().reverse().greatestOf(list, 3);
    // listMaxtOfK.add(1); UnmodifiableCollection 返回的是不可变对象，不可以进行操作
    log.info("获取最大的Minik个元素：" + listMaxtOfMinik.toString());
}
```

####sortedCopy(Iterable) immutableSortedCopy（Iterable）
复制排序后的元素的副本，不过这里不是深复制，需要保证集合元素中的不可变性，返回可变的集合元素和不可变的集合元素
```
/**
 * 复制一个副本，这里没有进行集合元素的深复制，需要保证集合对象中为不可变对象
 * 这里可以通过sortedCopy，返回一个可变的对象，immutableSortedCopy返回一个不可变的对象
 */
@Test
public void testSortedCopy() {
    List<Integer> list = Arrays.asList(1, 5, 3, 8, 2);
    List<Integer> duplicateList = Ordering.natural().sortedCopy(list);// 返回一个副本
    if (!Objects.equals(list, duplicateList)) {
        log.info("返回的一个副本" + duplicateList.toString());
        duplicateList.add(10);
        log.info("返回的一个可变的副本" + duplicateList.toString());

    }
    List<Integer> duplicateImmutableList = Ordering.natural().immutableSortedCopy(list); // 返回一个不可变的副本
    if (!Objects.equals(list, duplicateImmutableList)) {
        log.info("返回的一个副本" + duplicateImmutableList.toString());
        try {
            duplicateImmutableList.add(10);
        } catch (Exception e) {
            log.info("返回的一个可变的副本不能添加");
        }
    }
}
```

###所有的代码 guava 23.0
#### OrderExample
```
package guava.order;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * descrption: 排序的简单使用（Ordering是Guava流畅风格比较器Comparator的实现，它可以用来为构建复杂的比较器，以完成集合排序的功能。）
 * authohr: wangji
 * date: 2018-02-07 9:34
 */
@Slf4j
public class OrderExample {

    @Test
    public void testJDKOrder() {
        List<Integer> list = Arrays.asList(1, 5, 3, 8, 2);
        log.info("排序前" + list.toString());
        Collections.sort(list);
        log.info("排序后" + list.toString());
    }

    /**
     * 排序中出现，空指针会异常的！
     */
    @Test(expected = NullPointerException.class)
    public void testJDKOrderIssue() {
        List<Integer> list = Arrays.asList(1, 5, null, 3, 8, 2);
        log.info("排序前" + list.toString());
        Collections.sort(list); // 出现异常...
    }

    /**
     * 将空值放置在最前面的情况
     */
    @Test
    public void testOrderNaturalByNullFirst() {
        List<Integer> list = Arrays.asList(1, 5, null, 3, 8, 2);
        Collections.sort(list, Ordering.natural().nullsFirst());
        log.info("空在最前面-排序后" + list.toString());
    }

    /**
     * 将空值放置在最后面的情况
     */
    @Test
    public void testOrderNaturalByNullLast() {
        List<Integer> list = Arrays.asList(1, 5, null, 3, 8, 2);
        Collections.sort(list, Ordering.natural().nullsLast());
        log.info("空在最后面-排序后" + list.toString());
    }

    /**
     * isOrdered(Iterable)	判断可迭代对象是否已按排序器排序：允许有排序值相等的元素。
     */
    @Test
    public void testOrderNatural() {
        List<Integer> list = Arrays.asList(1, 5, 3, 8, 2);
        Collections.sort(list);
        boolean order = Ordering.natural().isOrdered(list); //是否为按照这样的顺序排好序的！自然的排序
        log.info("排好序的：" + (order == true ? "是的" : "不是"));
    }


    /**
     * reverse()	获取语义相反的排序器
     */
    @Test
    public void testOrderReverse() {
        List<Integer> list = Arrays.asList(1, 5, 3, 8, 2);
        Collections.sort(list, Ordering.natural().reverse());
        log.info("获取最大的元素" + Ordering.natural().max(list).toString());
        log.info("排序后" + list.toString());
    }

    /**
     * 返回元素中的最大的值,同理最小的
     */
    @Test
    public void testMax() {
        List<Integer> list = Arrays.asList(1, 5, 3, 8, 2);
        log.info("获取最大的元素" + Ordering.natural().max(list).toString()); //获取最大的元素8

        log.info("获取最大的元素" + Ordering.natural().reverse().max(list).toString());//获取最大的元素1
    }

    /**
     * 返回一个排序对象中的最大的多少个元素-是不稳定的。
     */
    @Test
    public void testGreaTestOf() {
        List<Integer> list = Arrays.asList(1, 5, 3, 8, 2);
        List<Integer> listMaxtOfK = Ordering.natural().greatestOf(list, 3);//获取可迭代对象中最大的k个元素。
        log.info("获取最大的k个元素：" + listMaxtOfK.toString());

        List<Integer> listMaxtOfMinik = Ordering.natural().reverse().greatestOf(list, 3);
        // listMaxtOfK.add(1); UnmodifiableCollection 返回的是不可变对象，不可以进行操作
        log.info("获取最大的Minik个元素：" + listMaxtOfMinik.toString());
    }

    /**
     * natural()：使用Comparable类型的自然顺序， 例如：整数从小到大，字符串是按字典顺序;
     * usingToString() ：使用toString()返回的字符串按字典顺序进行排序；
     */
    @Test
    public void testUsingToString() {
        List<Integer> list = Arrays.asList(1, 5, 3, 8, 2);
        Collections.sort(list, Ordering.usingToString());
        log.info("排序后" + list.toString());

        List<String> listString = Lists.newArrayList("wang", "test", "a");
        Collections.sort(listString, Ordering.usingToString());
        log.info("排序后" + listString.toString());
    }

    /**
     * 复制一个副本，这里没有进行集合元素的深复制，需要保证集合对象中为不可变对象
     * 这里可以通过sortedCopy，返回一个可变的对象，immutableSortedCopy返回一个不可变的对象
     */
    @Test
    public void testSortedCopy() {
        List<Integer> list = Arrays.asList(1, 5, 3, 8, 2);
        List<Integer> duplicateList = Ordering.natural().sortedCopy(list);// 返回一个副本
        if (!Objects.equals(list, duplicateList)) {
            log.info("返回的一个副本" + duplicateList.toString());
            duplicateList.add(10);
            log.info("返回的一个可变的副本" + duplicateList.toString());

        }
        List<Integer> duplicateImmutableList = Ordering.natural().immutableSortedCopy(list); // 返回一个不可变的副本
        if (!Objects.equals(list, duplicateImmutableList)) {
            log.info("返回的一个副本" + duplicateImmutableList.toString());
            try {
                duplicateImmutableList.add(10);
            } catch (Exception e) {
                log.info("返回的一个可变的副本不能添加");
            }
        }
    }
}

```

####多字段排序
```
@Slf4j
public class Worker {
    private Integer workId;
    private String name;

    public Integer getWorkId() {
        return workId;
    }

    public void setWorkId(Integer workId) {
        this.workId = workId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Worker(Integer workId, String name) {
        this.workId = workId;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Worker{" +
                "workId=" + workId +
                ", name='" + name + '\'' +
                '}';
    }
}

```
```
package guava.order;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * descrption: 使用自定义排序规则、多字段排序
 * authohr: wangji
 * date: 2018-02-07 10:21
 */
@Slf4j
public class OrderExample2 {

    public class WorkerIdComparator implements Comparator<Worker> {
        @Override
        public int compare(Worker worker1, Worker worker2) {
            return Ints.compare(worker1.getWorkId(), worker2.getWorkId());
        }
    }

    public class WorkerNameComparator implements Comparator<Worker> {
        @Override
        public int compare(Worker worker1, Worker worker2) {
            return worker1.getName().compareTo(worker2.getName());
        }
    }


    /**
     * http://blog.csdn.net/wenniuwuren/article/details/46361119
     * guava 多参数排序
     * http://ifeve.com/google-guava-ordering/
     * Ordering.from 的使用
     */
    @Test
    public void testFrom() {
        Worker worker = new Worker(1, "wangji");
        Worker worker2 = new Worker(11, "guava");
        Worker worker22 = new Worker(11, "aaa");
        Worker worker3 = new Worker(111, "wangji3");
        List<Worker> workers = Lists.newArrayList(worker, worker2, worker22, worker3);
        Ordering<Worker> orderWorker = Ordering.from(new WorkerIdComparator());
        Collections.sort(workers, orderWorker);
        log.info("单参数：" + workers.toString());

        // compound 相等的情况下，考虑第二个参数进行比较
        Ordering<Worker> orderManyWorker = Ordering.from(new WorkerIdComparator()).compound(new WorkerNameComparator()).reverse();
        Collections.sort(workers, orderManyWorker);
        log.info("多参数：" + workers.toString());

    }
}

```

###多看学、多总结
