package com.frankfancode.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frankfancode.im.R;
import com.frankfancode.im.bean.ChatMoreTypeItem;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * Created by user on 2015/10/15.
 */
public class ChatBottomGridViewAdapter extends BaseAdapter {
    public static int ROW_NUMBER = 2;
    View mParentView;
    List<ChatMoreTypeItem> mDatas;
    Context context;

    int height;

    public ChatBottomGridViewAdapter(View parentView, Context context, List datas, int itemLayoutId) {
        this.context = context;
        mParentView = parentView;
        mDatas = datas;
    }


    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(context).inflate(R.layout.chat_more_type_grid_view_item,null);

        if (height<mParentView.getMeasuredHeight()/ ROW_NUMBER){
            height=mParentView.getMeasuredHeight()/ ROW_NUMBER;
        }
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,height
                );


        if (param.height>0){
            convertView.setLayoutParams(param);
        }


        final LinearLayout ll_chat_bottom_item = (LinearLayout) convertView.findViewById(R.id.ll_chat_bottom_item);
        final ImageView iv_type_icon = (ImageView) convertView.findViewById(R.id.iv_type_icon);
        final TextView tv_type_title = (TextView) convertView.findViewById(R.id.tv_type_title);
        iv_type_icon.setImageResource(mDatas.get(position).chat_bottom_type_icon);
        tv_type_title.setText(mDatas.get(position).chat_bottom_type_title);
        return convertView;
    }
}
