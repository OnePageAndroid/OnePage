package kr.nexters.onepage.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.text.DecimalFormat;
import java.util.Calendar;

import kr.nexters.onepage.R;

/**
 * Created by OhJaeHwan on 2017-01-30.
 */

public class ConvertUtil {

    public static String integerToCommaString(int number) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(number);
    }

    public static int findResouceIdByWeatherCode(String code) {
        switch (Integer.parseInt(code.substring(code.length()-2))) {
            case 3:
            case 7: {
                return R.raw.cloud;
            }
            case 4:
            case 6:
            case 8:
            case 10:
            case 11:
            case 12:
            case 14: {
                return R.raw.rain;
            }
            case 5:
            case 9:
            case 13: {
                return R.raw.snow;
            }
            default: {
                return -1;
            }
        }
    }

    public static String getDayTime() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
//        if(6 <= hour && hour < 18) {
//            return "MORNING";
//        }
        return "NIGHT";
    }

    public static int dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
}
