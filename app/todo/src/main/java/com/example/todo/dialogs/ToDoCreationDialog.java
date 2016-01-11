package com.example.todo.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.todo.R;
import com.example.todo.db.Database;
import com.example.todo.db.beans.Section;
import com.example.todo.db.beans.ToDoItem;

public class ToDoCreationDialog {

    private Section section;
    private ToDoItem toDoItem;

    public ToDoCreationDialog(Section section, ToDoItem toDoItem) {
        this.section = section;
        this.toDoItem = toDoItem;
    }

    public void show(final Activity activity) {
        String dialogTitle = activity.getString(toDoItem != null ? R.string.edit_todo : R.string.create_todo);
        String title = toDoItem != null ? toDoItem.getTitle() : "";
        String description = toDoItem != null ? toDoItem.getDescription() : "";

        int negativeButton = R.string.cancel;
        int positiveButton = toDoItem != null ? R.string.save : R.string.create;

        View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_todo_creation, null, false);
        final EditText titleInput = (EditText) dialogView.findViewById(R.id.title_input);
        final EditText descriptionInput = (EditText) dialogView.findViewById(R.id.description_input);

        titleInput.setText(title);
        titleInput.setSelection(title.length());

        descriptionInput.setText(description);
        descriptionInput.setSelection(description.length());

        Builder dialogBuilder = new Builder(activity, R.style.DialogTheme);
        dialogBuilder.setTitle(dialogTitle);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setNegativeButton(negativeButton, null);
        dialogBuilder.setPositiveButton(positiveButton, null);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = titleInput.getText().toString();
                        String description = descriptionInput.getText().toString();
                        if (!TextUtils.isEmpty(title)) {
                            if (toDoItem != null) {
                                toDoItem.setTitle(title);
                                toDoItem.setDescription(description);
                                Database.getDefault().getToDoHandler().updateToDoItem(toDoItem);
                            } else {
                                Database.getDefault().getToDoHandler().addToDoItem(new ToDoItem(null, 0, title, description, section.getId()));
                            }
                            dialog.dismiss();
                        } else {
                            Toast.makeText(activity, R.string.error_title_empty, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        alertDialog.show();
    }
}
