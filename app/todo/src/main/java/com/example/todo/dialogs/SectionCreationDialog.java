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

public class SectionCreationDialog {

    private Section section;

    public SectionCreationDialog(Section section) {
        this.section = section;
    }

    public void show(final Activity activity) {
        String title = activity.getString(section != null ? R.string.edit_section : R.string.create_section);
        String name = section != null ? section.getName() : "";

        int negativeButton = R.string.cancel;
        int positiveButton = section != null ? R.string.save : R.string.create;

        View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_section_creation, null, false);
        final EditText nameInput = (EditText) dialogView.findViewById(R.id.name_input);
        nameInput.setText(name);
        nameInput.setSelection(name.length());

        Builder dialogBuilder = new Builder(activity, R.style.DialogTheme);
        dialogBuilder.setTitle(title);
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
                        String name = nameInput.getText().toString();
                        if (!TextUtils.isEmpty(name)) {
                            if (section != null) {
                                section.setName(name);
                                Database.getDefault().getSectionHandler().updateSection(section);
                            } else {
                                Database.getDefault().getSectionHandler().addSection(new Section(null, 0, name, true));
                            }
                            dialog.dismiss();
                        } else {
                            Toast.makeText(activity, R.string.error_page_name_empty, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        alertDialog.show();
    }
}
