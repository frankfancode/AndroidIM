package com.frankfann.im.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frankfann.im.R;
import com.frankfann.im.entity.ChatMoreTypeItem;
import com.frankfann.im.media.ViewHolder;

import java.util.List;

/**
 * Created by user on 2015/10/15.
 */
public class ChatBottomGridViewAdapter extends CommonAdapter<ChatMoreTypeItem> {
    public ChatBottomGridViewAdapter(Context context, List mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void convert(ViewHolder helper, final ChatMoreTypeItem item) {
        final LinearLayout ll_chat_bottom_item = helper.getView(R.id.ll_chat_bottom_item);
        final ImageView iv_type_icon = helper.getView(R.id.iv_type_icon);
        final TextView tv_type_title = helper.getView(R.id.tv_type_title);
        iv_type_icon.setImageResource(item.chat_bottom_type_icon);
        tv_type_title.setText(item.chat_bottom_type_title);
    }


}
