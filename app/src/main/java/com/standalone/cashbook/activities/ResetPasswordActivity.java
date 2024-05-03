package com.standalone.cashbook.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.standalone.cashbook.R;
import com.standalone.cashbook.databinding.ActivityResetPasswordBinding;
import com.standalone.core.dialogs.ProgressDialog;
import com.standalone.core.utils.DialogUtil;
import com.standalone.core.utils.ValidationManager;


import java.util.Objects;

public class ResetPasswordActivity extends AppCompatActivity {
    static final String MSG_SEND_MAIL_SUCCESS = "Hệ thống đã gửi đường liên kết đến %s. Xin vui lòng kiểm tra Email.";
    final String TAG = getClass().getCanonicalName();

    ActivityResetPasswordBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;

        actionBar.setTitle(R.string.reset_password);

        auth = FirebaseAuth.getInstance();
        auth.setLanguageCode("vi");
        final ValidationManager validator = ValidationManager.getInstance();
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.refresh();
                validator.doValidation(binding.tilEmail).checkEmpty().checkEmail();
                if (validator.isAllValid()) {
                    onSubmit();
                }
            }
        });
    }

    private void onSubmit() {
        final String email = Objects.requireNonNull(binding.edtEmail.getText()).toString().trim();
        final ProgressDialog progress = DialogUtil.showProgressDialog(this);

        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progress.dismiss();
                if (task.isSuccessful()) {
                    DialogUtil.showAlertDialog(ResetPasswordActivity.this, String.format(MSG_SEND_MAIL_SUCCESS, email));
                } else {
                    Log.w(TAG, "Something wrong" + task.getException());
                }
            }
        });
    }
}