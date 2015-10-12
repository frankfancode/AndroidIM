package com.frankfann.im.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.frankfann.im.APP;
import com.frankfann.im.R;
import com.frankfann.im.entity.AppConstants;
import com.frankfann.im.entity.UserInfo;
import com.frankfann.im.service.ChatService;
import com.frankfann.im.utils.StringUtils;
import com.frankfann.im.utils.Utils;

import java.util.UUID;

/**
 * Created by Frank on 2015/9/30.
 */
public class LoginActivity extends BaseActivity {
    private Dialog dialog;
    private ConnectedChatServiceBroadcastReceiver connectedChatServiceBroadcastReceiver;

    private Activity activity = this;
    private EditText etUsername;
    private Button btnSetUsername;


    private void assignViews() {
        etUsername = (EditText) findViewById(R.id.et_username);
        btnSetUsername = (Button) findViewById(R.id.btn_set_username);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_login);
        assignViews();
        registerListener();
        registerReceiver();

    }

    private void registerReceiver() {
        connectedChatServiceBroadcastReceiver = new ConnectedChatServiceBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.BC_CONNECT_STATUS);
        activity.registerReceiver(connectedChatServiceBroadcastReceiver, intentFilter);
    }

    private void unRegisterReceiver() {
        activity.unregisterReceiver(connectedChatServiceBroadcastReceiver);
    }

    private void registerListener() {
        btnSetUsername.setOnClickListener(clickListener);
    }


    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_set_username:
                    String username = etUsername.getText().toString();
                    if (StringUtils.isNullOrEmpty(username)) {
                        Utils.toast(mContext, getString(R.string.setusername));
                    } else {
                        Intent intentS = new Intent(activity, ChatService.class);
                        startService(intentS);
                        if (null==dialog||!dialog.isShowing()){
                            dialog = Utils.showProcessDialog(activity, getString(R.string.connecting));
                        }

                    }
                    break;
            }
        }
    };


    public class ConnectedChatServiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Utils.dismissProcessDialog(dialog);

            if (null != intent && intent.getBooleanExtra("connect", false)) {

                UserInfo userInfo = new UserInfo();
                userInfo.username = etUsername.getText().toString();
                ;
                userInfo.userid = UUID.randomUUID().toString();
                APP.getSelf().setUserInfo(userInfo);

                Intent intentcl = new Intent(activity, ContactsListActivity.class);
                startActivity(intentcl);
                finish();
            } else {
                Utils.toast(activity, getString(R.string.connect_failure));
            }


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.dismissProcessDialog(dialog);
        unRegisterReceiver();
    }
}
