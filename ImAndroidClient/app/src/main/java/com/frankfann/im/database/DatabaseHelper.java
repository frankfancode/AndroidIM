package com.frankfann.im.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Frank on 2015/9/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "database";
    private static final String DATABASE_NAME = "chat.db";
    //数据库版本
    private static final int DATABASE_VERSION = 1;


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS"
        +ChatDbManager.chatTable
        + " (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
        + " command varchar(50),"


        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
