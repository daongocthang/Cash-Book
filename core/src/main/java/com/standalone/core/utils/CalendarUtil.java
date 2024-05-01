package com.standalone.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarUtil {

    public static Date parseTime(String fmt, String str) {
        SimpleDateFormat sdf = new SimpleDateFormat(fmt, Locale.US);
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toString(String fmt, Date dt) {
        SimpleDateFormat sdf = new SimpleDateFormat(fmt, Locale.US);
        return sdf.format(dt);
    }

    public static Date now() {
        Calendar cal = Calendar.getInstance();
        return cal.getTime();
    }

    public static Date nextMonth(Date dt) {
        Calendar cal = Calendar.getInstance();
        if (dt != null) cal.setTime(dt);

        cal.add(Calendar.MONTH, 1);
        return cal.getTime();
    }

    public static int getLastDayOfMonth() {
        Calendar cal = Calendar.getInstance();
        return cal.getActualMaximum(Calendar.DATE);
    }

    public static int get(Date dt, int calendarField) {
        Calendar cal = Calendar.getInstance();
        if (dt != null) cal.setTime(dt);
        return cal.get(calendarField);
    }

    public static int get(int calendarField) {
        return get(null, calendarField);
    }

    public static Date nextMonth() {
        return nextMonth(null);
    }

    private static Calendar setTime(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        return cal;
    }
}