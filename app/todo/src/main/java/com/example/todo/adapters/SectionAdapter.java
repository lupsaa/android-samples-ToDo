package com.example.todo.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.todo.R;
import com.example.todo.adapters.SectionAdapter.SectionViewHolder;
import com.example.todo.adapters.base.DragDismissAdapter;
import com.example.todo.db.beans.Section;

public class SectionAdapter extends DragDismissAdapter<SectionViewHolder, Section> {

    public SectionAdapter(Context context) {
        super(context, R.id.list_item_drag_handle);
    }

    @Override
    public SectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SectionViewHolder(getLayoutInflater().inflate(R.layout.layout_list_item_section, parent, false));
    }

    @Override
    protected void onBindViewHolder(SectionViewHolder holder, Section section) {
        holder.text.setText(section.getName());
        holder.text.setTextColor(ContextCompat.getColor(getContext(),
                section.getIsSelected() ? android.R.color.white : R.color.text_color_secondary));
        holder.icon.setImageResource(section.getIsSelected() ? R.drawable.ic_reorder_white : R.drawable.ic_reorder);
        holder.background.setBackgroundColor(ContextCompat.getColor(getContext(),
                section.getIsSelected() ? R.color.primary_color : android.R.color.transparent));
    }

    @Override
    public boolean canDismissItems() {
        return false;
    }

    protected class SectionViewHolder extends RecyclerView.ViewHolder {

        public View background;
        public ImageView icon;
        public TextView text;

        public SectionViewHolder(View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.list_item_background);
            icon = (ImageView) itemView.findViewById(R.id.list_item_icon);
            text = (TextView) itemView.findViewById(R.id.list_item_text);
        }
    }
}
