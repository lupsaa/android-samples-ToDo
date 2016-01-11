package com.example.todo.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.todo.R;
import com.example.todo.adapters.SectionAdapter;
import com.example.todo.adapters.base.BaseListAdapter.OnItemClickListener;
import com.example.todo.adapters.base.BaseListAdapter.OnItemLongClickListener;
import com.example.todo.adapters.base.DragDismissAdapter.OnItemReorderListener;
import com.example.todo.db.Database;
import com.example.todo.db.beans.Section;
import com.example.todo.dialogs.ContextDialog;
import com.example.todo.dialogs.ContextDialog.ContextDialogListener;
import com.example.todo.dialogs.SectionCreationDialog;
import com.example.todo.fragments.base.BaseFragment;
import com.example.todo.util.Event;
import com.example.todo.util.Event.EventType;

import java.util.List;

import de.greenrobot.dao.async.AsyncOperation;
import de.greenrobot.dao.async.AsyncOperationListener;

public class SectionFragment extends BaseFragment {

    private RecyclerView sectionList;
    private SectionAdapter sectionAdapter;

    private OnSectionChangedListener onSectionChangedListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.onSectionChangedListener = (OnSectionChangedListener) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_section, container, false);

        sectionList = (RecyclerView) view.findViewById(R.id.recycler_view);
        sectionList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        sectionAdapter = new SectionAdapter(getActivity());
        sectionAdapter.attachToRecyclerView(sectionList);
        sectionList.setAdapter(sectionAdapter);

        SectionAdapterListener adapterListener = new SectionAdapterListener();
        sectionAdapter.setOnItemClickListener(adapterListener);
        sectionAdapter.setOnItemLongClickListener(adapterListener);
        sectionAdapter.setOnItemReorderListener(adapterListener);

        loadSections();

        return view;
    }

    private void loadSections() {
        Database.getDefault().getSectionHandler().getSectionsOrderedByPosition(new SectionListener());
    }

    public void onActionButtonClick() {
        new SectionCreationDialog(null).show(getActivity());
    }

    @Override
    public void onEventMainThread(Event event) {
        super.onEventMainThread(event);
        switch (event.getType()) {
            case DB_SECTION_TABLE_UPDATED:
                loadSections();
                break;
        }
    }

    private class SectionListener implements AsyncOperationListener {

        @Override
        @SuppressWarnings("unchecked")
        public void onAsyncOperationCompleted(AsyncOperation operation) {
            List<Section> sections = (List<Section>) operation.getResult();
            Section selectedSection = null;
            sectionAdapter.setData(sections);
            for (Section section : sections) {
                if (section.getIsSelected()) {
                    selectedSection = section;
                    break;
                }
            }
            onSectionChangedListener.onSectionChanged(selectedSection);
        }
    }

    private class SectionAdapterListener implements OnItemClickListener<Section>,
            OnItemLongClickListener<Section>, OnItemReorderListener<Section>, ContextDialogListener<Section> {

        @Override
        public void onItemClick(ViewHolder viewHolder, Section section) {
            Database.getDefault().getSectionHandler().selectSection(section);
            new Event(EventType.ACTION_CLOSE_NAVIGATION_DRAWER).submitWithDelay(100);
        }

        @Override
        public boolean onItemLongClick(ViewHolder viewHolder, Section section) {
            new ContextDialog<>(section, sectionAdapter.getData().size() > 1, this).show(getActivity());
            return true;
        }

        @Override
        public void onItemOrderChanged(List<Section> reorderedItems) {
            Database.getDefault().getSectionHandler().updateSectionPositions(reorderedItems);
        }

        @Override
        public void onEdit(Section section) {
            new SectionCreationDialog(section).show(getActivity());
        }

        @Override
        public void onDelete(Section section) {
            Database.getDefault().getSectionHandler().deleteSection(section);
        }
    }

    public interface OnSectionChangedListener {

        void onSectionChanged(Section section);

    }
}
