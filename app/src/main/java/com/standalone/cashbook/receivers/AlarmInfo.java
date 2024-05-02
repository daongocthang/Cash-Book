package com.standalone.cashbook.receivers;

import java.util.Calendar;
import java.util.UUID;

public class AlarmInfo {
    public static final int REQUEST_CODE_RECEIVER = 0;
    public static final int REQUEST_CODE_ACTIVITY = 1;
    public static final int ALARM_HOUR = 8;
    public static final int ALARM_MINUTE = 0;
    public static String DATE_PATTERN = "dd-MM-yyyy";
    public static String CHANNEL_ID = "notification_cashbook";

    public static long getTimeInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, ALARM_HOUR);
        calendar.set(Calendar.MINUTE, ALARM_MINUTE);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTimeInMillis();
    }
}
