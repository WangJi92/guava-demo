package guava.Collections;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * descrption:
 * authohr: wangji
 * date: 2018-03-07 16:59
 */
@Slf4j
public class HashMultisetTest {
    @Test
    public void testMultiset() {
        Multiset<String> wordsMultiset = HashMultiset.create();
        wordsMultiset.addAll(Lists.newArrayList("good", "good", "study", "day", "day", "up"));
        log.info(""+wordsMultiset.count("good"));//2
        log.info(wordsMultiset.toString());//[study, up, good x 2, day x 2]
        wordsMultiset.remove("good", 2);
        log.info(""+wordsMultiset.count("good"));//0
    }
}
