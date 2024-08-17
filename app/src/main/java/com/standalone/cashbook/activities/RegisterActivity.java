package com.standalone.cashbook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.standalone.cashbook.R;
import com.standalone.cashbook.databinding.ActivityRegisterBinding;
import com.standalone.core.dialogs.ProgressDialog;
import com.standalone.core.utils.DialogUtil;
import com.standalone.core.utils.ValidationManager;

import java.util.Objects;


public class RegisterActivity extends AppCompatActivity {
    static final String MSG_SIGNUP_SUCCESS = "Bạn đã đăng ký thành công.";
    static final String MSG_SIGNUP_FAILURE = "Email đã được sử dụng.";
    ActivityRegisterBinding binding;
    ValidationManager manager;
    FirebaseAuth auth;

    final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;

        actionBar.setTitle(R.string.register);


        manager = ValidationManager.getInstance();
        auth = FirebaseAuth.getInstance();

        binding.btnLoginHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        binding.btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.refresh();

                manager.doValidation(binding.tilEmail).checkEmpty().checkEmail();
                manager.doValidation(binding.tilPassword).checkEmpty().checkPassword();
                manager.doValidation(binding.tilConfirmPassword).checkEmpty().matchPassword(binding.tilPassword);

                if (manager.isAllValid()) {
                    onSubmit();
                }
            }
        });
    }

    void onSubmit() {
        String email = Objects.requireNonNull(binding.edtEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(binding.edtPassword.getText()).toString().trim();

        ProgressDialog progressDialog = DialogUtil.showProgressDialog(this);
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEMail:success");
                    DialogUtil.showAlertDialog(RegisterActivity.this, MSG_SIGNUP_SUCCESS);
                    binding.edtEmail.setText("");
                    binding.edtPassword.setText("");
                    binding.edtConfirmPassword.setText("");
                } else {
                    Log.w(TAG, "createUserWithEMail:failure", task.getException());
                    DialogUtil.showAlertDialog(RegisterActivity.this, MSG_SIGNUP_FAILURE);
                }
            }
        });
    }


}
