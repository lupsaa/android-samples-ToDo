package com.example.todo.fragments.base;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import com.example.todo.app.ToDoApplication;
import com.example.todo.store.Settings;
import com.example.todo.util.Event;

import de.greenrobot.event.EventBus;

public class BaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    protected Context getApplicationContext() {
        return ToDoApplication.getInstance().getApplicationContext();
    }

    protected Settings getSettings() {
        return Settings.getInstance();
    }

    protected boolean isFragmentActive() {
        return isAdded() && !isDetached() && !isRemoving();
    }

    public void onEventMainThread(Event event) {
    }
}
