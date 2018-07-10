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
