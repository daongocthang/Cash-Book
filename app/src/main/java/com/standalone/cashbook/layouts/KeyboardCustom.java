package com.standalone.cashbook.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;

import com.standalone.cashbook.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class KeyboardCustom extends LinearLayout implements View.OnClickListener {
    AppCompatButton bt00, bt0, bt1, bt2, bt3, bt4, bt5, bt6, bt7, bt8, bt9, btC;
    AppCompatImageButton btBackspace, btApply;
    SparseArray<String> keyValues = new SparseArray<>();
    InputConnection inputConnection;

    public KeyboardCustom(Context context) {
        super(context);
        init(context);
    }

    public KeyboardCustom(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public KeyboardCustom(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public KeyboardCustom(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void setInputConnection(InputConnection inputConnection) {
        this.inputConnection = inputConnection;
    }

    private void init(Context ctx) {
        LayoutInflater.from(ctx).inflate(R.layout.keyboard_custom, this, true);

        bt00 = findViewById(R.id.bt_00);
        bt0 = findViewById(R.id.bt_0);
        bt1 = findViewById(R.id.bt_1);
        bt2 = findViewById(R.id.bt_2);
        bt3 = findViewById(R.id.bt_3);
        bt4 = findViewById(R.id.bt_4);
        bt5 = findViewById(R.id.bt_5);
        bt6 = findViewById(R.id.bt_6);
        bt7 = findViewById(R.id.bt_7);
        bt8 = findViewById(R.id.bt_8);
        bt9 = findViewById(R.id.bt_9);
        btC = findViewById(R.id.bt_clear);
        btBackspace = findViewById(R.id.bt_backspace);
        btApply = findViewById(R.id.bt_apply);

        bt00.setOnClickListener(this);
        bt0.setOnClickListener(this);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);
        bt4.setOnClickListener(this);
        bt5.setOnClickListener(this);
        bt6.setOnClickListener(this);
        bt7.setOnClickListener(this);
        bt8.setOnClickListener(this);
        bt9.setOnClickListener(this);
        btC.setOnClickListener(this);
        btBackspace.setOnClickListener(this);
        btApply.setOnClickListener(this);

        keyValues.put(R.id.bt_0, "0");
        keyValues.put(R.id.bt_1, "1");
        keyValues.put(R.id.bt_2, "2");
        keyValues.put(R.id.bt_3, "3");
        keyValues.put(R.id.bt_4, "4");
        keyValues.put(R.id.bt_5, "5");
        keyValues.put(R.id.bt_6, "6");
        keyValues.put(R.id.bt_7, "7");
        keyValues.put(R.id.bt_8, "8");
        keyValues.put(R.id.bt_9, "9");
        keyValues.put(R.id.bt_00, "00");

    }

    @Override
    public void onClick(View view) {
        if (inputConnection == null) return;


        switch (view.getId()) {
            case R.id.bt_backspace:
                inputConnection.deleteSurroundingText(1, 0);
                formatDecimal();
                break;
            case R.id.bt_clear:
                inputConnection.deleteSurroundingText(getText().length(), 0);
                break;
            case R.id.bt_apply:
                //TODO: commit
                break;
            default:
                inputConnection.commitText(keyValues.get(view.getId()), 0);
                formatDecimal();
        }

    }


    public String getText() {
        CharSequence extracted = inputConnection.getExtractedText(new ExtractedTextRequest(), 0).text;
        return extracted.toString();
    }

    private void formatDecimal() {
        String original = getText();

        if (original.length() < 4) return;

        if (original.contains(",")) {
            original = original.replace(",", "");
        }

        long longVal = Long.parseLong(original);

        DecimalFormat fmt = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        fmt.applyLocalizedPattern("#,###");


        inputConnection.deleteSurroundingText(getText().length(), 0);
        inputConnection.commitText(fmt.format(longVal), 0);
    }

}
