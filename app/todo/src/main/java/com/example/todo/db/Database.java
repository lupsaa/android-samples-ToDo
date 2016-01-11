package com.example.todo.db;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.todo.R;
import com.example.todo.db.beans.DaoMaster;
import com.example.todo.db.beans.DaoSession;
import com.example.todo.db.beans.Section;
import com.example.todo.db.beans.SectionDao;
import com.example.todo.db.beans.ToDoItem;
import com.example.todo.db.beans.ToDoItemDao;
import com.example.todo.util.Event;
import com.example.todo.util.Event.EventType;

import java.util.List;
import java.util.concurrent.Callable;

import de.greenrobot.dao.async.AsyncOperation;
import de.greenrobot.dao.async.AsyncOperationListener;
import de.greenrobot.dao.async.AsyncSession;

public class Database {

    private static final String TAG = "Database";

    public static final String DEFAULT_DB_NAME = "todo_db";

    private static Database defaultInstance;

    private Context context;
    private SQLiteDatabase db;
    private DaoSession dbSession;

    private SectionHandler sectionHandler;
    private ToDoHandler toDoHandler;

    public Database(Context applicationContext, String dbName) {
        if (context instanceof Activity) {
            throw new IllegalArgumentException("Provide an application context when initializing the database");
        }
        context = applicationContext;
        db = new DbOpenHelper(context, dbName).getWritableDatabase();
        dbSession = new DaoMaster(db).newSession();
        sectionHandler = new SectionHandler();
        toDoHandler = new ToDoHandler();
    }

    public static Database getDefault() {
        if (defaultInstance == null) {
            Log.e(TAG, "getDefault: Default database not initialized. Make sure to call initDefault(context) before anything else");
            return null;
        }
        return defaultInstance;
    }

    public static void initDefault(Context context) {
        if (defaultInstance == null) {
            synchronized (Database.class) {
                if (defaultInstance == null) {
                    defaultInstance = new Database(context, DEFAULT_DB_NAME);
                }
            }
        }
    }

    public SectionHandler getSectionHandler() {
        return sectionHandler;
    }

    public ToDoHandler getToDoHandler() {
        return toDoHandler;
    }

