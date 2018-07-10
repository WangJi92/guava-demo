package guava.Collections;

import com.google.common.collect.HashBiMap;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * descrption:
 * authohr: wangji
 * date: 2018-03-07 16:32
 */
@Slf4j
public class BiMap {

    @Test
    public void testBiMap() {
        HashBiMap<String, String> biMap = HashBiMap.create();
        biMap.put("1", "2");
        biMap.put("2", "3");
        biMap.put("3", "4");
        log.info(biMap.toString());//{1=2, 2=3, 3=4}
        biMap.forcePut("5","2");
        log.info(biMap.toString());//{2=3, 3=4, 5=2}
    }
}
