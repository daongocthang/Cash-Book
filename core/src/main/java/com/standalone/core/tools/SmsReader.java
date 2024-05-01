package com.standalone.core.tools;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;

public class SmsReader {
    public static final int READ_SMS_PERMISSION_CODE = 1;

    final Context context;


    public SmsReader(Context context) {
        this.context = context;
    }

    public void read(ValueEventListener listener) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(Telephony.Sms.CONTENT_URI, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                String body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
                Map<String, String> snapshot = new HashMap<>();
                snapshot.put("address", address);
                snapshot.put("body", body);

                if (listener.onDataChange(snapshot)) break;

            } while (cursor.moveToNext());
        }

        if (cursor != null) cursor.close();
    }


    public int checkReadSmsPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS);
    }

    public void requestReadSmsPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_SMS}, READ_SMS_PERMISSION_CODE);
    }

    public interface ValueEventListener {

        /**
         * Access data from a cursor. Stopping if met the condition.
         */
        boolean onDataChange(Map<String, String> sms);

    }
}
