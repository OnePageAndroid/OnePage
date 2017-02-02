package kr.nexters.onepage;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void parse_stringWeather() {
        String t1 = "SKY_A01";
        String t2 = "SKY_A14";

        System.out.println(Integer.parseInt(t1.substring(t1.length()-2)));
    }
}