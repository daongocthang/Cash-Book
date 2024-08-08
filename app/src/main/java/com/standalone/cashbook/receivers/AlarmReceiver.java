package com.standalone.cashbook.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.standalone.cashbook.controllers.FireStoreHelper;
import com.standalone.cashbook.models.PayableModel;
import com.standalone.core.utils.LogUtil;

import java.util.ArrayList;
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
            if (model.getUpdatedAt().before(new Date())) {
                model.setPaid(false);
                helper.update(model.getKey(), model);
            }
        }
    }
}
