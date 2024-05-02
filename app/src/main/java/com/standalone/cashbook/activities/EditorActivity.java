package com.standalone.cashbook.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckedTextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.standalone.cashbook.R;
import com.standalone.cashbook.controllers.SqliteHelper;
import com.standalone.cashbook.databinding.ActivityEditorBinding;
import com.standalone.cashbook.models.PayableModel;
import com.standalone.cashbook.receivers.AlarmInfo;
import com.standalone.core.components.DecimalKeyboard;
import com.standalone.core.utils.CalendarUtil;
import com.standalone.core.utils.PickerUtil;
import com.standalone.core.utils.ValidationManager;

import java.util.Locale;
import java.util.Objects;

public class EditorActivity extends AppCompatActivity {
    ActivityEditorBinding binding;
    ValidationManager manager;
    SqliteHelper helper;
    PayableModel model;
    int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        manager = ValidationManager.getInstance();
        helper = new SqliteHelper(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        if (bundle != null) {
            model = (PayableModel) bundle.getSerializable("payment");
            if (model != null) {
                position = model.getId();
                binding.titleET.setText(model.getTitle());
                binding.amountET.setText(String.format(Locale.US, "%,d", model.getAmount()));
                binding.calendarTV.setText(model.getDate());
                binding.chkPaid.setChecked(model.getPaid() > 0);
            }
        }

        if (position < 0) {
            String dateStr = CalendarUtil.toString(AlarmInfo.DATE_PATTERN, CalendarUtil.now());
            binding.calendarTV.setText(dateStr);
        }

        binding.amountET.setShowSoftInputOnFocus(false);
        binding.amountET.setRawInputType(InputType.TYPE_CLASS_TEXT);

        InputConnection ic = binding.amountET.onCreateInputConnection(new EditorInfo());
        binding.keyboard.setInputConnection(ic);

        binding.keyboard.setOnAcceptListener(new DecimalKeyboard.OnActionDoneListener() {
            @Override
            public void onActionDone(InputConnection inputConnection) {
                binding.keyboard.setVisibility(View.INVISIBLE);
            }
        });

        binding.amountET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    binding.keyboard.setVisibility(View.VISIBLE);
                } else {
                    binding.keyboard.setVisibility(View.INVISIBLE);
                }
            }
        });

        binding.calendarIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickerUtil.from(EditorActivity.this)
                        .setPatternDate(AlarmInfo.DATE_PATTERN)
                        .showDatePicker("Select date of payment", binding.calendarTV);
            }
        });

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
                manager.doValidation(binding.titleTIL).checkEmpty();
                manager.doValidation(binding.amountTIL).checkEmpty();
                if (manager.isAllValid()) onSubmit();
            }
        });
    }

    private void onSubmit() {
        String title = Objects.requireNonNull(binding.titleET.getText()).toString().trim();
        String amount = Objects.requireNonNull(binding.amountET.getText()).toString().trim().replace(",", "");
        String dateStr = binding.calendarTV.getText().toString();

        int amountInt = Integer.parseInt(amount);

        PayableModel model = new PayableModel();
        model.setTitle(title);
        model.setAmount(amountInt);
        model.setDate(dateStr);
        model.setPaid(binding.chkPaid.isChecked() ? 1 : 0);

        if (position < 0) {
            helper.insert(model);
        } else {
            model.setId(position);
            helper.update(model);
        }

        startActivity(new Intent(this, MainActivity.class));
    }
}