package com.frankfancode.im.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.frankfancode.im.utils.FLog;

/**
 * Created by Frank on 2015/9/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TAG = "database";
    private static final String DB_NAME = "im.db";
    //数据库版本
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            createChat(db);

            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();

        }
        finally {
            db.endTransaction();
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



    private void createChat(SQLiteDatabase db) {
        String chatSql="CREATE TABLE IF NOT EXISTS "
                + ChatDbManager.chatTable
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + " command varchar(100),"
                + " fromwho varchar(100), "
                + " sendto varchar(100), "
                + " username varchar(100), "
                + " tousername varchar(100), "
                + " message varchar(500), "
                + " messageid varchar(200), "
                + " messagekey varchar(200), "
                + " localdatapath varchar(200), "
                + " resultcode varchar(50), "
                + " receivedorsend varchar(50),"
                + " messageservertime varchar(200), "
                + " userid varchar(50), "
                + " touserid varchar(50), "
                + " other varchar(50)"
                +")";
        FLog.e(TAG, chatSql);
        try {
            db.execSQL(chatSql);
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
