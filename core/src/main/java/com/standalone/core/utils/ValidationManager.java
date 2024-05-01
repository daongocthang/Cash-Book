package com.standalone.core.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationManager {
    @SuppressLint("StaticFieldLeak")
    static ValidationManager instance;
    final String EMAIL_PATTERN = "[a-z0-9A-Z._-]+@[a-z]+\\.[a-z]+";
    final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=\\S+$).{6,20}$";

    static final String ERR_MSG_CHECK_EMPTY = "Vui lòng không để trống.";
    static final String ERR_MSG_CHECK_EMAIL = "Email không hợp lệ.";
    static final String ERR_MSG_MATCH_PASSWORD = "Mật khẩu nhập lại không khớp";
    static final String ERR_MSG_CHECK_PASSWORD = "Mật khẩu có độ dài từ 6-20 ký tự bao gồm chữ cái và số";

    TextInputLayout textInputLayout;
    EditText editText;
    Pattern pattern;
    Matcher matcher;

    boolean isEmpty;

    ArrayList<TextInputLayout> invalidList = new ArrayList<>();

    public static ValidationManager getInstance() {
        if (instance == null) instance = new ValidationManager();
        return instance;
    }

    public ValidationManager doValidation(TextInputLayout textInputLayout) {
        this.textInputLayout = textInputLayout;
        this.editText = textInputLayout.getEditText();
        this.isEmpty = false;
        return instance;
    }

    public ValidationManager checkEmpty() {
        isEmpty = editText.getText().toString().trim().isEmpty();

        if (isEmpty) {
            textInputLayout.setError(ERR_MSG_CHECK_EMPTY);
            appendToInvalidListIfNotExists();
        }

        return instance;
    }

    public ValidationManager checkEmail() {
        boolean isEmailValid = editText.getText().toString().trim().matches(EMAIL_PATTERN);
        if (!isEmpty && !isEmailValid) {
            textInputLayout.setError(ERR_MSG_CHECK_EMAIL);
            appendToInvalidListIfNotExists();
        }

        return instance;
    }

    public ValidationManager matchPassword(TextInputLayout password) {
        String passwordString = Objects.requireNonNull(password.getEditText()).getText().toString().trim();
        boolean hasMatched = editText.getText().toString().trim().equals(passwordString) && !passwordString.isEmpty();
        if (!isEmpty && !hasMatched) {
            textInputLayout.setError(ERR_MSG_MATCH_PASSWORD);
            appendToInvalidListIfNotExists();
        }

        return instance;
    }

    public ValidationManager checkPassword() {
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(editText.getText().toString().trim());
        boolean isPasswordValid = matcher.matches();

        if (!isEmpty && !isPasswordValid) {
            textInputLayout.setError(ERR_MSG_CHECK_PASSWORD);
            invalidList.add(textInputLayout);
            appendToInvalidListIfNotExists();
        }

        return instance;
    }

    public boolean isAllValid() {
        return (invalidList.size() == 0);
    }

    public void refresh() {
        isEmpty = false;
        if (invalidList.size() == 0) return;

        for (TextInputLayout child : invalidList) {
            child.setError(null);
        }

        invalidList.clear();
    }

    private void appendToInvalidListIfNotExists() {
        if (!invalidList.contains(textInputLayout)) {
            invalidList.add(textInputLayout);
        }
    }
}
