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
 * descrption: 排序的简单使用（Ordering是Guava流畅风格比较器[Comparator]的实现，它可以用来为构建复杂的比较器，以完成集合排序的功能。）
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
            log.info("返回的一个可变的副本" + list.toString());

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
