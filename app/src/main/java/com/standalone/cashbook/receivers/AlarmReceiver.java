package com.standalone.cashbook.receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.standalone.cashbook.R;
import com.standalone.cashbook.activities.MainActivity;
import com.standalone.cashbook.controllers.SqliteHelper;
import com.standalone.cashbook.models.PayableModel;
import com.standalone.core.utils.CalendarUtil;
import com.standalone.core.utils.NotificationUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SqliteHelper helper = new SqliteHelper(context);
        List<PayableModel> payableList = helper.fetchAll();
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        int sum = 0;
        for (PayableModel model : payableList) {
            Date date = CalendarUtil.parseTime(AlarmInfo.DATE_PATTERN, model.getDate());
            Calendar dateOfPayment = Calendar.getInstance();
            dateOfPayment.setTime(date);
            if (today.after(dateOfPayment)) {
                if (model.getPaid() > 0) {
                    model.setPaid(0);
                    dateOfPayment.add(Calendar.MONTH, 1);
                    model.setDate(CalendarUtil.toString(AlarmInfo.DATE_PATTERN, dateOfPayment.getTime()));
                } else {
                    sum += model.getAmount();
                }
            }
        }


        if (sum > 0) {
            Intent specifiedIntent = new Intent(context, MainActivity.class);
            int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
            PendingIntent pendingActivity = PendingIntent.getActivity(context, AlarmInfo.REQUEST_CODE_ACTIVITY, specifiedIntent, flags);
            NotificationUtil.post(context,
                    AlarmInfo.CHANNEL_ID,
                    R.drawable.ic_launcher_background,
                    context.getString(R.string.notification_title),
                    context.getString(R.string.notification_msg) + String.format(Locale.US, "%,d VND", sum),
                    pendingActivity
            );
        }
    }
}
