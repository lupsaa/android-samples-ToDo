package com.example.todo.db;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.SmallTest;

import com.example.todo.db.beans.Section;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import de.greenrobot.dao.async.AsyncOperation;
import de.greenrobot.dao.async.AsyncOperationListener;

public class DatabaseTest extends AndroidTestCase {

    private static final String TEST_DB_NAME = "todo_db_test";

    private Context context;
    private Database db;
    private CountDownLatch latch;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        context = new RenamingDelegatingContext(getContext(), "test_");
        db = new Database(context, TEST_DB_NAME);
        latch = new CountDownLatch(1);
    }

    @Override
    protected void tearDown() throws Exception {
        context.deleteDatabase(TEST_DB_NAME);
        super.tearDown();
    }

    @SmallTest
    public void testPopulateDb() throws InterruptedException {
        final List<Section> sections = new ArrayList<>();
        db.populateDatabase(3, 5);
        db.getSectionHandler().getSectionsOrderedByPosition(new AsyncOperationListener() {
            @Override
            public void onAsyncOperationCompleted(AsyncOperation operation) {
                sections.addAll((List<Section>) operation.getResult());
                latch.countDown();
            }
        });
        latch.await();
        assertEquals(3, sections.size());
        assertEquals(true, sections.get(0).getIsSelected().booleanValue());
        assertEquals(false, sections.get(1).getIsSelected().booleanValue());
        assertEquals(false, sections.get(2).getIsSelected().booleanValue());
        assertEquals("Page 2", sections.get(1).getName());
        assertEquals("ToDo 5", sections.get(2).getToDoItems().get(4).getTitle());
    }
}
