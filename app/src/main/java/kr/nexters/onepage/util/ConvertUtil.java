package kr.nexters.onepage.util;

import java.text.DecimalFormat;

/**
 * Created by OhJaeHwan on 2017-01-30.
 */

public class ConvertUtil {

    public static String integerToCommaString(int number) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(number);
    }
}
