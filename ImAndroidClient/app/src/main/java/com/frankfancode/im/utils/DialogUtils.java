package com.frankfancode.im.utils;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.frankfancode.im.ImAPPlication;
import com.frankfancode.im.R;

/**
 * Created by Frank on 2016/1/25.
 */
public class DialogUtils {
    public static Dialog mProcessdialog;

    public static Dialog showProcessDialog(Activity activity, String promptText) {
        if (null == mProcessdialog) {
            mProcessdialog = new Dialog(activity, R.style.TopDialog);
            mProcessdialog.setContentView(R.layout.dialog_top);

            Window window = mProcessdialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            // 获取和mLoginingDlg关联的当前窗口的属性，从而设置它在屏幕中显示的位置

            // 获取屏幕的高宽
            DisplayMetrics dm = new DisplayMetrics();
            Application a = ImAPPlication.getSelf();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            int cxScreen = dm.widthPixels;
            int cyScreen = dm.heightPixels;

            int height = (int) activity.getResources().getDimension(
                    R.dimen.top_dialog_height);// 高42dp
            int lrMargin = (int) activity.getResources().getDimension(
                    R.dimen.top_dialog_lr_margin); // 左右边沿10dp
            int topMargin = (int) activity.getResources().getDimension(
                    R.dimen.top_dialog_top_margin); // 上沿20dp

            params.y = (-(cyScreen - height) / 2) + topMargin; // -199
        /* 对话框默认位置在屏幕中心,所以x,y表示此控件到"屏幕中心"的偏移量 */

            params.width = cxScreen;
            params.height = height;
            // width,height表示mLoginingDlg的实际大小

            mProcessdialog.setCanceledOnTouchOutside(true); // 设置点击Dialog外部任意区域关闭Dialog
        }
        TextView tv_process = (TextView) mProcessdialog.findViewById(R.id.tv_process);
        tv_process.setText(promptText);
        if (null!=activity&&!activity.isFinishing()){
            mProcessdialog.show();
        }

        return mProcessdialog;
    }


    /**
     * 消掉 dialog
     */
    public static void dismissProcessDialog() {
        dismissProcessDialog(mProcessdialog);
    }

    /**
     * 消掉 dialog
     */
    public static void dismissProcessDialog(Dialog dialog) {
        if (null != dialog && dialog.isShowing()) {
            dialog.dismiss();
        } else {

        }
    }

}
