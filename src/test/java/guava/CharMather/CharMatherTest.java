package guava.CharMather;

import com.google.common.base.CaseFormat;
import com.google.common.base.CharMatcher;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * https://github.com/google/guava/wiki/StringsExplained
 * http://blog.csdn.net/victor_cindy1/article/details/52073155
 * descrption: CharMatherTest
 * authohr: wangji
 * date: 2018-03-07 13:54
 */
@Slf4j
public class CharMatherTest {
    @Test
    public void test() {
        //返回一个字符串,其中包含所有匹配字符的字符序列
        String result = CharMatcher.digit().retainFrom("some text 89983 and more");
        log.info(result);//89983

        result = CharMatcher.digit().removeFrom("some text 89983 and more");

        log.info(result);//some text  and more
        result = CharMatcher.anyOf("abcxy").removeFrom("abcdefgxyz");
        log.info(result);//defgz

        //匹配条件复合运算
        String str1 = CharMatcher.inRange('a', 'f')
                .and(CharMatcher.isNot('c'))
                .and(CharMatcher.isNot('e'))
                .or(CharMatcher.is('z'))
                .replaceFrom("abyczef", "*");
        log.info(str1); //**yc*e*
    }

    @Test
    public void test2() {
        String str = "FirstName LastName +1 123 456 789 !@#$%^&*()_+|}{:?><";
        String result = CharMatcher.javaLowerCase(). //只匹配大写字母
                negate().// 然后取反
                retainFrom(str);
        log.info("result:" + result); //FN LN +1 123 456 789 !@#$%^&*()_+|}{:"?><

        result = CharMatcher.javaDigit().or(CharMatcher.anyOf("aeiou")).retainFrom(str);
        log.info("result:" + result);//iaeaae1123456789

        //原字符串
        String string = " ROCKY  rocky\tRoCkY ~!@#$%^&*()   \n   23(*&gS   你好 234啊   GES";
        System.out.println(string);

        //去掉控制字符(\t,\n,\b...)
        System.out.println(CharMatcher.javaIsoControl().removeFrom(string));

        //获取所有的数字
        System.out.println(CharMatcher.javaDigit().retainFrom(string));

        //把多个空格替换为一个包括\t,并去掉首位的空格
        System.out.println(CharMatcher.whitespace().trimAndCollapseFrom(string, ' '));

        //把所有的数字用"*"代替
        System.out.println(CharMatcher.javaDigit().replaceFrom(string, "*"));

        //获取所有的数字和小写字母
        System.out.println(CharMatcher.javaDigit().or(CharMatcher.javaLowerCase()).retainFrom(string));

        //获取所有的大写字母
        System.out.println(CharMatcher.javaUpperCase().retainFrom(string));

        System.out.println(CharMatcher.breakingWhitespace().retainFrom(string));




    }

    @Test
    public void testCaseFormat() {
        System.out.println(CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, "test-data"));//testData
        System.out.println(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "test_data"));//testData
        System.out.println(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, "test_data"));//TestData

        System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "testData"));//test_data
        System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "testData"));//test_data
        System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, "testData"));//     test-data
    }
}
