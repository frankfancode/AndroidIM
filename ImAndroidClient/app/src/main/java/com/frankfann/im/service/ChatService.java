package com.frankfann.im.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.frankfann.im.APP;
import com.frankfann.im.activity.ChatActivity;
import com.frankfann.im.chatmanager.WebSocketClient;
import com.frankfann.im.database.ChatDbManager;
import com.frankfann.im.entity.AppConstants;
import com.frankfann.im.entity.Chat;
import com.frankfann.im.utils.Log;

import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * Created by user on 2015/9/1.
 */
public class ChatService extends Service {
    //    private String urls="wss://104.207.155.166:8887";
    private String url="ws://104.207.155.166:8887";
    //private String url = "ws://192.168.1.119:8887";
    //private String url="ws://192.168.113.248:8887";


    private static String TAG = "chatmessage";
    private boolean isClose = false;
    private static boolean isConnected = false;
    public static WebSocketClient client;
    private final int ALIVE_INTERVAL_TIME = 120 * 1000;

    private static ChatDbManager chatDbManager;
    private static ChatService chatservice;

    public static Activity chatActivity ;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
        isClose = false;
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        chatservice=this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isClose = true;
        Log.e(TAG, "onDestroy");
    }

    private void init() {
        Log.e(TAG, "start init");
        if (isClose) {
            stopSelf();
            return;
        }
        chatDbManager=new ChatDbManager(this);
        initConnect();
    }

    private void initConnect()  {
        Log.e(TAG, "start initConnect");
        List<BasicNameValuePair> extraHeaders = Arrays.asList(
                new BasicNameValuePair("Cookie", "session=abcd")
        );

        HashMap<String, String> extraHeadersMap = new HashMap<String, String>();
        extraHeadersMap.put("Cookie", "session=abcd");
        try {

            extraHeadersMap.put("userid", URLEncoder.encode(APP.getSelf().getUserInfo().userid, "utf-8"));
            extraHeadersMap.put("username", URLEncoder.encode(APP.getSelf().getUserInfo().username, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        client = new WebSocketClient(URI.create(url), new WebSocketClient.Listener() {
            @Override
            public void onConnect() {
                isConnected = true;
                Log.e(TAG, "Connected!");

                Intent intent = new Intent();
                intent.setAction(AppConstants.BC_CONNECT_STATUS);
                intent.putExtra("connect",true);
                ChatService.this.sendBroadcast(intent);




                //发送心跳包
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (!isClose) {
                            client.send("t");
                            try {
                                Thread.sleep(ALIVE_INTERVAL_TIME);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }).start();

            }

            @Override
            public void onMessage(String message) {
                isConnected = true;
                Log.e(TAG, String.format("Got string message! %s", message));
                if (TextUtils.isEmpty(message)) {

                } else {

                    try {
                        Chat chat = new Chat().ChatFromJson(message);
                        APP.getSelf().getEBus().startRegister(chat.messagekey, message, chat.command);
                        chatDbManager.insertChat(chat);
                        String foremostActivity = getForemostActivity();
                        if (foremostActivity.indexOf("com.frankfann.im.activity.ChatActivity") > -1){
                            ((ChatActivity)chatActivity).refreshNewChat(chat);
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                    }
                }


            }

            @Override
            public void onMessage(byte[] data) {
                isConnected = true;
                //Log.e(TAG, String.format("Got binary message! %s", toHexString(data));
                Log.e(TAG, String.format("%02x", data));

            }

            @Override
            public void onDisconnect(int code, String reason) {

                isConnected = false;
                Log.e(TAG, String.format("Disconnected! Code: %d Reason: %s", code, reason));

                Intent intent = new Intent();
                intent.setAction(AppConstants.BC_CONNECT_STATUS);
                intent.putExtra("connect",false);
                ChatService.this.sendBroadcast(intent);

                stopSelf();
            }

            @Override
            public void onError(Exception error) {
                isConnected = false;
                Log.e(TAG, "Error!", error);

                Intent intent = new Intent();
                intent.setAction(AppConstants.BC_CONNECT_STATUS);
                intent.putExtra("connect",false);
                ChatService.this.sendBroadcast(intent);

                stopSelf();
            }
        }, extraHeadersMap);

        client.connect();

        //client.send("hello!");
        //client.disconnect();
    }

    public static boolean sendMessage(String s) {

        Log.e(TAG,"send message: "+s);
        try {
            Chat c=new Chat().ChatFromJson(s);
            chatDbManager.insertChat(c);
        }catch (Exception e){

        }
        if (null != client && isConnected) {
            client.send(s);
            return true;
        } else {
            if (null!=chatservice){

            }
            chatservice.initConnect();
            return false;
        }


    }

    public static boolean getConnected(){
        return isConnected;
    }

    /**
     * 获取前端的包名及类名
     *
     * @return
     */
    public String getForemostActivity() {
        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(2);
        ComponentName name = taskInfo.get(0).topActivity;
        String activityName = name.getClassName();
        return activityName;
    }


}
