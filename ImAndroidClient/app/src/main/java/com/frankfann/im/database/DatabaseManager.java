package com.frankfann.im.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.frankfann.im.utils.Log;

public class DatabaseManager {

    private DatabaseHelper mDatabaseHelper = null;
    private SQLiteDatabase mSqLiteDatabase = null;

    private static DatabaseManager mDbManager;

    private DatabaseManager(Context context) {
        mDatabaseHelper = new DatabaseHelper(context);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
    }

    public static DatabaseManager getInstance(Context context) {
        Log.e("database", "数据库操作=====DatabaseManager getInstance");

        synchronized (DatabaseManager.class) {
            if (mDbManager == null) {
                mDbManager = new DatabaseManager(context);
            }
        }

        return mDbManager;
    }

    public SQLiteDatabase open() {
        if (!mSqLiteDatabase.isOpen()) {
            mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        }
        return mSqLiteDatabase;
    }

    public void close() {
//		if (mSqLiteDatabase != null) {
//			mSqLiteDatabase.close();
//		}
    }
}
