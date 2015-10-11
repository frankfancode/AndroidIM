package com.frankfann.im.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.frankfann.im.R;
import com.frankfann.im.utils.Log;
import com.frankfann.im.utils.Utils;
import com.frankfann.im.widget.BaseLayout;

;

public abstract class BaseActivity extends Activity implements
        View.OnClickListener {

    public BaseActivity() {
        mContext = this;
    }

    protected Context mContext;

    protected BaseLayout baseLayout;

    // protected SwipeBackLayout mSwipeBackLayout;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getParent() != null) {
                getParent().onKeyDown(keyCode, event);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 设置布局
     *
     * @param layoutResId 布局id
     */
    protected void setView(int layoutResId) {
        baseLayout = new BaseLayout(this, layoutResId);
        setContentView(baseLayout);
        baseLayout.ll_header_left.setOnClickListener(this);
        baseLayout.ll_header_right.setOnClickListener(this);
    }


    protected void setTitle(String title) {
        baseLayout.setTitle(title);
    }

    protected void setRight1(String title) {
        baseLayout.setRight1(title);
    }

    protected void setRight1Drawable(int id) {
        baseLayout.setRight1Drawable(id);
    }

    protected void setLeft(String left) {
        baseLayout.setLeft(left);
    }

    protected void setRight1TextColor(int color) {
        baseLayout.setRight1TextColor(color);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (mContext == this) {
            super.onCreate(savedInstanceState);
        }
        //StartBroadcastReceiver();
        Log.e("nameOfActivity", getClass().getSimpleName());
        // mSwipeBackLayout = getSwipeBackLayout();
        // mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
    }

    private void StartBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("finish");
        registerReceiver(mFinishReceiver, filter);
    }

    private BroadcastReceiver mFinishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("finish".equals(intent.getAction())) {
                finish();
            }
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    /**
     * 检查软件版本更新信息
     *
     * @return
     */
    protected void checkForUpDate() {
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_header_left:
                if (false) {
                    handleHeaderEventleft();
                } else {
                    finish();
                }

                break;
            case R.id.ll_header_right:
                handleHeaderEventRight(baseLayout.ll_header_right);
                break;
            default:
                break;
        }
    }

    protected void handleHeaderEventleft() {

    }

    protected void handleHeaderEventRight(View view) {
        Utils.homeKey(mContext);
    }

}
