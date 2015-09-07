package com.frankfann.im.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.frankfann.im.chatmanager.WebSocketClient;
import com.frankfann.im.utils.Log;

import org.apache.http.message.BasicNameValuePair;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * Created by user on 2015/9/1.
 */
public class ChatService extends Service {
    private String TAG="chatmessage";
    private boolean isClose=false;

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
        List<BasicNameValuePair> extraHeaders = Arrays.asList(
                new BasicNameValuePair("Cookie", "session=abcd")
        );

        HashMap<String ,String> extraHeadersMap=new HashMap<String,String>();
        extraHeadersMap.put("Cookie", "session=abcd");

        WebSocketClient client = new WebSocketClient(URI.create("wss://104.207.155.166:8887"), new WebSocketClient.Listener() {
            @Override
            public void onConnect() {
                Log.d(TAG, "Connected!");
            }

            @Override
            public void onMessage(String message) {
                Log.d(TAG, String.format("Got string message! %s", message));
            }

            @Override
            public void onMessage(byte[] data) {
                //Log.d(TAG, String.format("Got binary message! %s", toHexString(data));
            }

            @Override
            public void onDisconnect(int code, String reason) {
                Log.d(TAG, String.format("Disconnected! Code: %d Reason: %s", code, reason));
            }

            @Override
            public void onError(Exception error) {
                Log.e(TAG, "Error!", error);
            }
        }, extraHeadersMap);

        client.connect();

        //client.send("hello!");
        //client.disconnect();
    }
}
