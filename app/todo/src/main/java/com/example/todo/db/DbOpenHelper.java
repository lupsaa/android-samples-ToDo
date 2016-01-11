package com.example.todo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.todo.db.beans.DaoMaster.OpenHelper;

public class DbOpenHelper extends OpenHelper {

    // For testing purposes you can extend DaoMaster.DevOpenHelper (to drop all tables on upgrade)

    public DbOpenHelper(Context context, String dbName) {
        super(context, dbName, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade code goes here
    }
}
