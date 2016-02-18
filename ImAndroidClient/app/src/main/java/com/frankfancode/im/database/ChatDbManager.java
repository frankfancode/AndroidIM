package com.frankfancode.im.database;

import android.content.Context;
import android.database.Cursor;

import com.frankfancode.im.ImAPPlication;
import com.frankfancode.im.bean.AppConstants;
import com.frankfancode.im.bean.Chat;
import com.frankfancode.im.utils.FLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frank on 2015/9/16.
 */
public class ChatDbManager {
    private DatabaseManager mDBManager;
    public final static String chatTable = "chat";
    private String userid;


    public ChatDbManager(Context context) {
        long time = System.currentTimeMillis();
        mDBManager = DatabaseManager.getInstance(context);
        userid = ImAPPlication.getSelf().getUserInfo().userid;
    }

    public synchronized void insertChat(final Chat chat) {
        chat.userid = userid;
        if (Chat.RECEIVED.equals(chat.receivedorsend)) {

            chat.touserid = chat.fromwho;
        } else {
            chat.touserid = chat.sendto;
        }

        insertChat(chatTable, chat);
    }

    public synchronized void insertChat(final String table, final Chat chat) {
        try {
            mDBManager
                    .open()
                    .execSQL(
                            "INSERT INTO "
                                    + table
                                    + "(command," +
                                    "fromwho," +
                                    "sendto," +
                                    "message," +
                                    "messageid," +
                                    "messagekey," +
                                    "localdatapath," +
                                    "resultcode," +
                                    "receivedorsend," +
                                    "messageservertime," +
                                    "userid," +
                                    "touserid," +
                                    "other" +
                                    ") "

                                    + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)",
                            new Object[]{chat.command, chat.fromwho, chat.sendto, chat.message, chat.messageid, chat.messagekey, chat.localdatapath, chat.resultcode
                                    , chat.receivedorsend
                                    , chat.messageservertime
                                    , chat.userid
                                    , chat.touserid
                                    , chat.other
                            });


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // mDBManager.close();
        }

    }

    /**
     * 获取一个联系人的所有信息
     *
     * @param touserid
     * @param _id
     * @return
     */
    public synchronized List<Chat> getChatList(String touserid, long _id) {
        String sql;
        if (_id < 1) {//没传id就取最新消息
            sql = "select * from  (select * from " + chatTable
                    + " where  userid='" + userid + "'"
                    + " and touserid='" + touserid + "' "
                    + " order by _id desc limit " + AppConstants.PAGE_SIZE
                    + " offset 0) order by _id asc";
        } else {//取得比传入_id更早的消息
            sql = "select * from  (select * from " + chatTable
                    + " where  userid='" + userid + "'"
                    + " and touserid='" + touserid + "' "
                    + " and _id<" + _id
                    + " order by _id desc limit " + AppConstants.PAGE_SIZE
                    + " offset 0) order by _id asc";
        }

        return getChatList(sql);
    }

    /**
     * 获取一个联系人的未加载的所有信息
     *
     * @param touserid
     * @param _id      查找大于_id 的数据，也就是
     * @return
     */
    public synchronized List<Chat> getNewChatList(String touserid, long _id) {
        String sql;

        sql = "select * from  (select * from " + chatTable
                + " where  userid='" + userid + "'"
                + " and touserid='" + touserid + "' "
                + " and _id>" + _id
                + " order by _id desc  limit 200"
                + " offset 0) order by _id asc";


        return getChatList(sql);
    }

    /**
     * @return
     */
    public synchronized Chat getLastChat(Chat c) {
        String sql = "";
        if (null != c) {
            sql = "select *  from " + chatTable
                    + " where  userid='" + userid + "'"
                    + " and touserid='" + c.touserid + "'"
                    + " and messageservertime='" + c.messageservertime + "'"
                    + " and messageid='" + c.messageid + "'"
                    + " and message='" + c.message + "'";


        }

        List<Chat> list = getChatList(sql);
        if (null != list && list.size() >= 1) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public synchronized List<Chat> getChatList(String sql) {
        long time = System.currentTimeMillis();
        FLog.e(DatabaseHelper.TAG, sql);
        Cursor cursor = null;
        List<Chat> list = null;

        try {
            list = new ArrayList<Chat>();
            cursor = mDBManager.open().rawQuery(sql, null);
            if (cursor != null) {

                Type listType = new TypeToken<ArrayList<Chat>>() {
                }.getType();
                list = new Gson().fromJson(cursorToString(cursor), listType);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;

    }

    private String cursorToString(Cursor crs) {
        JSONArray arr = new JSONArray();
        int nColumns = crs.getColumnCount();
        while (crs.moveToNext()) {
            JSONObject row = new JSONObject();
            for (int i = 0; i < nColumns; i++) {
                String colName = crs.getColumnName(i);
                if (colName != null) {
                    String val = "";
                    try {

                        switch (crs.getType(i)) {
                            case Cursor.FIELD_TYPE_BLOB:
                                row.put(colName, crs.getBlob(i).toString());
                                break;
                            case Cursor.FIELD_TYPE_FLOAT:
                                row.put(colName, crs.getDouble(i));
                                break;
                            case Cursor.FIELD_TYPE_INTEGER:
                                row.put(colName, crs.getLong(i));
                                break;
                            case Cursor.FIELD_TYPE_NULL:
                                row.put(colName, null);
                                break;
                            case Cursor.FIELD_TYPE_STRING:
                                row.put(colName, crs.getString(i));
                                break;
                        }
                    } catch (JSONException e) {
                    }
                }


            }
            arr.put(row);
        }


        return arr.toString();
    }
}
