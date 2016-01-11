package com.example.todo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.todo.R;
import com.example.todo.adapters.ToDoAdapter.ToDoViewHolder;
import com.example.todo.adapters.base.DragDismissAdapter;
import com.example.todo.db.beans.ToDoItem;

public class ToDoAdapter extends DragDismissAdapter<ToDoViewHolder, ToDoItem> {

    public ToDoAdapter(Context context) {
        super(context, R.id.list_item_drag_handle);
    }

    @Override
    public ToDoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ToDoViewHolder(getLayoutInflater().inflate(R.layout.layout_list_item_todo, parent, false));
    }

    @Override
    protected void onBindViewHolder(ToDoViewHolder holder, ToDoItem toDoItem) {
        holder.title.setText(toDoItem.getTitle());
        holder.description.setText(toDoItem.getDescription());
        holder.description.setVisibility(!TextUtils.isEmpty(toDoItem.getDescription()) ? View.VISIBLE : View.GONE);
    }

    protected class ToDoViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView description;

        public ToDoViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.list_item_title);
            description = (TextView) itemView.findViewById(R.id.list_item_description);
        }
    }
}
