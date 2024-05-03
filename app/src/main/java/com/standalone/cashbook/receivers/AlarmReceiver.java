package com.standalone.cashbook.receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.standalone.cashbook.R;
import com.standalone.cashbook.activities.MainActivity;
import com.standalone.cashbook.controllers.FireStoreHelper;
import com.standalone.cashbook.models.PayableModel;
import com.standalone.core.utils.DateTimeUtil;
import com.standalone.core.utils.NotificationUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {
    FireStoreHelper<PayableModel> helper;

    @Override
    public void onReceive(Context context, Intent intent) {
        helper = new FireStoreHelper<>(AlarmInfo.COLLECTION_ID);
        helper.fetch(PayableModel.class, new FireStoreHelper.OnFetchCompleteListener<PayableModel>() {
            @Override
            public void onFetchComplete(ArrayList<PayableModel> data) {
                doWork(context, data);
            }
        });
    }

    private void doWork(Context context, List<PayableModel> payableList) {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        int sum = 0;
        for (PayableModel model : payableList) {
            Date date = DateTimeUtil.parseTime(AlarmInfo.DATE_PATTERN, model.getDate());
            Calendar dateOfPayment = Calendar.getInstance();
            dateOfPayment.setTime(date);
            if (today.get(Calendar.DATE) == 1 && model.isPaid()) {
                model.setPaid(false);
                dateOfPayment.add(Calendar.MONTH, 1);
                model.setDate(DateTimeUtil.toString(AlarmInfo.DATE_PATTERN, dateOfPayment.getTime()));
                helper.update(model.getKey(), model);
            }

            if (today.after(dateOfPayment) && !model.isPaid()) {
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
                    context.getString(R.string.notification_title),
                    context.getString(R.string.notification_msg) + String.format(Locale.US, "%,d VND", sum),
                    pendingActivity
            );
        }
    }
}
