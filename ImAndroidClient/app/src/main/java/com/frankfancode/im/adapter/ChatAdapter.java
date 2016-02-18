package com.frankfancode.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.frankfancode.im.R;
import com.frankfancode.im.bean.Chat;
import com.frankfancode.im.widget.CircleImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChatAdapter extends BaseListAdapter<Chat> {
    public ChatAdapter(Context context, List<Chat> values) {
        super(context, values);
    }

    @Override
    protected View getItemView(View convertView, int position) {

        final Chat chat = mValues.get(position);
        View view;
        ViewHolder viewHolder;
        /*if (convertView == null) {*/
        view = LayoutInflater.from(getContext()).inflate(R.layout.chat_item, null);
        viewHolder = new ViewHolder(view);
        //view.setTag(viewHolder);
        /*} else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }*/


        viewHolder.setAllGone();
        if (Chat.SEND.equals(chat.receivedorsend)) {//发送消息
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.ivPortraitRight.setVisibility(View.VISIBLE);
            viewHolder.rightMsg.setVisibility(View.VISIBLE);
            viewHolder.rightMsg.setText(chat.message);


        } else if (Chat.RECEIVED.equals(chat.receivedorsend)) {//接收消息
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.ivPortraitLeft.setVisibility(View.VISIBLE);
            viewHolder.leftMsg.setVisibility(View.VISIBLE);
            viewHolder.leftMsg.setText(chat.message);
        }
        return view;
    }


    static class ViewHolder {
        @Bind(R.id.iv_portrait_left)
        CircleImageView ivPortraitLeft;
        @Bind(R.id.left_msg)
        TextView leftMsg;
        @Bind(R.id.left_layout)
        LinearLayout leftLayout;
        @Bind(R.id.iv_portrait_right)
        CircleImageView ivPortraitRight;
        @Bind(R.id.right_msg)
        TextView rightMsg;
        @Bind(R.id.ll_right)
        LinearLayout llRight;
        @Bind(R.id.right_layout)
        RelativeLayout rightLayout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }


        public void setAllGone(){
            ivPortraitLeft.setVisibility(View.GONE);
            leftMsg.setVisibility(View.GONE);
            leftLayout.setVisibility(View.GONE);
            rightMsg.setVisibility(View.GONE);
            ivPortraitRight.setVisibility(View.GONE);
            rightLayout.setVisibility(View.GONE);

        }
    }
}

