package com.example.todo.db.beans;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.example.todo.db.beans.Section;
import com.example.todo.db.beans.ToDoItem;

import com.example.todo.db.beans.SectionDao;
import com.example.todo.db.beans.ToDoItemDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig sectionDaoConfig;
    private final DaoConfig toDoItemDaoConfig;

    private final SectionDao sectionDao;
    private final ToDoItemDao toDoItemDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        sectionDaoConfig = daoConfigMap.get(SectionDao.class).clone();
        sectionDaoConfig.initIdentityScope(type);

        toDoItemDaoConfig = daoConfigMap.get(ToDoItemDao.class).clone();
        toDoItemDaoConfig.initIdentityScope(type);

        sectionDao = new SectionDao(sectionDaoConfig, this);
        toDoItemDao = new ToDoItemDao(toDoItemDaoConfig, this);

        registerDao(Section.class, sectionDao);
        registerDao(ToDoItem.class, toDoItemDao);
    }
    
    public void clear() {
        sectionDaoConfig.getIdentityScope().clear();
        toDoItemDaoConfig.getIdentityScope().clear();
    }

    public SectionDao getSectionDao() {
        return sectionDao;
    }

    public ToDoItemDao getToDoItemDao() {
        return toDoItemDao;
    }

}
