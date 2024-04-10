package com.standalone.cashbook.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
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

public class KeyboardCustom extends LinearLayout implements View.OnClickListener {
    AppCompatButton bt00, bt0, bt1, bt2, bt3, bt4, bt5, bt6, bt7, bt8, bt9, btComma;
    AppCompatImageButton btBackspace, btPlus, btSubtract, btEqualOrAccept;
    boolean calculating = false;
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
        btComma = findViewById(R.id.bt_comma);
        btBackspace = findViewById(R.id.bt_backspace);
        btPlus = findViewById(R.id.bt_plus);
        btSubtract = findViewById(R.id.bt_subtract);
        btEqualOrAccept = findViewById(R.id.bt_equal_or_accept);

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
        btComma.setOnClickListener(this);
        btBackspace.setOnClickListener(this);
        btPlus.setOnClickListener(this);
        btSubtract.setOnClickListener(this);
        btEqualOrAccept.setOnClickListener(this);

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
        keyValues.put(R.id.bt_comma, ",");
    }

    @Override
    public void onClick(View view) {
        if (inputConnection == null) return;


        String op = "+";
        switch (view.getId()) {
            case R.id.bt_backspace:
                inputConnection.deleteSurroundingText(1, 0);
                break;
            case R.id.bt_subtract:
                op = "-";
            case R.id.bt_plus:
                calculating = true;
                btEqualOrAccept.setImageResource(R.drawable.ic_equal);
                inputConnection.commitText(op, 1);
                break;
            case R.id.bt_equal_or_accept:
                if (calculating) {
                    CharSequence extractedText = inputConnection.getExtractedText(new ExtractedTextRequest(), 0).text;
                    calculate(extractedText.toString());
                    btEqualOrAccept.setImageResource(R.drawable.ic_accept);
                } else {
                    //TODO: commit
                }
                break;
            default:
                inputConnection.commitText(keyValues.get(view.getId()), 1);
        }

    }

    private void calculate(String str) {
        String[] separated = str.split("[+]");
        Log.i("CONSOLE", separated[0].toString());
    }

}
