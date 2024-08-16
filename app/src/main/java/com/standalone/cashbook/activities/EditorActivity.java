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
import com.standalone.core.utils.DialogUtil;
import com.standalone.core.utils.ValidationManager;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class EditorActivity extends AppCompatActivity {
    ActivityEditorBinding binding;
    ValidationManager manager;
    FireStoreHelper<PayableModel> helper;
    PayableModel model;
    String key;

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
                key = model.getKey();
                binding.edtTitle.setText(model.getTitle());
                binding.edtAmount.setText(String.format(Locale.US, "%,d", model.getAmount()));
                binding.chkPaid.setChecked(model.isPaid());
            }
        }

        binding.autocomplete.setThreshold(2);
        binding.autocomplete.setSize(4);
        binding.autocomplete.attachEditText(binding.edtAmount, binding.getRoot());

        binding.chkPaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = binding.chkPaid.isChecked();
                binding.chkPaid.setChecked(!isChecked);
            }
        });

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.refresh();
                manager.doValidation(binding.tilTitle).checkEmpty();
                manager.doValidation(binding.tilAmount).checkEmpty();
                if (manager.isAllValid())
                    onSubmit();
            }
        });
    }

    private void onSubmit() {
        String title = Objects.requireNonNull(binding.edtTitle.getText()).toString().trim();
        String amount = Objects.requireNonNull(binding.edtAmount.getText()).toString().trim().replace(",", "");

        long longAmount = Long.parseLong(amount);

        PayableModel model = new PayableModel();
        model.setTitle(title);
        model.setAmount(longAmount);
        model.setPaid(binding.chkPaid.isChecked());
        Task<Void> task;
        if (TextUtils.isEmpty(key)) {
            // Create
            model.setKey(UUID.randomUUID().toString());
            task = helper.create(model);
        } else {
            // Update
            task = helper.update(key, model);
        }

        ProgressDialog progressDialog = DialogUtil.showProgressDialog(this);
        task.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                startActivity(new Intent(EditorActivity.this, HomeActivity.class));
                finish();
            }
        });
    }
}