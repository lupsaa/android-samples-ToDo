package com.example.todo.listeners;

import android.support.v7.widget.RecyclerView;

import com.example.todo.util.Constants.Direction;

public abstract class UIScrollListener extends RecyclerView.OnScrollListener {

    private static final int HIDE_THRESHOLD = 20;
    private static final int SHOW_THRESHOLD = -20;

    private boolean isUIVisible;

    private int scrollState;
    private int scrolledDistance = 0;

    public abstract void onToggleUIVisibility(boolean visible);

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        scrollState = newState;
    }

    @Override
    public void onScrolled(RecyclerView rv, int dx, int dy) {
        super.onScrolled(rv, dx, dy);
        boolean fromUser = scrollState == RecyclerView.SCROLL_STATE_DRAGGING;

        if (isUIVisible && scrolledDistance > HIDE_THRESHOLD) {
            toggleUIVisibility(false);
        } else if (!isUIVisible && ((scrolledDistance < SHOW_THRESHOLD && fromUser) || !canScroll(rv, Direction.UP))) {
            toggleUIVisibility(true);
        }

        if ((isUIVisible && dy > 0) || (!isUIVisible && dy < 0)) {
            scrolledDistance += dy;
        }
    }

    public void toggleUIVisibility(boolean visible) {
        isUIVisible = visible;
        scrolledDistance = 0;
        onToggleUIVisibility(visible);
    }

    private boolean canScroll(RecyclerView rv, Direction direction) {
        return rv.canScrollVertically(direction == Direction.UP ? -1 : 1);
    }
}
