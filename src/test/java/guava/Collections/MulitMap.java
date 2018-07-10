package guava.Collections;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * descrption:
 * authohr: wangji
 * date: 2018-03-07 15:51
 */
public class MulitMap {

    @Test
    public void test() {
        Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
        List<Integer> objectList = map.get("key");
        if (objectList != null) {
            objectList.add(1);
        } else {
            List<Integer> list = Lists.newArrayList();
            list.add(1);
            map.put("key", list);
        }
        System.out.println(map);//{key=[1]}
    }


    @Test
    public void testMultimap() {
        Multimap<String, String> myMultimap = ArrayListMultimap.create();

        myMultimap.put("Fruits", "Bannana");
        myMultimap.put("Fruits", "Apple");
        myMultimap.put("Fruits", "Pear");
        myMultimap.put("Vegetables", "Carrot");

        int size = myMultimap.size();
        System.out.println(size);//4

        Collection<String> fruits = myMultimap.get("Fruits");
        System.out.println(fruits);//[Bannana,Apple]

        myMultimap.remove("Fruits", "Pear");
        System.out.println(myMultimap.get("Fruits"));//[Bannana,Apple]

        myMultimap.removeAll("Fruits");
        System.out.println(myMultimap.get("Fruits"));//[](EmptyCollection!)
    }


}
