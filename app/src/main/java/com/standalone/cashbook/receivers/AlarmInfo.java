package com.standalone.cashbook.receivers;

import java.util.Calendar;

public class AlarmInfo {
    public static final int REQUEST_CODE_RECEIVER = 0;
    public static final int REQUEST_CODE_ACTIVITY = 1;
    public static final int ALARM_HOUR = 8;
    public static final int ALARM_MINUTE = 0;

    public static String CHANNEL_ID = "cashbook_notification_default_channel";

    public static String COLLECTION_ID = "liabilities";

    public static long getTimeInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, ALARM_HOUR);
        calendar.set(Calendar.MINUTE, ALARM_MINUTE);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTimeInMillis();
    }
}
