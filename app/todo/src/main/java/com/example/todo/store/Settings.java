package com.example.todo.store;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {

    private static final String NAME = "todo.settings";

    private static final String KEY_IS_FIRST_START = "is_first_start";

    private static Settings instance;

    private SharedPreferences pref;

    private Settings(Context context) {
        pref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static Settings getInstance() {
        return instance;
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new Settings(context);
        }
    }

    public boolean isFirstStart() {
        return pref.getBoolean(KEY_IS_FIRST_START, true);
    }

    public void setIsFirstStart(boolean firstStart) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(KEY_IS_FIRST_START, firstStart);
        editor.apply();
    }
}
