package com.frankfancode.im.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.frankfancode.im.ImAPPlication;
import com.frankfancode.im.R;
import com.frankfancode.im.adapter.BaseListAdapter;
import com.frankfancode.im.bean.AppConstants;
import com.frankfancode.im.bean.Chat;
import com.frankfancode.im.bean.Contact;
import com.frankfancode.im.service.ChatService;
import com.frankfancode.im.utils.ChatCommand;
import com.frankfancode.im.utils.FLog;
import com.frankfancode.im.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Frank on 2015/10/10.
 */
public class ContactsListActivity extends BaseActivity {


    private TextView tv_status;
    private List<Contact> contactList;
    private ListView lvContacts;
    private ContactsListAdapter contactsListAdapter;

    private int cCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_contacts_list);
        assignViews();
        registerListener();
        initData();
        getData();
    }


    private void getData() {
        getOnlineContacts();
    }


    private void assignViews() {
        tv_status=(TextView)findViewById(R.id.tv_status);
        lvContacts = (ListView) findViewById(R.id.lv_contacts);
    }

    private void initData() {
        tv_status.setText("正在获取在线联系人");
        contactList = new ArrayList<Contact>();
        contactsListAdapter = new ContactsListAdapter(mContext, contactList);
        lvContacts.setAdapter(contactsListAdapter);

    }

    private void registerListener() {
        lvContacts.setOnItemClickListener(onItemClickListener);
        tv_status.setOnClickListener(clickListener);

    }

    View.OnClickListener clickListener=new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            tv_status.setText("正在获取在线联系人");
            getData();
        }
    };
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            switch (parent.getId()) {
                case R.id.lv_contacts:
                    Contact c = contactList.get(position);
                    if (null != c) {

                        Intent intent = new Intent(mContext, ChatActivity.class);
                        intent.putExtra("tocontact", c);
                        startActivity(intent);
                    }

                    break;
            }
        }
    };

    private void getOnlineContacts() {
        try {
            Chat chatRequest = new Chat();
            chatRequest.command = ChatCommand.GET_CONTACTS_USERID_RANDOM;
            chatRequest.fromwho = ImAPPlication.getSelf().getUserInfo().userid;
            chatRequest.messagekey = UUID.randomUUID().toString();
            chatRequest.other = AppConstants.PAGE_SIZE.toString();
            ChatService.sendMessage(chatRequest.toJson());
            ImAPPlication.getSelf().getEBus().register(mContext, ChatCommand.GET_CONTACTS_USERID_RANDOM, chatRequest.messagekey);
        } catch (Exception e) {

        }
    }

    public void getcontactsuseridrandomStart(String string) {
        FLog.e("ebus", "Start=====" + string);
        Chat chat = new Chat().ChatFromJson(string);
        if (null == contactList) {
            contactList = new ArrayList<Contact>();
        } else {
            contactList.clear();
        }
        if (null != chat) {

            if ("1".equals(chat.resultcode)) {
                if (!StringUtils.isNullOrEmpty(chat.message)) {

                    try{
                        Type listType = new TypeToken<ArrayList<Contact>>(){}.getType();
                        contactList = new Gson().fromJson(chat.message, listType);
                    }catch (Exception e){
                        e.printStackTrace();
                    }



                    /*String[] userids = chat.message.split(",");
                    if (userids.length > 0) {
                        for (int i = 0; i < userids.length; i++) {
                            if (!StringUtils.isNullOrEmpty(userids[i])) {
                                Contact c = new Contact();
                                c.userid = userids[i];
                                contactList.add(c);
                            }

                        }
                    }*/


                } else {
                    //else 就是没有人
                }

            } else {
            }

        }
        handler.sendEmptyMessage(GET_CONTACTS_DONE);

    }

    public void getcontactsuseridrandomEnd(String string) {
        //contactList.clear();
        handler.sendEmptyMessage(GET_CONTACTS_DONE);
        FLog.e("ebus", "End=====" + string);

    }

    private class ContactsListAdapter extends BaseListAdapter {

        public ContactsListAdapter(Context context, List values) {
            super(context, values);
        }

        @Override
        protected View getItemView(View convertView, int position) {
            final Contact c = (Contact) mValues.get(position);
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.contacts_item, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_userid = (TextView) view.findViewById(R.id.tv_userid);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            if (StringUtils.equals(ImAPPlication.getSelf().getUserInfo().userid, c.userid)) {
                viewHolder.tv_userid.setText("（自己）" + c.username);
            } else {
                viewHolder.tv_userid.setText(c.username);
            }

            return view;
        }

        class ViewHolder {
            TextView tv_userid;
        }
    }

    private static final int GET_CONTACTS_DONE = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_CONTACTS_DONE:
                    cCount=contactList.size();
                    tv_status.setText("目前在线 "+cCount+" 人");
                    contactsListAdapter.update(contactList);
                    break;

            }


        }

    };

}
