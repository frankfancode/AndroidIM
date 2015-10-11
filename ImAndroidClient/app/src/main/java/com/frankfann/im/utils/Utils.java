package com.frankfann.im.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.frankfann.im.R;

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
     * 弹出 dialog
     *
     * @param content 显示内容
     */
    public static Dialog showProcessDialog(Context context, String content) {
        Dialog customDialog = new Dialog(context, R.style.Theme_Light_ProcessDialog_Blue);
        customDialog.setContentView(R.layout.process_dialog);
        customDialog.findViewById(R.id.piv_loading_process).setVisibility(View.VISIBLE);
        ((TextView) customDialog.findViewById(R.id.tv_process)).setText(content);
        customDialog.show();
        return customDialog;
    }

    /**
     * 消掉 dialog
     */
    public static void dismissProcessDialog(Dialog dialog) {
        if (null == dialog) {

        } else {
            dialog.dismiss();
        }
    }

    /**
     * 模拟按home键
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
}