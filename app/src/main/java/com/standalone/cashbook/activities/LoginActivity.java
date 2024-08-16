package com.standalone.cashbook.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.standalone.cashbook.R;
import com.standalone.cashbook.databinding.ActivityLoginBinding;
import com.standalone.cashbook.utils.BiometricPromptUtil;
import com.standalone.core.dialogs.ProgressDialog;
import com.standalone.core.utils.DialogUtil;
import com.standalone.core.utils.ValidationManager;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    ValidationManager manager = ValidationManager.getInstance();
    FirebaseAuth auth;

    SharedPreferences sharedPreferences;

    final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        auth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                manager.refresh();
                manager.doValidation(binding.tilEmail).checkEmpty().checkEmail();
                manager.doValidation(binding.tilPassword).checkEmpty();

                if (manager.isAllValid()) {
                    String email = Objects.requireNonNull(binding.edtEmail.getText()).toString().trim();
                    String password = Objects.requireNonNull(binding.edtPassword.getText()).toString().trim();

                    performAuth(email, password);
                }
            }
        });

        binding.btnRegisterHere.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                        startActivity(intent);
                    }
                }
        );

        binding.btnForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        boolean isLogin = sharedPreferences.getBoolean("isLogin", false);
        if (isLogin) showBiometricPrompt();
    }

    void performAuth(@NonNull String email, @NonNull String password) {
        final ProgressDialog progressDialog = DialogUtil.showProgressDialog(this);
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:success");

                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    editor.putString("email", email);
                    editor.putString("password", password);
                    editor.putBoolean("isLogin", true);
                    editor.apply();

                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    DialogUtil.showAlertDialog(LoginActivity.this, getString(R.string.msg_login_failure));
                }
                progressDialog.dismiss();
            }
        });
    }

    void showBiometricPrompt() {
        BiometricPrompt biometricPrompt = BiometricPromptUtil.createBiometricPrompt(this, new BiometricPromptUtil.AuthenticationProcessor() {
            @Override
            public void onSuccess(BiometricPrompt.AuthenticationResult result) {
                String email = sharedPreferences.getString("email", "");
                String password = sharedPreferences.getString("password", "");

                performAuth(email, password);
            }
        });

        BiometricPrompt.PromptInfo promptInfo = BiometricPromptUtil.createPromptInfo(this);
        biometricPrompt.authenticate(promptInfo);
    }
}
