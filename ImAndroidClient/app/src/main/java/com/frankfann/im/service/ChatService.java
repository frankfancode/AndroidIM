package com.frankfann.im.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.frankfann.im.chatmanager.WebSocketClient;
import com.frankfann.im.entity.Chat;
import com.frankfann.im.utils.Log;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * Created by user on 2015/9/1.
 */
public class ChatService extends Service {
//    private String urls="wss://104.207.155.166:8887";
    private String url="ws://104.207.155.166:8887";
    //private String url="ws://192.168.1.112:8887";

    private String TAG="chatmessage";
    private boolean isClose=false;
    public static WebSocketClient client;
    private final int ALIVE_INTERVAL_TIME=8*1000;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate(){
        super.onCreate();
        Log.e(TAG,"onCreate");
        isClose=false;
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        isClose=true;
        Log.e(TAG,"onDestroy");
    }

    private void init() {
        Log.e(TAG, "start init");
        if (isClose) {
            stopSelf();
            return;
        }
        initConnect();
    }

    private void initConnect() {
        Log.e(TAG, "start initConnect");
        List<BasicNameValuePair> extraHeaders = Arrays.asList(
                new BasicNameValuePair("Cookie", "session=abcd")
        );

        HashMap<String ,String> extraHeadersMap=new HashMap<String,String>();
        extraHeadersMap.put("Cookie", "session=abcd");

        client = new WebSocketClient(URI.create(url), new WebSocketClient.Listener() {
            @Override
            public void onConnect() {
                Log.e(TAG, "Connected!");

                //发送心跳包
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (!isClose){
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
                Log.e(TAG, String.format("Got string message! %s", message));
                if (TextUtils.isEmpty(message)){

                }else{

                    try {
                        Chat chat=new Chat(message);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                    }
                }



            }

            @Override
            public void onMessage(byte[] data) {
                //Log.e(TAG, String.format("Got binary message! %s", toHexString(data));
                Log.e(TAG, String.format("%02x", data));

            }

            @Override
            public void onDisconnect(int code, String reason) {
                Log.e(TAG, String.format("Disconnected! Code: %d Reason: %s", code, reason));
                stopSelf();
            }

            @Override
            public void onError(Exception error) {
                Log.e(TAG, "Error!", error);
                stopSelf();
            }
        }, extraHeadersMap);

        client.connect();

        //client.send("hello!");
        //client.disconnect();
    }
}
