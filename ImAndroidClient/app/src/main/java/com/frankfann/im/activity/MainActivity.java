package com.frankfann.im.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.frankfann.im.client.R;
import com.frankfann.im.service.ChatService;

public class MainActivity extends Activity {
    private Activity activity = MainActivity.this;

    private EditText et_sendmessage;
    private Button bt_start_service, bt_stop_service, btn_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        registerListener();

    }

    private void registerListener() {
        bt_start_service.setOnClickListener(clickListener);
        bt_stop_service.setOnClickListener(clickListener);
        btn_send.setOnClickListener(clickListener);
    }

    private void initViews() {
        bt_start_service = (Button) findViewById(R.id.bt_start_service);
        bt_stop_service = (Button) findViewById(R.id.bt_stop_service);
        et_sendmessage = (EditText) findViewById(R.id.et_sendmessage);
        btn_send = (Button) findViewById(R.id.btn_send);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, ChatService.class);
            switch (v.getId()) {
                case R.id.bt_start_service:
                    try {
                        startService(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case R.id.bt_stop_service:
                    stopService(intent);
                    break;
                case R.id.btn_send:
                    String message=et_sendmessage.getText().toString();
                    ChatService.client.send(message);
                    break;
            }


        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
