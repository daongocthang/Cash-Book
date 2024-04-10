package com.standalone.cashbook;

import android.os.Bundle;
import android.text.InputType;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.standalone.cashbook.layouts.KeyboardCustom;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText edt = findViewById(R.id.edt);
        KeyboardCustom kb = findViewById(R.id.kb);

        edt.setShowSoftInputOnFocus(false);
        edt.setRawInputType(InputType.TYPE_CLASS_TEXT);

        InputConnection ic = edt.onCreateInputConnection(new EditorInfo());
        kb.setInputConnection(ic);
    }
}