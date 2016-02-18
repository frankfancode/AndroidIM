package com.frankfancode.im.activity;

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

import com.frankfancode.im.ImAPPlication;
import com.frankfancode.im.R;
import com.frankfancode.im.bean.AppConstants;
import com.frankfancode.im.bean.UserInfo;
import com.frankfancode.im.service.ChatService;
import com.frankfancode.im.utils.DialogUtils;
import com.frankfancode.im.utils.StringUtils;
import com.frankfancode.im.utils.Utils;

import java.util.UUID;

/**
 * Created by Frank on 2015/9/30.
 */
public class LoginAActivity extends BaseActivity {
    private Dialog dialog;
    private ConnectedChatServiceBroadcastReceiver connectedChatServiceBroadcastReceiver;

    private Activity activity = this;
    private EditText etUsername;
    private Button btnSetUsername;
    private String userid=UUID.randomUUID().toString();


    private void assignViews() {
        etUsername = (EditText) findViewById(R.id.et_username);
        btnSetUsername = (Button) findViewById(R.id.btn_set_username);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_logina);
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

                        UserInfo userInfo = new UserInfo();
                        userInfo.username = etUsername.getText().toString();
                        ;
                        userInfo.userid = userid;
                        ImAPPlication.getSelf().setUserInfo(userInfo);

                        Intent intentS = new Intent(activity, ChatService.class);
                        startService(intentS);
                        if (null==dialog||!dialog.isShowing()){
                            dialog = DialogUtils.showProcessDialog(activity, getString(R.string.connecting));
                        }

                    }
                    break;
            }
        }
    };


    public class ConnectedChatServiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            DialogUtils.dismissProcessDialog(dialog);

            if (null != intent && intent.getBooleanExtra("connect", false)) {
                ImAPPlication.getSelf().setAutoLogin(userid);
                Intent intentcl = new Intent(activity, ContactsListActivity.class);
                startActivity(intentcl);
                finish();
            } else {
                ImAPPlication.getSelf().clearUserInfo();
                ImAPPlication.getSelf().clearAutoLogin();
                Utils.toast(activity, getString(R.string.connect_failure));
            }


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogUtils.dismissProcessDialog(dialog);
        unRegisterReceiver();
    }
}
