package com.mhxks.zjy.time;

import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
    public static Date date = new Date();
    public static Calendar calendar = Calendar.getInstance();
    public static int getWeekday(){
        calendar.setTime(date);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        return weekDay;
    }
}
