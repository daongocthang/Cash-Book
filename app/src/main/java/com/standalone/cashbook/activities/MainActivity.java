package com.standalone.cashbook.activities;

import android.app.AlarmManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.standalone.cashbook.adapters.PayableAdapter;
import com.standalone.cashbook.controllers.SqliteHelper;
import com.standalone.cashbook.databinding.ActivityMainBinding;
import com.standalone.cashbook.models.PayableModel;
import com.standalone.cashbook.receivers.AlarmInfo;
import com.standalone.cashbook.receivers.AlarmReceiver;
import com.standalone.core.adapters.RecyclerItemTouchHelper;
import com.standalone.core.services.AlarmScheduler;
import com.standalone.core.tools.SmsReader;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements SmsReader.ValueEventListener {
    static final String TAG = MainActivity.class.getSimpleName();
    static final String SMS_PATTERN = "So du ([0-9\\.]+) VND";
    static final String SMS_ADDRESS = "VTMONEY";
    static final int JOB_ID = 123;

    ActivityMainBinding binding;
    PayableAdapter adapter;
    SmsReader reader;
    Pattern pattern;
    SqliteHelper sqliteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sqliteHelper = new SqliteHelper(this);
        reader = new SmsReader(this);
        pattern = Pattern.compile(SMS_PATTERN);

        invokeSmsReader();
        scheduleAlarm();

        adapter = new PayableAdapter(this);
        adapter.setItemList(sqliteHelper.fetchAll());
        binding.recycler.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(this) {
            @Override
            public void onSwipeLeft(int position) {
                adapter.editItem(position);
            }

            @Override
            public void onSwipeRight(int position) {
                PayableModel model = adapter.removeItem(position);
                sqliteHelper.remove(model.getId());

                binding.liabilitiesTV.setText(String.format(Locale.US, "%,d", adapter.getTotalAmount()));
            }
        });
        itemTouchHelper.attachToRecyclerView(binding.recycler);

        binding.liabilitiesTV.setText(String.format(Locale.US, "%,d", adapter.getTotalAmount()));
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, EditorActivity.class));
            }
        });
    }

    private void invokeSmsReader() {
        if (reader.checkReadSmsPermission() == PackageManager.PERMISSION_GRANTED) {
            reader.read(this);
        } else {
            reader.requestReadSmsPermission(this);
        }
    }

    private void scheduleAlarm() {
        AlarmScheduler scheduler = AlarmScheduler.from(this);
        scheduler.setAlarm(AlarmInfo.REQUEST_CODE_RECEIVER,
                AlarmInfo.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                AlarmReceiver.class
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SmsReader.READ_SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                reader.read(this);
            }
        }
    }

    @Override
    public boolean onDataChange(Map<String, String> sms) {
        String body = sms.get("body");
        if (Objects.equals(sms.get("address"), SMS_ADDRESS) && !TextUtils.isEmpty(body)) {
            Matcher matcher = pattern.matcher(body);
            if (matcher.find()) {
                String substring = matcher.group(1);
                if (!TextUtils.isEmpty(substring)) {
                    binding.balanceTV.setText(substring.replace(".", ","));
                    return true;
                }
            }
        }
        return false;
    }
}