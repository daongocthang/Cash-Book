package com.standalone.cashbook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.standalone.cashbook.databinding.ActivitySignInBinding;
import com.standalone.core.dialogs.ProgressDialog;
import com.standalone.core.utils.DialogUtil;
import com.standalone.core.utils.ValidationManager;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {
    static final String MSG_LOGIN_FAILURE = "Email hoặc mật khẩu không đúng.";
    ActivitySignInBinding binding;
    ValidationManager manager = ValidationManager.getInstance();
    FirebaseAuth auth;

    final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        auth = FirebaseAuth.getInstance();

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                manager.refresh();
                manager.doValidation(binding.tilEmail).checkEmpty().checkEmail();
                manager.doValidation(binding.tilPassword).checkEmpty();

                if (manager.isAllValid()) {
                    onSubmit();
                }
            }
        });

        binding.btnRegisterHere.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
                        startActivity(intent);
                    }
                }
        );

        binding.btnForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, ResetPasswordActivity.class));
            }
        });
    }

    void onSubmit() {
        String email = Objects.requireNonNull(binding.edtEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(binding.edtPassword.getText()).toString().trim();
        final ProgressDialog progressDialog = DialogUtil.showProgressDialog(this);
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:success");
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    progressDialog.dismiss();
                    finish();
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    DialogUtil.showAlertDialog(SignInActivity.this, MSG_LOGIN_FAILURE);
                }
            }
        });
    }
}