    public void populateDatabase(final int sections, final int toDoItemsInEachSection) {
        startAsyncSessionWithFinalEvent(EventType.DB_SECTION_TABLE_UPDATED).runInTx(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                for (int i = 0; i < sections; i++) {
                    long sectionId = dbSession.insert(new Section(null, i, (context.getString(R.string.page) + " " + (i + 1)), i == 0));
                    for (int j = 0; j < toDoItemsInEachSection; j++) {
                        String title = context.getString(R.string.app_name) + " " + (j + 1);
                        String description = context.getString(R.string.description) + " " + (j + 1);
                        dbSession.insert(new ToDoItem(null, j, title, description, sectionId));
                    }
                }
                Log.i(TAG, "populatingDatabase in: " + (System.currentTimeMillis() - startTime) + " ms");
            }
        });
    }

    // Convenient methods that I found useful

    private AsyncSession startAsyncSession() {
        return dbSession.startAsyncSession();
    }

    private AsyncSession startAsyncSession(AsyncOperationListener listener) {
        return startAsyncSession(listener, true);
    }

    private AsyncSession startAsyncSession(AsyncOperationListener listener, boolean runOnMainThread) {
        AsyncSession session = dbSession.startAsyncSession();
        if (listener != null) {
            if (runOnMainThread) {
                session.setListenerMainThread(listener);
            } else {
                session.setListener(listener);
            }
        }
        return session;
    }

    // This one was especially useful since it fired an EventBUS event after an operation was completed (to update the UI accordingly)
    private AsyncSession startAsyncSessionWithFinalEvent(final EventType type) {
        return startAsyncSession(new AsyncOperationListener() {
            @Override
            public void onAsyncOperationCompleted(AsyncOperation operation) {
                new Event(type).submit();
            }
        });
    }

    public class SectionHandler {

        public void getSectionsOrderedByPosition(AsyncOperationListener listener) {
            startAsyncSession(listener).callInTx(new Callable<List>() {
                @Override
                public List call() throws Exception {
                    return getSectionsOrderedByPosition();
                }
            });
        }

        public void addSection(final Section section) {
            startAsyncSessionWithFinalEvent(EventType.DB_SECTION_TABLE_UPDATED).runInTx(new Runnable() {
                @Override
                public void run() {
                    Section lastSection = getLastSection();
                    if (lastSection != null) {
                        section.setPosition(lastSection.getPosition() + 1);
                    }
                    if (section.getIsSelected()) {
                        clearSectionSelection();
                    }
                    dbSession.insert(section);
                }
            });
        }

        public void selectSection(final Section section) {
            startAsyncSessionWithFinalEvent(EventType.DB_SECTION_TABLE_UPDATED).runInTx(new Runnable() {
                @Override
                public void run() {
                    section.setIsSelected(true);
                    clearSectionSelectionExcept(section);
                    dbSession.update(section);
                }
            });
        }

        public void updateSection(Section section) {
            startAsyncSessionWithFinalEvent(EventType.DB_SECTION_TABLE_UPDATED).update(section);
        }

        public void updateSectionPositions(final List<Section> orderedSectionList) {
            startAsyncSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    int position = 0;
                    for (Section section : orderedSectionList) {
                        if (section.getPosition() != position) {
                            section.setPosition(position);
                            dbSession.update(section);
                        }
                        position++;
                    }
                }
            });
        }

        public void deleteSection(final Section section) {
            startAsyncSessionWithFinalEvent(EventType.DB_SECTION_TABLE_UPDATED).runInTx(new Runnable() {
                @Override
                public void run() {
                    if (section.getIsSelected()) {
                        List<Section> sections = getSectionsOrderedByPosition();
                        int sectionIndex = sections.indexOf(section);

                        Section nextSection = null;
                        if (sectionIndex > 0) {
                            nextSection = sections.get(sectionIndex - 1);
                        } else if (sections.size() > 1) {
                            nextSection = sections.get(1);
                        }

                        if (nextSection != null) {
                            nextSection.setIsSelected(true);
                            dbSession.update(nextSection);
                        }
                    }
                    for (ToDoItem toDoItem : section.getToDoItems()) {
                        dbSession.delete(toDoItem);
                    }
                    dbSession.delete(section);
                }
            });
        }

        private List<Section> getSectionsOrderedByPosition() {
            return dbSession.queryBuilder(Section.class).orderAsc(SectionDao.Properties.Position).list();
        }

        private Section getLastSection() {
            return dbSession.queryBuilder(Section.class).orderDesc(SectionDao.Properties.Position).limit(1).unique();
        }

        private void clearSectionSelection() {
            clearSectionSelectionExcept(null);
        }

        private void clearSectionSelectionExcept(Section exception) {
            for (Section section : dbSession.loadAll(Section.class)) {
                if (section.getIsSelected() && section != exception) {
                    section.setIsSelected(false);
                    dbSession.update(section);
                }
            }
        }
    }

    public class ToDoHandler {

        public void getToDoItemsOrderedByPosition(final long sectionId, AsyncOperationListener listener) {
            startAsyncSession(listener).callInTx(new Callable<List>() {
                @Override
                public List call() throws Exception {
                    return dbSession.queryBuilder(ToDoItem.class).where(ToDoItemDao.Properties.SectionId.eq(sectionId))
                            .orderAsc(ToDoItemDao.Properties.Position).list();
                }
            });
        }

        public void addToDoItem(final ToDoItem toDoItem) {
            startAsyncSessionWithFinalEvent(EventType.DB_TO_DO_ITEM_TABLE_UPDATED).runInTx(new Runnable() {
                @Override
                public void run() {
                    ToDoItem lastToDoItem = getLastToDoItem(toDoItem.getSectionId());
                    if (lastToDoItem != null) {
                        toDoItem.setPosition(lastToDoItem.getPosition() + 1);
                    }
                    dbSession.insert(toDoItem);
                }
            });
        }

        public void updateToDoItem(ToDoItem toDoItem) {
            startAsyncSessionWithFinalEvent(EventType.DB_TO_DO_ITEM_TABLE_UPDATED).update(toDoItem);
        }

        public void updateToDoItemsPositions(final List<ToDoItem> orderedToDoItemList) {
            startAsyncSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    int position = 0;
                    for (ToDoItem toDoItem : orderedToDoItemList) {
                        if (toDoItem.getPosition() != position) {
                            toDoItem.setPosition(position);
                            dbSession.update(toDoItem);
                        }
                        position++;
                    }
                }
            });
        }

        public void deleteToDoItem(final ToDoItem toDoItem, boolean notify) {
            if (notify) {
                startAsyncSessionWithFinalEvent(EventType.DB_TO_DO_ITEM_TABLE_UPDATED).delete(toDoItem);
            } else {
                startAsyncSession().delete(toDoItem);
            }
        }

        private ToDoItem getLastToDoItem(long sectionId) {
            return dbSession.queryBuilder(ToDoItem.class).where(ToDoItemDao.Properties.SectionId.eq(sectionId))
                    .orderDesc(ToDoItemDao.Properties.Position).limit(1).unique();
        }
    }
}
