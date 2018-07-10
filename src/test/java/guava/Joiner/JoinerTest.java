package guava.Joiner;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * descrption: 字符串连接操作工具
 * authohr: wangji
 * date: 2018-02-25 14:27
 */
@Slf4j
public class JoinerTest {







    private final List<String> stringList = Arrays.asList("Google", "Guava", "Java", "Scala", "Kafka");

    /**
     * 基本用法
     */
    @Test
    public void testJoinOnJoin() {
        String result = Joiner.on("#").join(stringList);
        log.info("result:"+result);
    }

    /**
     * 集合转换为字符串存在空指针，掏出异常
     */
    @Test(expected = NullPointerException.class)
    public void testJoinOnJoinWithNullValue() {
        // 集合中不允许出现空值，否则掏出异常
        String result = Joiner.on("#").join(stringListWithNullValue);
        log.info("result:"+result);
    }

    private final List<String> stringListWithNullValue = Arrays.asList("Google", "Guava", "Java", "Scala", null);
    /**
     * 跳过空值集合 skipNulls
     */
    @Test
    public void testJoinOnJoinWithNullValueButSkip() {
        String result = Joiner.on("#").skipNulls().join(stringListWithNullValue);
        log.info("result:"+result);
}


    /**
     * 出现空指针使用默认的字符串代替 useForNull("DEFAULT")
     */
    @Test
    public void testJoin_On_Join_WithNullValue_UseDefaultValue() {
        String result = Joiner.on("#").useForNull("DEFAULT").join(stringListWithNullValue);
        log.info("result:"+result);
    }


    /**
     * 将分隔后的集合添加到具体的存在的Appendable对象上，可以
     */
    @Test
    public void testJoin_On_Append_To_StringBuilder() {
        final StringBuilder builder = new StringBuilder();
        Joiner.on("#").useForNull("DEFAULT").appendTo(builder, stringListWithNullValue);
        log.info("result:"+builder.toString());//Google#Guava#Java#Scala#DEFAULT
    }



    private final Map<String, String> stringMap = ImmutableMap.of("Hello", "Guava", "Java", "Scala");
    /**
     * 将一个Map类型的数据转换为一个有规则的字符串
     */
    @Test
    public void testJoinOnWithMap() {
        String result = Joiner.on('#').withKeyValueSeparator("=").join(stringMap);
        log.info("result:"+result);// Hello=Guava#Java=Scala
    }

    @Test
    public void testOld(){
        List<String> list = new ArrayList<String>();
        list.add("aa");
        list.add("bb");
        list.add("cc");
        StringBuffer buffer = new StringBuffer(10);
        buffer.append(list.get(0));
        for(int i=1; i<list.size(); i++){
            buffer.append("#").append(list.get(i));
        }
        log.info("result:"+buffer.toString());
    }
}
