package com.standalone.core.utils;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PickerUtil {
    static PickerUtil instance;
    String pattern;
    private PickerUtil() {
    }

    AppCompatActivity activity;

    public static PickerUtil from(AppCompatActivity activity) {
        if (instance == null) instance = new PickerUtil();

        instance.activity = activity;
        return instance;
    }

    public PickerUtil setPatternDate(String pattern) {
        this.pattern = pattern;
        return this;
    }

    public void showDatePicker(String title, TextView textView) {
        Calendar calCurrent = Calendar.getInstance();
        calCurrent.setTime(DateTimeUtil.parseTime(pattern, textView.getText().toString()));
        calCurrent.add(Calendar.DAY_OF_YEAR, 1);

        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(title)
                .setSelection(calCurrent.getTimeInMillis())
                .build();
        picker.show(activity.getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                Calendar calSelection = Calendar.getInstance();
                calSelection.setTimeInMillis(selection);
                SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
                textView.setText(sdf.format(calSelection.getTime()));
            }
        });
    }
}
