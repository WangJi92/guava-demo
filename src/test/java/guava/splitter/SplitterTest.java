package guava.splitter;

import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * descrption: 字符串分隔处理
 * authohr: wangji
 * date: 2018-02-25 13:55
 */
@Slf4j
public class SplitterTest {

    @Test
    public void testSplitOnSplit() {
        List<String> result = Splitter.on("|").splitToList("hello|world");
        log.info("result："+result.toString());
        log.info("size："+result.size());

    }
    /**
     * 排除掉空字符串 omitEmptyStrings
     */
    @Test
    public void testSplit_On_Split_OmitEmpty() {
        List<String> result = Splitter.on("&&").trimResults().splitToList("user000000&&&&&&&&TGT-15-tFjCzuy9GZfB1dzptolOUdcfcg9HNhblB7qv3bh3bOLFnGLRUI-cas&&zh_CN \n");
        log.info("result："+result.toString());//[hello, world, , , ]
        log.info("size："+result.size());//5

        result = Splitter.on("|").omitEmptyStrings().splitToList("hello|world|||");
        log.info("result："+result.toString()); //result：[hello, world]
        log.info("size："+result.size());//2
    }

    /**
     * 去掉分隔后字符串两边的空格 trimResults
     */
    @Test
    public void testSplit_On_Split_OmitEmpty_TrimResult() {
        List<String> result = Splitter.on("|").omitEmptyStrings().splitToList("hello | world|||");
        log.info("result："+result.toString());// [hello ,  world]
        log.info("size："+result.size());// 2

        result = Splitter.on("|").trimResults().omitEmptyStrings().splitToList("hello | world|||");
        log.info("result："+result.toString());
        log.info("size："+result.size());
    }

    /**
     * 使用固定的长度分隔字符串 fixedLength
     */
    @Test
    public void testSplitFixLength() {
        List<String> result = Splitter.fixedLength(4).splitToList("aaaabbbbccccdddd");
        log.info("result："+result.toString());//[aaaa, bbbb, cccc, dddd]
        log.info("size："+result.size());
    }

    /**
     * 对于分隔最后的结果进行限制，达到之后将不再进行分隔 limit
     */
    @Test
    public void testSplitOnSplitLimit() {
        List<String> result = Splitter.on("#").limit(3).splitToList("hello#world#java#google#scala");
        log.info("result："+result.toString()); //[hello, world, java#google#scala]
        log.info("size："+result.size());//3
    }


    /**
     * 使用模式匹配进行分隔 Splitter.onPattern("\\|"),通过字符串的形式
     */
    @Test
    public void testSplitOnPatternString() {
        List<String> result = Splitter.onPattern("\\|").trimResults().omitEmptyStrings().splitToList("hello | world|||");
        log.info("result："+result.toString()); //[hello, world]
        log.info("size："+result.size());//2
    }

    /**
     * 使用模式匹配进行分隔 Splitter.on(Pattern.compile("\\|"))，通过原生的形式
     */
    @Test
    public void testSplitOnPattern() {
        List<String> result = Splitter.on(Pattern.compile("\\|")).trimResults().omitEmptyStrings().splitToList("hello | world|||");
        log.info("result："+result.toString()); //[hello, world]
        log.info("size："+result.size());//2
    }


    /**
     * 首先使用|分隔为一个Map的key-value，然后通过分隔Map中的key值和value值进行分隔。
     */
    @Test
    public void testSplitOnSplitToMap() {
        Map<String, String> result = Splitter.on("|").trimResults()
                                                     .omitEmptyStrings()
                                                     .withKeyValueSeparator("=")
                                                     .split("hello=HELLO| world=WORLD|||");
        log.info("result："+result.toString()); //{hello=HELLO, world=WORLD}
        log.info("size："+result.size());//2

    }

    @Test
    public void testSplitOnSplitToMapSimple() {
        Map<String, String> result = Splitter.on("|")
                .withKeyValueSeparator("=")
                .split("hello=HELLO|world=WORLD");
        log.info("result："+result.toString()); //{hello=HELLO, world=WORLD}
        log.info("size："+result.size());//2

    }

    @Test
    public void testOld(){
        List<String> list = new ArrayList<String>();
        String a = "1-2-3-4-5-6";
        String[] strs = a.split("-");
        for(int i=0; i<strs.length; i++){
            list.add(strs[i]);
        }
        log.info("result:"+list.toString());
    }
}
