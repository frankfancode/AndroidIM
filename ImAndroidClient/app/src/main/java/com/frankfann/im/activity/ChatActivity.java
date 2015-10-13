package com.frankfann.im.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.frankfann.im.APP;
import com.frankfann.im.R;
import com.frankfann.im.adapter.ChatAdapter;
import com.frankfann.im.database.ChatDbManager;
import com.frankfann.im.entity.AppConstants;
import com.frankfann.im.entity.Chat;
import com.frankfann.im.entity.Contact;
import com.frankfann.im.service.ChatService;
import com.frankfann.im.utils.ChatCommand;
import com.frankfann.im.utils.Log;
import com.frankfann.im.utils.StringUtils;
import com.frankfann.im.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ChatActivity extends BaseActivity {
    private Activity activity = this;
    private Dialog dialog;
    private Contact toContact;

    private int PAGE_INDEX=1;
    private long _id=-1;
    private boolean hasOldChat=true;

    private List<Chat> chatlist = new ArrayList<Chat>();
    private ChatAdapter chatadapter;
    private ChatDbManager chatDbManager;

    private ListView lvmsg;
    private LinearLayout barBottom;
    private LinearLayout rlBottom;
    private Button btnSetModeVoice;
    private Button btnSetModeKeyboard;
    private LinearLayout btnPressToSpeak;
    private RelativeLayout edittextLayout;
    private EditText etTextMessage;
    private ImageView ivEmoticonsNormal;
    private ImageView ivEmoticonsChecked;
    private Button btnMore;
    private Button btnSend;
    private LinearLayout more;
    private LinearLayout llFaceContainer;
    private ViewPager vPager;
    private LinearLayout llBtnContainer;
    private ImageView btnTakePicture;
    private ImageView btnPicture;
    private ImageView btnLocation;
    private ImageView btnVideo;
    private ImageView btnFile;
    private LinearLayout containerVoiceCall;
    private ImageView btnVoiceCall;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_chat);
        assignViews();
        registerListener();
        initIntentData();
        initdata();
        if (StringUtils.isNullOrEmpty(toContact.userid)){
            pairSomeone();
        }
    }

    private void initIntentData() {
        toContact=(Contact)getIntent().getSerializableExtra("tocontact");
        setTitle(toContact.username);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        toContact=(Contact)getIntent().getSerializableExtra("tocontact");
        setTitle(toContact.username);
    }

    private void assignViews() {
        lvmsg = (ListView) findViewById(R.id.lv_msg );
        barBottom = (LinearLayout) findViewById(R.id.bar_bottom);
        rlBottom = (LinearLayout) findViewById(R.id.rl_bottom);
        btnSetModeVoice = (Button) findViewById(R.id.btn_set_mode_voice);
        btnSetModeKeyboard = (Button) findViewById(R.id.btn_set_mode_keyboard);
        btnPressToSpeak = (LinearLayout) findViewById(R.id.btn_press_to_speak);
        edittextLayout = (RelativeLayout) findViewById(R.id.edittext_layout);
        etTextMessage = (EditText) findViewById(R.id.et_text_message);
        ivEmoticonsNormal = (ImageView) findViewById(R.id.iv_emoticons_normal);
        ivEmoticonsChecked = (ImageView) findViewById(R.id.iv_emoticons_checked);
        btnMore = (Button) findViewById(R.id.btn_more);
        btnSend = (Button) findViewById(R.id.btn_send);
        more = (LinearLayout) findViewById(R.id.more);
        llFaceContainer = (LinearLayout) findViewById(R.id.ll_face_container);
        vPager = (ViewPager) findViewById(R.id.vPager);
        llBtnContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
        btnTakePicture = (ImageView) findViewById(R.id.btn_take_picture);
        btnPicture = (ImageView) findViewById(R.id.btn_picture);
        btnLocation = (ImageView) findViewById(R.id.btn_location);
        btnVideo = (ImageView) findViewById(R.id.btn_video);
        btnFile = (ImageView) findViewById(R.id.btn_file);
        containerVoiceCall = (LinearLayout) findViewById(R.id.container_voice_call);
        btnVoiceCall = (ImageView) findViewById(R.id.btn_voice_call);
    }

    private void registerListener() {
        btnSend.setOnClickListener(clickListener);
    }
    private void initdata() {
        chatDbManager=new ChatDbManager(activity);
        chatadapter=new ChatAdapter(activity,chatlist);
        lvmsg.setAdapter(chatadapter);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_send:
                    String textMessage = etTextMessage.getText().toString().trim();
                    if (StringUtils.isNullOrEmpty(textMessage)) {
                        Utils.toast(mContext, "不能发送空消息！");
                        return;
                    }
                    sendMessage(textMessage, ChatCommand.TEXT, null);
                    etTextMessage.setText("");
                    break;
            }
        }
    };


    private void sendMessage(String textMessage, String command, String localDataPath) {

        if (ChatCommand.TEXT.equals(command)) {
            Chat c = new Chat();
            c.fromwho = APP.getSelf().getUserInfo().userid;
            c.sendto = toContact.userid;
            c.message = textMessage;
            c.command = command;
            c.localdatapath = localDataPath;
            c.receivedorsend=Chat.SEND;
            ChatService.sendMessage(c.toJson());
            chatlist.add(c);
            chatadapter.update(chatlist);
            lvmsg.smoothScrollToPosition(lvmsg.getCount()-1);
        }

        //HashMap<String, String> map = assembleChatMap(c);;

        /*ChatService.sendMessage(ChatUtils.getJsonFromMap(map));*/
    }

    /*private HashMap<String,String> assembleChatMap(final Chat c){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("command",c.command);
        map.put("from",c.from);
        map.put("message",c.message);
        map.put("messageKey",c.messagekey);
        map.put("sendto",c.sendto);
        return map;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void pairSomeone() {
        dialog = Utils.showProcessDialog(activity, getString(R.string.pair_someone));
        try {
            /*HashMap<String,String> map = new HashMap<String, String>();
            map.put("command", ChatCommand.PAIR_SOMEONE_RANDOM);
            map.put("from", APP.getSelf().getUserInfo().userid);
            String messagekey= UUID.randomUUID().toString();
            map.put("messagekey", messagekey);
            ChatService.sendMessage(ChatUtils.getJsonFromMap(map));*/

            Chat chatRequest = new Chat();
            chatRequest.command = ChatCommand.PAIR_SOMEONE_RANDOM;
            chatRequest.fromwho = APP.getSelf().getUserInfo().userid;
            chatRequest.messagekey = UUID.randomUUID().toString();
            ChatService.sendMessage(chatRequest.toJson());
            APP.getSelf().getEBus().register(mContext, ChatCommand.PAIR_SOMEONE_RANDOM, chatRequest.messagekey);
        } catch (Exception e) {
            if (null != dialog) {
                dialog.dismiss();
            }

        }
    }

    public void pairsomeonerandomStart(String string) {
        if (null != dialog) {
            dialog.dismiss();
        }
        Log.e("ebus", "Start=====" + string);

        Chat chat = new Chat().ChatFromJson(string);
        if (null != chat) {

            if ("1".equals(chat.resultcode)) {
                toContact.userid = chat.message;

            } else {
                Message msg = new Message();
                msg.what = JUST_TOAST;
                msg.obj = chat.message;
                handler.sendMessage(msg);
            }

        }


    }

    public void pairsomeonerandomEnd(String string) {
        Log.e("ebus", "End=====" + string);
        if (null != dialog) {
            dialog.dismiss();
        }
        Utils.toast(activity, "匹配失败");

    }


    public static final int JUST_TOAST = 0;
    public static final int REFRESH = 1;
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JUST_TOAST:
                    String message = (String) msg.obj;
                    Utils.toast(activity, message);
                    break;
                case REFRESH:
                    Chat msgC= (Chat) msg.obj;
                    Chat newC=chatDbManager.getLastChat(msgC);
                    if (null!=newC){
                        chatlist.add(newC);

                        int scrolledX = lvmsg.getScrollX();
                        int scrolledY = lvmsg.getScrollY();
                        chatadapter.update(chatlist);
                        if (lvmsg.getLastVisiblePosition()>=lvmsg.getCount()-2){//如果当前展示的位置是最后一个或最后第二个，说明滑到了最底部，那么有新消息来时就滑到最后一条
                            lvmsg.smoothScrollToPosition(lvmsg.getCount()-1);
                        }else {
                            lvmsg.scrollTo(scrolledX, scrolledY);
                        }

                    }
                    break;
            }


        }

    };

    public void refreshNewChat(Chat chat){
        if (StringUtils.equals(toContact.userid,chat.fromwho)) {
            Message msg = new Message();
            msg.what = REFRESH;
            msg.obj = chat;
            handler.sendMessage(msg);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ChatService.chatActivity = this;
        //每次进入界面都要获取一下聊天记录啊
        getChatList();
        
    }

    private void getChatList() {

        if (1==PAGE_INDEX&&chatlist.size()==0){
            _id=0;
            List<Chat> tempList= chatDbManager.getChatList(toContact.userid, _id);
            if (null==tempList||tempList.size()<AppConstants.PAGE_SIZE){
                hasOldChat=false;
            }else{
                hasOldChat=true;
            }
            chatlist.addAll(0,tempList);
        }else if (1==PAGE_INDEX&&chatlist.size()>0){
            //去取界面不显示时更新的表数据
            long new_id=chatlist.get(chatlist.size()-1)._id;
            chatlist.addAll(chatlist.size(), chatDbManager.getNewChatList(toContact.userid, new_id));
        }else if(PAGE_INDEX>1){
            if (chatlist.get(0)._id==_id){

            }
        }




    }

    @Override
    protected void onPause() {
        super.onPause();
        ChatService.chatActivity=null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChatService.chatActivity=null;
    }
}
