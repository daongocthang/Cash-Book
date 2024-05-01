package com.standalone.core.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefUtil {
    static final String PREFS_NAME = "app_level_prefs";
    @SuppressLint("StaticFieldLeak")
    static SharedPrefUtil instance;
    SharedPreferences pref;
    Context context;

    private SharedPrefUtil() {
    }

    public static SharedPrefUtil from(Context context) {
        if (instance == null) {
            instance = new SharedPrefUtil();
        }

        instance.declare(context);
        return instance;
    }

    private void declare(Context context) {
        pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean contains(String key) {
        return pref.contains(key);
    }

    public void put(String key, Object val) {
        if (val instanceof String)
            pref.edit().putString(key, (String) val).apply();
        else if (val instanceof Boolean)
            pref.edit().putBoolean(key, (Boolean) val).apply();
        else if (val instanceof Integer)
            pref.edit().putInt(key, (Integer) val).apply();
        else if (val instanceof Float)
            pref.edit().putFloat(key, (Float) val).apply();
        else if (val instanceof Long)
            pref.edit().putLong(key, (Long) val).apply();
        else
            throw new IllegalArgumentException(String.format("`%s` is invalid.", val.getClass()));
    }

    public <T> T get(String key, Class<T> typeClass) {
        try {
            Object object = null;
            if (typeClass.equals(String.class))
                object = pref.getString(key, null);
            else if (typeClass.equals(Boolean.class))
                object = pref.getBoolean(key, false);
            else if (typeClass.equals(Integer.class))
                object = pref.getInt(key, 0);
            else if (typeClass.equals(Float.class))
                object = pref.getFloat(key, 0F);
            else if (typeClass.equals(Long.class))
                object = pref.getLong(key, 0L);
            else
                throw new IllegalArgumentException();

            return typeClass.cast(object);
        } catch (ClassCastException | IllegalArgumentException e) {
            return null;
        }

    }

    public void remove(String key) {
        pref.edit().remove(key).apply();
    }

    public void removeAll() {
        pref.edit().clear().apply();
    }


}
