package com.frankfancode.im.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.frankfancode.im.R;

import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.PanelLayout;

/**
 * Created by Jacksgong on 15/7/1.
 * <p/>
 * Desc: 适配了 Panel<->Keybord 切换冲突
 */
public class JChattingActivity extends AppCompatActivity {

    private RecyclerView mContentRyv;
    private EditText mSendEdt;
    private PanelLayout mPanelRoot;
    private ImageView plus_iv;

    private void assignViews() {
        mContentRyv = (RecyclerView) findViewById(R.id.content_ryv);
        mSendEdt = (EditText) findViewById(R.id.send_edt);
        mPanelRoot = (PanelLayout) findViewById(R.id.panel_root);

        plus_iv=(ImageView)findViewById(R.id.plus_iv);
        plus_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPlusIv(v);
            }
        });
    }


    public void onClickPlusIv(final View view) {
        if (mPanelRoot.getVisibility() == View.VISIBLE) {
            KeyboardUtil.showKeyboard(mSendEdt);
        } else {
            KeyboardUtil.hideKeyboard(mSendEdt);
            mPanelRoot.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        assignViews();


        //mContentRyv.setLayoutManager(new LinearLayoutManager(this));

/*        mContentRyv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    KeyboardUtil.hideKeyboard(mSendEdt);
                    mPanelRoot.setVisibility(View.GONE);
                }

                return false;
            }
        })*/;
    }



   /* @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP &&
                event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (mPanelRoot.getVisibility() == View.VISIBLE) {
                mPanelRoot.setVisibility(View.GONE);
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }*/

    public void onClickPhoto(final View view) {

    }
}
