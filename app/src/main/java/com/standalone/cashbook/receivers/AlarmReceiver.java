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

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SqliteHelper helper = new SqliteHelper(context);
        List<PayableModel> payableList = helper.fetchAll();
        Calendar calendar = Calendar.getInstance();

        int sum = 0;
        for (PayableModel model : payableList) {
            if (calendar.get(Calendar.DATE) == 1) {
                model.setPaid(0);
                helper.update(model);
            }
            Date date = CalendarUtil.parseTime(AlarmInfo.DATE_PATTERN, model.getDate());
            if (CalendarUtil.isToday(date) && model.getPaid() == 0) {
                sum += model.getAmount();
            }
        }


        if (sum > 0) {
            Intent specifiedIntent = new Intent(context, MainActivity.class);
            int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
            PendingIntent pendingActivity = PendingIntent.getActivity(context, AlarmInfo.REQUEST_CODE_ACTIVITY, specifiedIntent, flags);
            NotificationUtil.post(context,
                    AlarmInfo.CHANNEL_ID,
                    R.drawable.ic_launcher_background,
                    "Thông báo",
                    String.format("Tổng số tiền cần phải thanh toán là : %s VND", sum),
                    pendingActivity
            );
        }
    }
}
