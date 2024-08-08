package com.standalone.cashbook.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.standalone.cashbook.controllers.FireStoreHelper;
import com.standalone.cashbook.models.PayableModel;
import com.standalone.core.utils.DateTimeUtil;
import com.standalone.core.utils.LogUtil;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    FireStoreHelper<PayableModel> helper;

    @Override
    public void onReceive(Context context, Intent intent) {
        helper = new FireStoreHelper<>(AlarmInfo.COLLECTION_ID);
        helper.fetch(PayableModel.class, new FireStoreHelper.OnFetchCompleteListener<PayableModel>() {
            @Override
            public void onFetchComplete(ArrayList<PayableModel> data) {
                try {
                    doWork(context, data);
                    LogUtil.write(context, "Receiver completed");
                } catch (Exception e) {
                    LogUtil.write(context, e.getMessage());
                }
            }
        });
    }

    private void doWork(Context context, List<PayableModel> payableList) {
        for (PayableModel model : payableList) {
            if (!model.isPaid()) continue;
            int monthDiff = countMothsOfYear(new Date()) - countMothsOfYear(model.getUpdatedAt());
            if (monthDiff != 0) {
                model.setPaid(false);
                helper.update(model.getKey(), model);
            }
        }
    }

    private int countMothsOfYear(Date dt) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        return 12 * calendar.get(Calendar.YEAR) + calendar.get(Calendar.MONTH);
    }
}
