package com.example.todo.adapters.base;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.todo.adapters.base.callbacks.DragDismissCallback;

import java.util.Collections;
import java.util.List;

public abstract class DragDismissAdapter<VH extends RecyclerView.ViewHolder, ITEM> extends BaseListAdapter<VH, ITEM> {

    private static final String TAG = "DragDismissAdapter";

    private ItemTouchHelper itemTouchHelper;
    private int dragViewResId;

    private OnItemReorderListener itemReorderListener;
    private OnItemDismissedListener itemDismissedListener;

    public DragDismissAdapter(Context context) {
        this(context, 0);
    }

    public DragDismissAdapter(Context context, int dragViewResId) {
        super(context);
        this.itemTouchHelper = new ItemTouchHelper(new DragDismissCallback(this));
        this.dragViewResId = dragViewResId;
    }

    public void attachToRecyclerView(RecyclerView recyclerView) {
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public int getDragViewResId() {
        return dragViewResId;
    }

    public void setOnItemReorderListener(OnItemReorderListener itemReorderListener) {
        this.itemReorderListener = itemReorderListener;
    }

    public void setOnItemDismissedListener(OnItemDismissedListener itemDismissedListener) {
        this.itemDismissedListener = itemDismissedListener;
    }

    public boolean canDragItems() {
        return getData() != null && getData().size() > 1;
    }

    public boolean canDismissItems() {
        return true;
    }

    @Override
    public void onBindViewHolder(final VH holder, int position) {
        super.onBindViewHolder(holder, position);
        if (dragViewResId != 0) {
            View dragView = holder.itemView.findViewById(dragViewResId);
            if (dragView != null) {
                dragView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                            if (canDragItems()) {
                                itemTouchHelper.startDrag(holder);
                            }
                        }
                        return false;
                    }
                });
            } else {
                Log.i(TAG, "onBindViewHolder: dragView was not found");
            }
        }
    }

    public void onItemMoved(int fromPosition, int toPosition) {
        Collections.swap(getData(), fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        if (itemReorderListener != null) {
            itemReorderListener.onItemOrderChanged(getData());
        }
    }

    public void onItemSwiped(int position) {
        if (itemDismissedListener != null) {
            itemDismissedListener.onItemDismissed(getData().get(position));
        }
        getData().remove(position);
        notifyItemRemoved(position);
    }

    public interface OnItemReorderListener<ITEM> {

        void onItemOrderChanged(List<ITEM> reorderedItems);

    }

    public interface OnItemDismissedListener<ITEM> {

        void onItemDismissed(ITEM item);

    }
}
