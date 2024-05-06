package com.standalone.core.utils;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.standalone.core.R;
import com.standalone.core.adapters.AutoCompleteAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SuggestionView extends LinearLayout implements TextWatcher {
    static final String TAG = SuggestionView.class.getSimpleName();

    static final int MAX_LONG_FORMAT = 18;

    Context context;
    AutoCompleteAdapter adapter;
    int threshold = 0;
    int completionThreshold = 10;
    int size = 3;

    boolean reverse = false;
    TextInputEditText editText;
    RecyclerView recycler;


    public SuggestionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SuggestionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public SuggestionView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.suggestion_view, this, true);
        this.context = context;
        recycler = findViewById(R.id.recycler);
        adapter = new AutoCompleteAdapter(new AutoCompleteAdapter.OnItemClickListener() {
            @Override
            public void onClick(String text) {
                if (editText == null) return;

                editText.setText(text);
                editText.setSelection(text.length());
                setVisibility(INVISIBLE);
                hideSoftKeyboard();
            }
        });

        this.bringToFront();
    }

    public void leave() {
        if (editText != null) editText.clearFocus();
    }

    public void focus() {
        if (editText == null) return;
        editText.requestFocus();
        editText.setText(editText.getText());
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public void setCompletionThreshold(int completionThreshold) {
        this.completionThreshold = completionThreshold;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public void attachEditText(TextInputEditText editText) {
        recycler.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycler.setLayoutManager(layoutManager);

        this.editText = editText;
        this.editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        this.editText.addTextChangedListener(this);
        this.editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    adapter.clear();
                } else {
                    editText.setText(editText.getText());
                }

            }
        });
        this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                int result = i & EditorInfo.IME_ACTION_DONE;
                if (result == EditorInfo.IME_ACTION_DONE) {
                    hideSoftKeyboard();
                }
                return false;
            }
        });
        this.editText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                focus();
            }
        });
    }

    private void hideSoftKeyboard() {
        assert editText != null;
        View view = editText.getRootView();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        adapter.clear();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        adapter.clear();

        if (!editText.isEnabled() || TextUtils.isEmpty(s)) {
            return;
        }

        editText.removeTextChangedListener(this);

        if (s.length() > 0 && editText.hasFocus()) {
            String textInput = s.toString().replace(",", "");
            try {
                int completion = Math.min(completionThreshold, MAX_LONG_FORMAT);
                if (textInput.length() > completion) {
                    textInput = textInput.substring(0, completion);
                } else if (textInput.length() > threshold) {
                    final List<String> itemList = new ArrayList<>();

                    long longInput = Long.parseLong(textInput);
                    int count = 0;
                    while (count < size) {
                        count++;
                        long suggestion = (long) (longInput * Math.pow(10, count));
                        if (String.valueOf(suggestion).length() > completion) break;
                        itemList.add(String.format(Locale.US, "%,d", suggestion));
                    }
                    adapter.setItemList(itemList);
                }
                textInput = String.format(Locale.US, "%,d", Long.parseLong(textInput));

                editText.setText(textInput);
                editText.setSelection(textInput.length());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            adapter.clear();
        }

        editText.addTextChangedListener(this);
    }

}
