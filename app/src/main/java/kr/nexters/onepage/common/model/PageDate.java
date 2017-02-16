package kr.nexters.onepage.common.model;

import java.util.Locale;

import lombok.Data;

/**
 * Created by ohjaehwan on 2017. 2. 13..
 */
@Data
public class PageDate {
    private int year;
    private int monthValue;
    private int dayOfMonth;
    private int hour;
    private int minute;

    public String getDateString() {
        return String.format(Locale.KOREA, "%d월 %d일 %02d:%02d", monthValue, dayOfMonth, hour, minute);
    }
}
