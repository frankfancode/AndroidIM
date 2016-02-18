package com.frankfancode.im.utils;

import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Created by user on 2015/10/16.
 */
public class MyAnimationUtils {


    private static final String TAG = MyAnimationUtils.class.getSimpleName();

    /**
     * 从控件所在位置移动到控件的底部
     *
     * @return
     */


    public static TranslateAnimation moveToViewBottom() {

        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        mHiddenAction.setDuration(500);
        return mHiddenAction;
    }


    /**
     * 从控件的底部移动到控件所在位置
     *
     * @return
     */


    public static TranslateAnimation moveToViewLocation() {

        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mHiddenAction.setDuration(500);
        return mHiddenAction;
    }
}
