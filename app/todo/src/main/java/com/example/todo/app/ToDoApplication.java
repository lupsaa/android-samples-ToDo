package com.example.todo.app;

import android.app.Application;
import android.os.Handler;

import com.example.todo.db.Database;
import com.example.todo.store.Settings;

public class ToDoApplication extends Application {

    public static ToDoApplication instance;

    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        handler = new Handler();

        Settings.init(getApplicationContext());
        Database.initDefault(getApplicationContext());

        if (Settings.getInstance().isFirstStart()) {
            Database.getDefault().populateDatabase(5, 100);
            Settings.getInstance().setIsFirstStart(false);
        }
    }

    public static ToDoApplication getInstance() {
        return instance;
    }

    public Handler getUIThreadHandler() {
        return handler;
    }
}
