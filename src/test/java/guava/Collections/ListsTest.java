package guava.Collections;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * descrption:
 * authohr: wangji
 * date: 2018-03-07 15:37
 */
public class ListsTest {

    @Test
    public void testListGeneric() {
        List<String> list1 = new ArrayList<String>(); //原始的方式
        list1.add("a");
        list1.add("b");

        List<String> list2 = Lists.newArrayList();
        list2.add("a");
        list2.add("b");

        List<String> list3 = Lists.newArrayList("a", "b");
        List<String> list4 = Lists.asList("a", "b", new String[]{"c", "d"});
        
    }

    }
