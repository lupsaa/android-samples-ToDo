package com.example.todo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.todo.R;
import com.example.todo.adapters.ToDoAdapter;
import com.example.todo.adapters.base.BaseListAdapter.OnItemLongClickListener;
import com.example.todo.adapters.base.DragDismissAdapter.OnItemDismissedListener;
import com.example.todo.adapters.base.DragDismissAdapter.OnItemReorderListener;
import com.example.todo.db.Database;
import com.example.todo.db.beans.Section;
import com.example.todo.db.beans.ToDoItem;
import com.example.todo.dialogs.ContextDialog;
import com.example.todo.dialogs.ContextDialog.ContextDialogListener;
import com.example.todo.dialogs.ToDoCreationDialog;
import com.example.todo.fragments.base.BaseFragment;
import com.example.todo.listeners.UIScrollListener;
import com.example.todo.util.Event;
import com.example.todo.util.Event.EventType;

import java.util.List;

import de.greenrobot.dao.async.AsyncOperation;
import de.greenrobot.dao.async.AsyncOperationListener;

public class ToDoFragment extends BaseFragment {

    private RecyclerView toDoList;
    private ToDoAdapter toDoAdapter;

    private Section section;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        toDoList = (RecyclerView) inflater.inflate(R.layout.fragment_todo, container, false);
        toDoList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        toDoList.addOnScrollListener(new ToDoScrollListener());

        toDoAdapter = new ToDoAdapter(getActivity());
        toDoAdapter.attachToRecyclerView(toDoList);
        toDoList.setAdapter(toDoAdapter);

        ToDoAdapterListener adapterListener = new ToDoAdapterListener();
        toDoAdapter.setOnItemLongClickListener(adapterListener);
        toDoAdapter.setOnItemReorderListener(adapterListener);
        toDoAdapter.setOnItemDismissedListener(adapterListener);

        return toDoList;
    }

    public void loadToDoItemsForSection(Section section) {
        Database.getDefault().getToDoHandler().getToDoItemsOrderedByPosition(
                section != null ? section.getId() : -1, new ToDoListener(section));
    }

    public void onActionButtonClick() {
        new ToDoCreationDialog(section, null).show(getActivity());
    }

    @Override
    public void onEventMainThread(Event event) {
        super.onEventMainThread(event);
        switch (event.getType()) {
            case DB_TO_DO_ITEM_TABLE_UPDATED:
                loadToDoItemsForSection(section);
                break;
        }
    }

    private class ToDoAdapterListener implements OnItemLongClickListener<ToDoItem>, OnItemReorderListener<ToDoItem>,
            OnItemDismissedListener<ToDoItem>, ContextDialogListener<ToDoItem> {

        @Override
        public boolean onItemLongClick(ViewHolder viewHolder, ToDoItem toDoItem) {
            new ContextDialog<>(toDoItem, toDoAdapter.getData().size() > 1, this).show(getActivity());
            return true;
        }

        @Override
        public void onItemOrderChanged(List<ToDoItem> reorderedItems) {
            Database.getDefault().getToDoHandler().updateToDoItemsPositions(reorderedItems);
        }

        @Override
        public void onItemDismissed(ToDoItem toDoItem) {
            Database.getDefault().getToDoHandler().deleteToDoItem(toDoItem, false);
        }

        @Override
        public void onEdit(ToDoItem toDoItem) {
            new ToDoCreationDialog(section, toDoItem).show(getActivity());
        }

        @Override
        public void onDelete(ToDoItem toDoItem) {
            Database.getDefault().getToDoHandler().deleteToDoItem(toDoItem, true);
        }
    }

    private class ToDoScrollListener extends UIScrollListener {

        @Override
        public void onToggleUIVisibility(boolean visible) {
            new Event(EventType.ACTION_TOGGLE_ACTION_BUTTON_VISIBILITY, visible).submit();
        }
    }

    private class ToDoListener implements AsyncOperationListener {

        private Section loadedSection;

        private ToDoListener(Section loadedSection) {
            this.loadedSection = loadedSection;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onAsyncOperationCompleted(AsyncOperation operation) {
            List<ToDoItem> toDoItems = (List<ToDoItem>) operation.getResult();
            toDoAdapter.setData(toDoItems);
            section = loadedSection;
        }
    }
}
