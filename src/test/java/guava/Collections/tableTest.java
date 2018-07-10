package guava.Collections;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Table;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * descrption: http://blog.csdn.net/bazingaea/article/details/51233969
 * authohr: wangji
 * date: 2018-03-07 18:20
 */
public class tableTest {

    @Test
    public void testTable() {
        //1.把数据存储到Table中，通过HashBasedTable.create()新建一个Table对象
        Table<String,String,Integer> tables= HashBasedTable.create();
        tables.put("stundent1", "javase", 80);
        tables.put("stundent2", "javaee", 90);
        tables.put("stundent3", "javame", 100);
        tables.put("stundent4", "guava", 70);

        //2.得到所有行数据 tables.cellSet()
        Set<Table.Cell<String,String,Integer>> cells=tables.cellSet();
        for(Table.Cell<String,String,Integer> temp:cells){
            System.out.println(temp.getRowKey()+" "+temp.getColumnKey()+" "+temp.getValue());//stundent1 javase 80
        }
        //3. 得到所有学生 rowKeySet()
        Set<String> students=tables.rowKeySet();
        for(String str:students){
            System.out.print(str+"\t");//stundent1	stundent2	stundent3	stundent4
        }
        //4.得到所有课程 columnKeySet()
        Set<String> courses=tables.columnKeySet();//javase	javaee	javame	guava
        for(String str:courses){
            System.out.print(str+"\t");
        }
        //5.得到所有成绩：values
        Collection<Integer> scores=tables.values();//80	90	100	70
        for(Integer in:scores){
            System.out.print(in+"\t");
        }

        Set<Integer> set = new HashSet<Integer>();
        set.add(1);
        set.add(2);
        ImmutableSet<Integer> immutableSet = ImmutableSet.copyOf(set);
        immutableSet.asList();

        //这里是不支持添加元素的
        /*immutableSet.add(4);
        Set<Integer> addOne = new HashSet<>();
        addOne.add(5);
        immutableSet.addAll(addOne);*/
    }
}
