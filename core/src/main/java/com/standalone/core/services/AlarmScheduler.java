package com.standalone.core.services;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmScheduler {
    @SuppressLint("StaticFieldLeak")
    static AlarmScheduler instance;
    Context context;
    AlarmManager alarmManager;

    private AlarmScheduler() {
    }

    public static AlarmScheduler from(Context context) {
        if (instance == null) {
            instance = new AlarmScheduler();
        }
        instance.context = context;
        instance.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        return instance;
    }

    /**
     * Returns an existing or new PendingIntent matching the given parameters.
     * May return null only if FLAG_NO_CREATE has been supplied.
     */
    PendingIntent getBroadcast(int requestCode, Class<? extends BroadcastReceiver> cls) {
        Intent intent = new Intent(context, cls);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        return PendingIntent.getBroadcast(context, requestCode, intent, flags);
    }


    public void setAlarm(int requestCode, long triggerAtMillis, long intervalMillis, Class<? extends BroadcastReceiver> cls) {
        PendingIntent pendingIntent = getBroadcast(requestCode, cls);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, intervalMillis, pendingIntent);
    }

    public void cancelAlarm(int requestCode, Class<? extends BroadcastReceiver> cls) {
        PendingIntent pendingIntent = getBroadcast(requestCode, cls);
        alarmManager.cancel(pendingIntent);
    }
}
