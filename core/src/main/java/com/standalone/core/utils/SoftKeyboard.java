package com.standalone.core.utils;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

public class SoftKeyboard {
    final View parent;
    final Context context;

    public SoftKeyboard(Context context, View parentView) {
        this.parent = parentView;
        this.context=context;
    }

    public void hide() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(parent.getWindowToken(), 0);
    }

    public void setOnVisibilityChangedListener(KeyboardObserver onVisibilityChangeListener) {
        parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            boolean alreadyOpen;
            final int DEFAULT_HEIGHT_KEYBOARD_DP = 100;
            final int ESTIMATED_KEYBOARD_DP = DEFAULT_HEIGHT_KEYBOARD_DP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ESTIMATED_KEYBOARD_DP, parent.getResources().getDisplayMetrics());
                parent.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parent.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean shown = heightDiff >= estimatedKeyboardHeight;
                // ignore global layout changed
                if (shown == alreadyOpen) return;
                alreadyOpen = shown;
                onVisibilityChangeListener.onVisibilityChanged(shown);
            }
        });
    }

    public interface KeyboardObserver {
        void onVisibilityChanged(boolean visible);
    }
}
