package com.example.todo.util;

import com.example.todo.app.ToDoApplication;

import de.greenrobot.event.EventBus;

public class Event {

    // Mind my laziness here. I know that this is a bad ideea.
    // This is a tiny app with 4 events and at most 3 registered listeners.
    // I know that I could have done it way better but creating separate event classes and
    // registering each one where needed it's too much at 2AM in the morning.

    public enum EventType {

        ACTION_CLOSE_NAVIGATION_DRAWER,
        ACTION_TOGGLE_ACTION_BUTTON_VISIBILITY,

        DB_SECTION_TABLE_UPDATED,
        DB_TO_DO_ITEM_TABLE_UPDATED;

    }

    private EventType type;
    private Object data;

    public Event(EventType type) {
        this(type, null);
    }

    public Event(EventType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public EventType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public void submit() {
        EventBus.getDefault().post(this);
    }

    public void submitWithDelay(long delay) {
        ToDoApplication.getInstance().getUIThreadHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Event.this.submit();
            }
        }, delay);
    }
}
