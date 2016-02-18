package com.frankfancode.im.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Frank on 2015/9/27.
 */
public class Utils {
    public static void toast(Context c, String s) {
        Toast.makeText(c, s, Toast.LENGTH_SHORT).show();
    }


    /**
     * 隐藏软键盘
     *
     * @param activity 要隐藏软键盘的activity
     */
    public static void hideSoftKeyBoard(Activity activity) {
        final View v = activity.getWindow().peekDecorView();
        if (v != null && v.getWindowToken() != null) {
            try {
                ((InputMethodManager) activity
                        .getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(activity.getCurrentFocus()
                                        .getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 模拟按home键
     *
     * @param context
     */
    public static void homeKey(Context context) {
        if (null == context) {
            return;
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            context.startActivity(intent);
        }

    }


    // 将字符串转为时间戳
    public static String getTimeStamp(String time) {
        String timestamp = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d;
        try {

            d = sdf.parse(time);
            long l = d.getTime();
            String str = String.valueOf(l);
            timestamp = str.substring(0, 10);

        } catch (ParseException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        return timestamp;
    }

    // 将时间戳转为字符串
    public static String getStrTime(String timestamp) {
        String re_StrTime = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
// 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(timestamp);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));

        return re_StrTime;

    }


}