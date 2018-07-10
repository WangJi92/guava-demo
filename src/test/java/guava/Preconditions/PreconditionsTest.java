package guava.Preconditions;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * descrption: 参数检测
 * authohr: wangji
 * date: 2018-03-08 9:34
 */
@Slf4j
public class PreconditionsTest {

    @Test(expected = IllegalArgumentException.class)
    public void testJava(){
        //use java
        int count = 0;
        if (count <= 0) {
            throw new IllegalArgumentException("must be positive: " + count);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPreconditions(){
        // 免去了很多麻烦！并且会使你的代码看上去更好看,统一参数异常的检测。
        int count = 0;
        //use guava
        Preconditions.checkArgument(count > 0, "must be positive: %s", count);
    }
}
