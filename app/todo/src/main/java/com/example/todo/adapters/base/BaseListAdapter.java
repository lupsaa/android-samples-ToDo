package com.example.todo.adapters.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;

import java.util.List;

public abstract class BaseListAdapter<VH extends ViewHolder, ITEM> extends RecyclerView.Adapter<VH> {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<ITEM> data;

    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener itemLongClickListener;

    protected abstract void onBindViewHolder(VH holder, ITEM item);

    public BaseListAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    protected Context getContext() {
        return context;
    }

    protected LayoutInflater getLayoutInflater() {
        return layoutInflater;
    }

    public List<ITEM> getData() {
        return data;
    }

    public void setData(List<ITEM> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    @Override
    public void onBindViewHolder(final VH holder, int position) {
        final ITEM item = data.get(position);
        onBindViewHolder(holder, item);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(holder, item);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (itemLongClickListener != null) {
                    return itemLongClickListener.onItemLongClick(holder, item);
                }
                return false;
            }
        });
    }

    public interface OnItemClickListener<ITEM> {

        void onItemClick(ViewHolder viewHolder, ITEM item);

    }

    public interface OnItemLongClickListener<ITEM> {

        boolean onItemLongClick(ViewHolder viewHolder, ITEM item);

    }
}
