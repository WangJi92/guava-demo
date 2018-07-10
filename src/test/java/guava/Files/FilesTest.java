package guava.Files;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * descrption:
 * authohr: wangji
 * date: 2018-03-08 9:56
 */
@Slf4j
public class FilesTest {
    @Test
    public void doTest(){
        log.info("wang" + 1);
        List<Integer> list  =  Lists.newArrayListWithCapacity(1);
        List<Integer> list2 =  Lists.newArrayList(1); //这里实现设置了处理化的大小根据当前的数据的大小

        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(1);

        List<String> result = Splitter.on(":").omitEmptyStrings().trimResults().splitToList(" hello : world :::");
        log.info(result.toString());//[hello, world]
    }
}
