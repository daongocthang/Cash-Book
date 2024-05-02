package com.standalone.cashbook.receivers;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.standalone.core.services.AlarmScheduler;

public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            AlarmScheduler.from(context)
                    .setAlarm(AlarmInfo.REQUEST_CODE_RECEIVER,
                            AlarmInfo.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY,
                            AlarmReceiver.class);
        }
    }
}
