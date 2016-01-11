package com.example.todo.db.beans;

import java.util.List;
import com.example.todo.db.beans.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "SECTION".
 */
public class Section {

    private Long id;
    private Integer position;
    private String name;
    private Boolean isSelected;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient SectionDao myDao;

    private List<ToDoItem> toDoItems;

    public Section() {
    }

    public Section(Long id) {
        this.id = id;
    }

    public Section(Long id, Integer position, String name, Boolean isSelected) {
        this.id = id;
        this.position = position;
        this.name = name;
        this.isSelected = isSelected;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getSectionDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<ToDoItem> getToDoItems() {
        if (toDoItems == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ToDoItemDao targetDao = daoSession.getToDoItemDao();
            List<ToDoItem> toDoItemsNew = targetDao._querySection_ToDoItems(id);
            synchronized (this) {
                if(toDoItems == null) {
                    toDoItems = toDoItemsNew;
                }
            }
        }
        return toDoItems;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetToDoItems() {
        toDoItems = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
