package com.standalone.cashbook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.standalone.cashbook.controllers.FireStoreHelper;
import com.standalone.cashbook.databinding.ActivityEditorBinding;
import com.standalone.cashbook.models.PayableModel;
import com.standalone.cashbook.receivers.AlarmInfo;
import com.standalone.core.dialogs.ProgressDialog;
import com.standalone.core.utils.DateTimeUtil;
import com.standalone.core.utils.DialogUtil;
import com.standalone.core.utils.PickerUtil;
import com.standalone.core.utils.ValidationManager;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class EditorActivity extends AppCompatActivity {
    ActivityEditorBinding binding;
    ValidationManager manager;
    FireStoreHelper<PayableModel> helper;
    PayableModel model;
    String keyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        manager = ValidationManager.getInstance();
        helper = new FireStoreHelper<>(AlarmInfo.COLLECTION_ID);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        if (bundle != null) {
            model = (PayableModel) bundle.getSerializable("payment");
            if (model != null) {
                keyRef = model.getKey();
                binding.edtTitle.setText(model.getTitle());
                binding.edtAmount.setText(String.format(Locale.US, "%,d", model.getAmount()));
                binding.tvDateOfPayment.setText(model.getDate());
                binding.chkPaid.setChecked(model.isPaid());
            }
        }

        binding.autocomplete.attachEditText(binding.edtAmount);

        if (TextUtils.isEmpty(keyRef)) {
            String dateStr = DateTimeUtil.toString(AlarmInfo.DATE_PATTERN, DateTimeUtil.now());
            binding.tvDateOfPayment.setText(dateStr);
        }

        binding.btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickerUtil.from(EditorActivity.this)
                        .setPatternDate(AlarmInfo.DATE_PATTERN)
                        .showDatePicker("Select date of payment", binding.tvDateOfPayment);
                binding.autocomplete.leave();
            }
        });

        binding.chkPaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = binding.chkPaid.isChecked();
                binding.chkPaid.setChecked(!isChecked);
                binding.autocomplete.leave();
            }
        });

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.doValidation(binding.tilTitle).checkEmpty();
                manager.doValidation(binding.tilAmount).checkEmpty();
                if (manager.isAllValid()) onSubmit();
            }
        });
    }

    private void onSubmit() {
        String title = Objects.requireNonNull(binding.edtTitle.getText()).toString().trim();
        String amount = Objects.requireNonNull(binding.edtAmount.getText()).toString().trim().replace(",", "");
        String dateStr = binding.tvDateOfPayment.getText().toString();

        long longAmount = Long.parseLong(amount);

        PayableModel model = new PayableModel();
        model.setTitle(title);
        model.setAmount(longAmount);
        model.setDate(dateStr);
        model.setPaid(binding.chkPaid.isChecked());
        Task<Void> task;
        if (TextUtils.isEmpty(keyRef)) {
            model.setKey(UUID.randomUUID().toString());
            model.setNextPay(0);
            task = helper.create(model);
        } else {
            task = helper.update(keyRef, model);
        }

        ProgressDialog progressDialog = DialogUtil.showProgressDialog(this);
        task.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                startActivity(new Intent(EditorActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}