package com.standalone.cashbook.receivers;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.standalone.core.services.AlarmScheduler;
import com.standalone.core.utils.LogUtil;

/**
 * Requirement:
 * <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
 */
public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.write(context, "Device boot completed.");
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            AlarmScheduler.from(context)
                    .setAlarm(AlarmInfo.REQUEST_CODE_RECEIVER,
                            AlarmInfo.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY,
                            AlarmReceiver.class);
        }
    }
}
