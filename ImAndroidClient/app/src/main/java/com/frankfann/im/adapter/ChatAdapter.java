package com.frankfann.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frankfann.im.R;
import com.frankfann.im.entity.Chat;

import java.util.List;

public class ChatAdapter extends BaseListAdapter<Chat> {
    public ChatAdapter(Context context, List<Chat> values) {
        super(context, values);
    }
    @Override
    protected View getItemView(View convertView, int position) {

        final Chat chat = mValues.get(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.chat_item, null);
            viewHolder = new ViewHolder();
            viewHolder.leftLayout = (LinearLayout) view.findViewById(R.id.left_layout);
            viewHolder.rightLayout = (LinearLayout) view.findViewById(R.id.right_layout);
            viewHolder.leftMsg = (TextView) view.findViewById(R.id.left_msg);
            viewHolder.rightMsg = (TextView) view.findViewById(R.id.right_msg);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if (Chat.SEND.equals(chat.receivedorsend )) {//发送消息
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightMsg.setText(chat.message);
        } else if(Chat.RECEIVED.equals(chat.receivedorsend) ) {//接收消息
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.leftMsg.setText(chat.message);
        }
        return view;
    }
    class ViewHolder {

        LinearLayout leftLayout;

        LinearLayout rightLayout;

        TextView leftMsg;

        TextView rightMsg;

    }

}
