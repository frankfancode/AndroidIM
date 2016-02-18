package com.frankfancode.im.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.frankfancode.im.R;
import com.frankfancode.im.fragment.SessionListFragment;
import com.orhanobut.logger.Logger;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatHomeActivity extends BaseActivity {


    @Bind(R.id.tv_header_left)
    TextView tv_header_left;
    @Bind(R.id.ll_header_left)
    LinearLayout ll_header_left;
    @Bind(R.id.tv_header_middle)
    TextView tv_header_middle;
    @Bind(R.id.ll_header_middle)
    LinearLayout ll_header_middle;
    @Bind(R.id.tv_header_right)
    TextView tv_header_right;
    @Bind(R.id.ll_header_right)
    LinearLayout ll_header_right;
    @Bind(R.id.header_bar)
    RelativeLayout header_bar;
    @Bind(R.id.ib_bottom_session)
    ImageButton ib_bottom_session;
    @Bind(R.id.ib_bottom_constact)
    ImageButton ib_bottom_constact;
    @Bind(R.id.ib_bottom_deynaimic)
    ImageButton ib_bottom_deynaimic;
    @Bind(R.id.ib_bottom_setting)
    ImageButton ib_bottom_setting;
    @Bind(R.id.fl_content)
    FrameLayout fl_chat_home;


    private View currentButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_home);
        ButterKnife.bind(this);
        setButton(ib_bottom_session);
        initHeader();
        initContent();
        registerListener();

    }

    private void initHeader() {
        tv_header_left.setText("IM");
        tv_header_middle.setText("消息");
        tv_header_right.setText("+");

    }

    private void registerListener() {

    }

    private void initContent() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        SessionListFragment sessionListFragment = new SessionListFragment();
        ft.replace(R.id.fl_content, sessionListFragment);
        ft.commit();
        setButton(ib_bottom_session);

    }


    private void setButton(View v) {
        if (currentButton != null && currentButton.getId() != v.getId()) {
            currentButton.setEnabled(true);
        }
        v.setEnabled(false);
        currentButton = v;
    }

    @OnClick({R.id.ib_bottom_session, R.id.ib_bottom_constact, R.id.ib_bottom_deynaimic, R.id.ib_bottom_setting})
    protected void switchFragment(View v) {
        if (null != v) {
            Logger.e(v.getId() + "");
        }
        setButton(v);
    }


    @OnClick({R.id.ll_header_left, R.id.ll_header_right})
    public void headerClick(View v) {
        if (v.getId() == R.id.ll_header_left) {
        } else if (v.getId() == R.id.ll_header_right) {
            Intent intent = new Intent();
            intent.setClass(mContext, ChatAddFriendActivity.class);
            startActivity(intent);

        }
    }
}
