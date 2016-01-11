package com.example.todo.activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.todo.R;
import com.example.todo.db.beans.Section;
import com.example.todo.fragments.SectionFragment;
import com.example.todo.fragments.SectionFragment.OnSectionChangedListener;
import com.example.todo.fragments.ToDoFragment;
import com.example.todo.util.Event;
import com.example.todo.util.Helper;

import de.greenrobot.event.EventBus;

public class ToDoActivity extends AppCompatActivity implements OnSectionChangedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private FloatingActionButton actionButton;

    private SectionFragment sectionFragment;
    private ToDoFragment toDoFragment;

    private DrawerListener drawerListener;

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        setContentView(R.layout.activity_todo);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        actionButton = (FloatingActionButton) findViewById(R.id.action_button);
        actionButton.setOnClickListener(new ActionButtonListener());

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerListener = new DrawerListener(this, drawerLayout, 0, 0);
        drawerLayout.setDrawerListener(drawerListener);
        drawerLayout.setDrawerShadow(R.drawable.shadow_drawer, GravityCompat.START);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.left_drawer_container, sectionFragment = new SectionFragment());
            transaction.add(R.id.main_container, toDoFragment = new ToDoFragment());
            transaction.commit();
        } else {
            sectionFragment = (SectionFragment) getFragmentManager().findFragmentById(R.id.left_drawer_container);
            toDoFragment = (ToDoFragment) getFragmentManager().findFragmentById(R.id.main_container);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerListener.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerListener.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerListener.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onEventMainThread(Event event) {
        switch (event.getType()) {
            case ACTION_CLOSE_NAVIGATION_DRAWER:
                drawerLayout.closeDrawers();
                break;
            case ACTION_TOGGLE_ACTION_BUTTON_VISIBILITY:
                boolean show = (Boolean) event.getData();
                actionButton.animate().translationY(show ? 0 : (float) (actionButton.getHeight() * 1.5)).start();
                break;
        }
    }

    @Override
    public void onSectionChanged(Section section) {
        toolbar.setTitle(section == null ? getString(R.string.app_name) : section.getName());
        toDoFragment.loadToDoItemsForSection(section);
    }

    private class ActionButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                sectionFragment.onActionButtonClick();
            } else {
                toDoFragment.onActionButtonClick();
            }
        }
    }

    private class DrawerListener extends ActionBarDrawerToggle {

        private Point screenSize;
        private int drawerWidth;

        public DrawerListener(Activity activity, DrawerLayout drawerLayout, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
            super(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes);
            screenSize = Helper.getScreenSize(getApplicationContext());
            drawerWidth = getResources().getDimensionPixelSize(R.dimen.drawer_width);
        }

        @Override
        public void onDrawerSlide(View view, float offset) {
            super.onDrawerSlide(view, offset);
            updateActionButtonPosition(offset);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            actionButton.animate().translationY(0).start();
        }

        @Override
        public void syncState() {
            super.syncState();
            updateActionButtonPosition(drawerLayout.isDrawerOpen(GravityCompat.START) ? 1 : 0);
        }

        private void updateActionButtonPosition(float offset) {
            actionButton.setTranslationX((screenSize.x - drawerWidth) * -offset);
        }
    }
}
