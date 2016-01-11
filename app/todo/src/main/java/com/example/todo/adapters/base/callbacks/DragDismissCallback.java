package com.example.todo.adapters.base.callbacks;

import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.todo.adapters.base.DragDismissAdapter;
import com.example.todo.util.Helper;

public class DragDismissCallback extends ItemTouchHelper.Callback {

    private static final long DRAG_VIEW_ELEVATION_ANIM_DURATION = 150;
    private static final float DRAG_VIEW_ELEVATION = 12f;
    private static final float SWIPE_ALPHA_FULL = 1.0f;

    private DragDismissAdapter adapter;

    private boolean isDragViewElevated;

    public DragDismissCallback(DragDismissAdapter adapter) {
        if (adapter == null) {
            throw new IllegalArgumentException("DragDismissAdapter cannot be null");
        }
        this.adapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return adapter.canDragItems() && adapter.getDragViewResId() == 0;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return adapter.canDismissItems();
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            return makeMovementFlags(adapter.canDragItems() ?
                    ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT : 0, 0);
        } else {
            final int dragFlags = adapter.canDragItems() ? ItemTouchHelper.UP | ItemTouchHelper.DOWN : 0;
            final int swipeFlags = adapter.canDismissItems() ? ItemTouchHelper.START | ItemTouchHelper.END : 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        if (source.getItemViewType() != target.getItemViewType()) {
            return false;
        }
        adapter.onItemMoved(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.onItemSwiped(viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            viewHolder.itemView.setAlpha(SWIPE_ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth());
            viewHolder.itemView.setTranslationX(dX);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && isCurrentlyActive && !isDragViewElevated && Helper.isAndroidL()) {
                ViewCompat.animate(viewHolder.itemView).translationZ(DRAG_VIEW_ELEVATION).setDuration(DRAG_VIEW_ELEVATION_ANIM_DURATION).start();
                isDragViewElevated = true;
            }
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setAlpha(SWIPE_ALPHA_FULL);
        if (isDragViewElevated) {
            ViewCompat.animate(viewHolder.itemView).translationZ(0f).setDuration(DRAG_VIEW_ELEVATION_ANIM_DURATION).start();
            isDragViewElevated = false;
        }
    }
}
