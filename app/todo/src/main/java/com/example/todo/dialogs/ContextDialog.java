package com.example.todo.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;

import com.example.todo.R;

import java.util.LinkedList;
import java.util.List;

public class ContextDialog<Item> {

    private Item item;
    private ContextDialogListener<Item> listener;

    private boolean showDelete;

    private static final int EDIT = 0;
    private static final int DELETE = 1;

    public ContextDialog(Item item, boolean showDelete, ContextDialogListener<Item> listener) {
        this.item = item;
        this.listener = listener;
        this.showDelete = showDelete;
    }

    public void show(final Activity activity) {
        List<String> items = new LinkedList<>();
        items.add(activity.getString(R.string.edit));
        if (showDelete) {
            items.add(activity.getString(R.string.delete));
        }
        new AlertDialog.Builder(activity, R.style.DialogTheme)
                .setItems(items.toArray(new String[items.size()]), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case EDIT:
                                edit();
                                break;
                            case DELETE:
                                delete(activity);
                                break;
                        }
                    }
                })
                .show();
    }

    private void edit() {
        if (listener != null) {
            listener.onEdit(item);
        }
    }

    private void delete(Activity activity) {
        new Builder(activity, R.style.DialogTheme)
                .setTitle(R.string.are_you_sure)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onDelete(item);
                        }
                    }
                }).show();
    }

    public interface ContextDialogListener<Item> {

        void onEdit(Item item);

        void onDelete(Item item);

    }
}
