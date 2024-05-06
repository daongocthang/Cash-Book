package com.standalone.cashbook.activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.standalone.cashbook.R;
import com.standalone.cashbook.adapters.PayableAdapter;
import com.standalone.cashbook.controllers.FireStoreHelper;
import com.standalone.cashbook.databinding.ActivityMainBinding;
import com.standalone.cashbook.models.PayableModel;
import com.standalone.cashbook.receivers.AlarmInfo;
import com.standalone.cashbook.receivers.AlarmReceiver;
import com.standalone.core.adapters.RecyclerItemTouchHelper;
import com.standalone.core.dialogs.ProgressDialog;
import com.standalone.core.services.AlarmScheduler;
import com.standalone.core.tools.SmsReader;
import com.standalone.core.utils.DialogUtil;
import com.standalone.core.utils.NotificationUtil;
import com.standalone.core.utils.StorageUtil;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements SmsReader.ValueEventListener {
    static final String TAG = MainActivity.class.getSimpleName();
    static final String SMS_PATTERN = "So du ([0-9\\.]+) VND";
    static final String SMS_ADDRESS = "VTMONEY";


    ActivityMainBinding binding;
    PayableAdapter adapter;
    SmsReader reader;
    Pattern pattern;
    FirebaseAuth auth;
    FireStoreHelper<PayableModel> helper;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }
        pattern = Pattern.compile(SMS_PATTERN);

        // Require permission
        StorageUtil.requirePermission(this);
        reader = new SmsReader(this);
        requireReadSmsPermission();
        scheduleAlarm();

        // Declare Notification
        NotificationUtil.createChannel(this, AlarmInfo.CHANNEL_ID, "Default Channel");

        adapter = new PayableAdapter(this);
        binding.recycler.setAdapter(adapter);

        helper = new FireStoreHelper<>(AlarmInfo.COLLECTION_ID);

        progressDialog = DialogUtil.showProgressDialog(this);
        helper.fetch(PayableModel.class, new FireStoreHelper.OnFetchCompleteListener<PayableModel>() {
            @Override
            public void onFetchComplete(ArrayList<PayableModel> data) {
                adapter.setItemList(data);
                binding.liabilitiesTV.setText(String.format(Locale.US, "%,d", adapter.getTotalAmount()));
                progressDialog.dismiss();
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(this) {
            @Override
            public void onSwipeLeft(int position) {
                adapter.notifyItemChanged(position);
                adapter.editItem(position);
            }

            @Override
            public void onSwipeRight(int position) {
                progressDialog = DialogUtil.showProgressDialog(MainActivity.this);
                new MaterialAlertDialogBuilder(MainActivity.this)
                        .setMessage(getString(R.string.alert_msg_delete))
                        .setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                PayableModel model = adapter.removeItem(position);
                                helper.remove(model.getKey()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        binding.liabilitiesTV.setText(String.format(Locale.US, "%,d", adapter.getTotalAmount()));
                                        progressDialog.dismiss();
                                    }
                                });
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                adapter.notifyItemChanged(position);
                                progressDialog.dismiss();
                                dialogInterface.dismiss();
                            }
                        }).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sm_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, SignInActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void requireReadSmsPermission() {
        if (reader.checkReadSmsPermission() == PackageManager.PERMISSION_GRANTED) {
            reader.read(this);
        } else {
            reader.requestReadSmsPermission(this);
        }
    }

    private void scheduleAlarm() {
        AlarmScheduler scheduler = AlarmScheduler.from(this);
        if (scheduler.isWorking(AlarmInfo.REQUEST_CODE_RECEIVER, AlarmReceiver.class)) {
            return;
        }

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