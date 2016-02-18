package com.frankfancode.im.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frankfancode.im.ImAPPlication;
import com.frankfancode.im.R;
import com.frankfancode.im.database.DatabaseManager;
import com.frankfancode.im.service.ChatService;
import com.frankfancode.im.utils.FLog;
import com.frankfancode.im.utils.StringUtils;


/**
 * Created by Frank on 2015/9/29.
 */


public class MainSplashActivity extends BaseActivity {
    private Activity activity = MainSplashActivity.this;
    private LinearLayout llSplash;
    private TextView tvSlogan;

    private void assignViews() {
        llSplash = (LinearLayout) findViewById(R.id.ll_splash);
        tvSlogan = (TextView) findViewById(R.id.tv_slogan);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        FLog.e(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        assignViews();
        // 透明动画（从完全透明到不透明，分别对应第一个参数和第二个参数）
        AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
        // 动画效果时间为3秒
        animation.setDuration(2 * 1000);
        // 动画监听
        animation.setAnimationListener(new Animation.AnimationListener() {
                                           @Override
                                           public void onAnimationStart(Animation animation) { // 动画开始时执行此方法
                                               
                                               if (!StringUtils.isNullOrEmpty(ImAPPlication.getSelf().getUserInfo().userid)) {
                                                   Intent intentS = new Intent(activity, ChatService.class);
                                                   startService(intentS);
                                               } else {
                                                   DatabaseManager.getInstance(mContext);
                                               }

                                           }

                                           @Override
                                           public void onAnimationRepeat(Animation animation) { // 动画重复调用时执行此方法
                                           }

                                           @Override
                                           public void onAnimationEnd(Animation animation) { // 动画结束时执行此方法
                                               //判断是否已经已登录了
                                               if (!StringUtils.isNullOrEmpty(ImAPPlication.getSelf().getUserInfo().userid)) {
                                                   Intent intent = new Intent(activity, ChatHomeActivity.class);
                                                   startActivity(intent);
                                                   finish();
                                               } else {
                                                   Intent intent = new Intent(activity, LoginActivity.class);
                                                   startActivity(intent);
                                                   finish();
                                               }

                                           }

                                       }

        );
        tvSlogan.startAnimation(animation);

    }
}
