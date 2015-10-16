package com.frankfann.im.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.frankfann.im.APP;
import com.frankfann.im.R;
import com.frankfann.im.adapter.ChatAdapter;
import com.frankfann.im.adapter.ChatBottomGridViewAdapter;
import com.frankfann.im.adapter.GridViewPagerAdapter;
import com.frankfann.im.database.ChatDbManager;
import com.frankfann.im.entity.AppConstants;
import com.frankfann.im.entity.Chat;
import com.frankfann.im.entity.ChatMoreTypeItem;
import com.frankfann.im.entity.Contact;
import com.frankfann.im.service.ChatService;
import com.frankfann.im.utils.ChatCommand;
import com.frankfann.im.utils.Log;
import com.frankfann.im.utils.MyAnimationUtils;
import com.frankfann.im.utils.StringUtils;
import com.frankfann.im.utils.Utils;
import com.frankfann.im.widget.FixedGridView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ChatActivity extends BaseActivity {
    private Activity activity = this;
    private Dialog dialog;
    private Contact toContact;

    private int PAGE_INDEX = 1;
    private long top_id = -1;//列表最上一条chat 的_id，用于获取老聊天记录时，查找小于 top_id 的chat
    private long bottom_id = Long.MAX_VALUE;//列表最下一条chat的_id，用于获取新聊天记录时，查找大于 top_id 的chat
    private boolean hasOldChat = true;

    /**
     * 底部更多种消息类型用到的变量
     */
    private int index = 0;
    private List<GridView> gvList;
    private GridViewPagerAdapter gvpAdapter;
    private List<ChatMoreTypeItem> moreTypeList;


    private List<Chat> chatlist = new ArrayList<Chat>();
    private ChatAdapter chatadapter;
    private ChatDbManager chatDbManager;

    private ListView lvmsg;
    private SwipeRefreshLayout srlmsg;
    private ViewPager vpChatBottom;
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
    private LinearLayout llMore;
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
        initData();
        if (StringUtils.isNullOrEmpty(toContact.userid)) {
            pairSomeone();
        }
    }

    private void initIntentData() {
        toContact = (Contact) getIntent().getSerializableExtra("tocontact");
        setTitle(toContact.username);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        toContact = (Contact) getIntent().getSerializableExtra("tocontact");
        setTitle(toContact.username);
    }

    private void assignViews() {
        srlmsg = (SwipeRefreshLayout) findViewById(R.id.srl_msg);
        vpChatBottom = (ViewPager) findViewById(R.id.vp_chat_bottom);
        srlmsg.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        lvmsg = (ListView) findViewById(R.id.lv_msg);
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
        llMore = (LinearLayout) findViewById(R.id.ll_more);
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
        lvmsg.setOnScrollListener(onScrollListener);
        srlmsg.setOnRefreshListener(refreshListener);
        btnMore.setOnClickListener(clickListener);
        vpChatBottom.addOnPageChangeListener(pageChangeListener);
        etTextMessage.setOnClickListener(clickListener);
        etTextMessage.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
    }

    private void initData() {
        chatDbManager = new ChatDbManager(activity);
        chatadapter = new ChatAdapter(activity, chatlist);
        lvmsg.setAdapter(chatadapter);
        initMoreType();
        initGridView();
        gvpAdapter = new GridViewPagerAdapter(this, gvList);
        vpChatBottom.setAdapter(gvpAdapter);
        gvpAdapter.notifyDataSetChanged();
    }


    ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener=new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {

        }
    };

    private void initGridView() {
        gvList=new ArrayList<>();
        final int PageCount = (int) Math.ceil(moreTypeList.size()/8.0f);
        for (int i = 0; i < PageCount; i++) {
            FixedGridView gv = new FixedGridView(this);
            ViewGroup.LayoutParams gvvglp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            gv.setLayoutParams(gvvglp);

            List<ChatMoreTypeItem> imoreTypeList=null;
            if (moreTypeList.size()>i*8){
                imoreTypeList=moreTypeList.subList(i*8,Math.min(i*8+8,moreTypeList.size()));
            }
            if (null==imoreTypeList){
                return;
            }
            gv.setAdapter(new ChatBottomGridViewAdapter(activity, imoreTypeList, R.layout.chat_more_type_grid_view_item));
            gv.setGravity(Gravity.CENTER);
            gv.setClickable(true);
            gv.setFocusable(true);
            gv.setNumColumns(4);

            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {

                    Intent pickImageIntent = new Intent(activity, ImagePickerActivity.class);
                    startActivityForResult(pickImageIntent, PICK_IMAGE);

                }
            });
            gvList.add(gv);
        }
    }

    private void initMoreType() {
        moreTypeList=new ArrayList<>();
        ChatMoreTypeItem moreTypeItem=new ChatMoreTypeItem();
        //拍照
        moreTypeItem.chat_bottom_type_id= ChatMoreTypeItem.Type.CAPTURE;
        moreTypeItem.chat_bottom_type_icon=R.drawable.icon_chat_capture;
        moreTypeItem.chat_bottom_type_title="拍照";
        moreTypeList.add(moreTypeItem);

        moreTypeItem=new ChatMoreTypeItem();
        //选择图片
        moreTypeItem.chat_bottom_type_id= ChatMoreTypeItem.Type.PICKIMAGE;
        moreTypeItem.chat_bottom_type_icon=R.drawable.icon_chat_image;
        moreTypeItem.chat_bottom_type_title="图片";
        moreTypeList.add(moreTypeItem);
        moreTypeItem=new ChatMoreTypeItem();
        //选择图片
        moreTypeItem.chat_bottom_type_id= ChatMoreTypeItem.Type.PICKIMAGE;
        moreTypeItem.chat_bottom_type_icon=R.drawable.icon_chat_image;
        moreTypeItem.chat_bottom_type_title="图片";
        moreTypeList.add(moreTypeItem);
        moreTypeItem=new ChatMoreTypeItem();
        //选择图片
        moreTypeItem.chat_bottom_type_id= ChatMoreTypeItem.Type.PICKIMAGE;
        moreTypeItem.chat_bottom_type_icon=R.drawable.icon_chat_image;
        moreTypeItem.chat_bottom_type_title="图片";
        moreTypeList.add(moreTypeItem);
        moreTypeItem=new ChatMoreTypeItem();
        //选择图片
        moreTypeItem.chat_bottom_type_id= ChatMoreTypeItem.Type.PICKIMAGE;
        moreTypeItem.chat_bottom_type_icon=R.drawable.icon_chat_image;
        moreTypeItem.chat_bottom_type_title="图片";
        moreTypeList.add(moreTypeItem);
        moreTypeItem=new ChatMoreTypeItem();

        //选择图片
        moreTypeItem.chat_bottom_type_id= ChatMoreTypeItem.Type.PICKIMAGE;
        moreTypeItem.chat_bottom_type_icon=R.drawable.icon_chat_image;
        moreTypeItem.chat_bottom_type_title="图片";
        moreTypeList.add(moreTypeItem);
        moreTypeItem=new ChatMoreTypeItem();
        //选择图片
        moreTypeItem.chat_bottom_type_id= ChatMoreTypeItem.Type.PICKIMAGE;
        moreTypeItem.chat_bottom_type_icon=R.drawable.icon_chat_image;
        moreTypeItem.chat_bottom_type_title="图片";
        moreTypeList.add(moreTypeItem);
        moreTypeItem=new ChatMoreTypeItem();
        //选择图片
        moreTypeItem.chat_bottom_type_id= ChatMoreTypeItem.Type.PICKIMAGE;
        moreTypeItem.chat_bottom_type_icon=R.drawable.icon_chat_image;
        moreTypeItem.chat_bottom_type_title="图片";
        moreTypeList.add(moreTypeItem);
        moreTypeItem=new ChatMoreTypeItem();
        //选择图片
        moreTypeItem.chat_bottom_type_id= ChatMoreTypeItem.Type.PICKIMAGE;
        moreTypeItem.chat_bottom_type_icon=R.drawable.icon_chat_image;
        moreTypeItem.chat_bottom_type_title="图片";
        moreTypeList.add(moreTypeItem);
        moreTypeItem=new ChatMoreTypeItem();
        //选择图片
        moreTypeItem.chat_bottom_type_id= ChatMoreTypeItem.Type.PICKIMAGE;
        moreTypeItem.chat_bottom_type_icon=R.drawable.icon_chat_image;
        moreTypeItem.chat_bottom_type_title="图片";
        moreTypeList.add(moreTypeItem);
        moreTypeItem=new ChatMoreTypeItem();
        //选择图片
        moreTypeItem.chat_bottom_type_id= ChatMoreTypeItem.Type.PICKIMAGE;
        moreTypeItem.chat_bottom_type_icon=R.drawable.icon_chat_image;
        moreTypeItem.chat_bottom_type_title="图片";
        moreTypeList.add(moreTypeItem);
        moreTypeItem=new ChatMoreTypeItem();
        //选择图片
        moreTypeItem.chat_bottom_type_id= ChatMoreTypeItem.Type.PICKIMAGE;
        moreTypeItem.chat_bottom_type_icon=R.drawable.icon_chat_image;
        moreTypeItem.chat_bottom_type_title="图片";
        moreTypeList.add(moreTypeItem);
        moreTypeItem=new ChatMoreTypeItem();
        //选择图片
        moreTypeItem.chat_bottom_type_id= ChatMoreTypeItem.Type.PICKIMAGE;
        moreTypeItem.chat_bottom_type_icon=R.drawable.icon_chat_image;
        moreTypeItem.chat_bottom_type_title="图片";
        moreTypeList.add(moreTypeItem);
        moreTypeItem=new ChatMoreTypeItem();

        //选择图片
        moreTypeItem.chat_bottom_type_id= ChatMoreTypeItem.Type.PICKIMAGE;
        moreTypeItem.chat_bottom_type_icon=R.drawable.icon_chat_image;
        moreTypeItem.chat_bottom_type_title="图片";
        moreTypeList.add(moreTypeItem);


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
                case  R.id.et_text_message:
                    setBottomVisiable(View.GONE);
                    break;
                case R.id.btn_more:
                    int scrolledX = lvmsg.getScrollX();
                    int scrolledY = lvmsg.getScrollY();
                    if (View.VISIBLE==llMore.getVisibility()){
                        setBottomVisiable(View.GONE);
                    }else{
                        setBottomVisiable(View.VISIBLE);
                    }
                    lvmsg.scrollTo(scrolledX, scrolledY);

                    break;


            }
        }
    };


    private void setBottomVisiable(int visiableType){
        Log.e("high", "llMore" + llMore.getVisibility() + "  " + llMore.getHeight());
        Log.e("high", "llMore" + llMore.getVisibility() + "  " + llMore.getMeasuredHeight());
        if (View.VISIBLE==visiableType){
            Utils.hideSoftKeyBoard(activity);
            llMore.setVisibility(View.VISIBLE);
        }else if(View.GONE==visiableType){
            llMore.setVisibility(View.GONE);
        }

    }



    //resultCode的定义
    private final int PICK_IMAGE = 101;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case PICK_IMAGE:

                    break;

            }


        }


    }

    private void sendMessage(String textMessage, String command, String localDataPath) {

        if (ChatCommand.TEXT.equals(command)) {
            Chat c = new Chat();
            c.fromwho = APP.getSelf().getUserInfo().userid;
            c.sendto = toContact.userid;
            c.message = textMessage;
            c.command = command;
            c.localdatapath = localDataPath;
            c.receivedorsend = Chat.SEND;
            ChatService.sendMessage(c.toJson());
            chatlist.add(c);
            chatadapter.update(chatlist);
            //lvmsg.smoothScrollToPosition(lvmsg.getCount() - 1);
            lvmsg.setSelection(lvmsg.getCount() - 1);
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
    public static final int GET_OLD_CHAT_LIST = 2;
    public static final int GET_NEW_CHAT_LIST = 3;
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
                    Chat msgC = (Chat) msg.obj;
                    Chat newC = chatDbManager.getLastChat(msgC);
                    if (null != newC) {
                        chatlist.add(newC);

                        int scrolledX = lvmsg.getScrollX();
                        int scrolledY = lvmsg.getScrollY();
                        chatadapter.update(chatlist);
                        int scrollCount = lvmsg.getLastVisiblePosition() - lvmsg.getFirstVisiblePosition();
                        if (lvmsg.getLastVisiblePosition() >= lvmsg.getCount() - scrollCount) {//如果当前展示的位置是列表的最后几个，说明滑到了最底部，那么有新消息来时就滑到最后一条
                            lvmsg.smoothScrollToPosition(lvmsg.getCount() - 1);
                        } else {
                            lvmsg.scrollTo(scrolledX, scrolledY);
                        }

                    }
                    break;
                case GET_OLD_CHAT_LIST:
                    List<Chat> oldList = (List<Chat>) msg.obj;
                    if (null != oldList && oldList.size() > 0) {
                        chatlist.addAll(0, oldList);
                        top_id = chatlist.get(0)._id;
                        chatadapter.notifyDataSetChanged();
                        if (chatlist.size() == oldList.size()) {
                            lvmsg.setSelection(chatlist.size() - 1);
                        } else {
                            lvmsg.setSelection(oldList.size());
                            lvmsg.smoothScrollToPositionFromTop(oldList.size(), 50);
                        }
                        srlmsg.setRefreshing(false);

                    }
                    break;
                case GET_NEW_CHAT_LIST:
                    List<Chat> newList = (List<Chat>) msg.obj;
                    if (null != newList && newList.size() > 0) {
                        chatlist.addAll(chatlist.size(), newList);
                    }
                    int scrolledX = lvmsg.getScrollX();
                    int scrolledY = lvmsg.getScrollY();
                    chatadapter.update(chatlist);
                    if (lvmsg.getLastVisiblePosition() >= lvmsg.getCount() - 2) {//如果当前展示的位置是最后一个或最后第二个，说明滑到了最底部，那么有新消息来时就滑到最后一条
                        lvmsg.smoothScrollToPosition(lvmsg.getCount() - 1);
                    } else {
                        lvmsg.scrollTo(scrolledX, scrolledY);
                    }
                    break;


            }


        }

    };

    public void refreshNewChat(Chat chat) {
        if (StringUtils.equals(toContact.userid, chat.fromwho)) {
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


        //第一次进入界面，直接取得老数据
        //chatlist为空就相当于第一次进入界面了
        //以后再进入界面，就取新数据
        if (chatlist.size() == 0) {
            getOldChatList();
        } else {
            getNewChatList();
        }


    }


    private void getNewChatList() {

        new Thread(new Runnable() {
            @Override
            public void run() {


                if (null == chatlist && chatlist.size() < 1) {
                    bottom_id = Long.MAX_VALUE;
                } else {
                    try {
                        bottom_id = chatlist.get(chatlist.size() - 1)._id;
                    } catch (Exception e) {
                    }
                }

                List<Chat> newList = chatDbManager.getNewChatList(toContact.userid, bottom_id);
                Message msg = Message.obtain();
                msg.what = GET_NEW_CHAT_LIST;
                msg.obj = newList;
                handler.sendMessage(msg);
            }
        }).start();

    }

    private void getOldChatList() {
        List<Chat> oldList = null;
        oldList = chatDbManager.getChatList(toContact.userid, top_id);
        if (null == oldList || oldList.size() < AppConstants.PAGE_SIZE) {
            hasOldChat = false;
        } else {
            hasOldChat = true;
        }
        Message msg = Message.obtain();
        msg.what = GET_OLD_CHAT_LIST;
        msg.obj = oldList;
        handler.sendMessage(msg);
    }


    @Override
    protected void onPause() {
        super.onPause();
        ChatService.chatActivity = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChatService.chatActivity = null;
    }


    AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            setBottomVisiable(View.GONE);
            switch (scrollState) {
                case SCROLL_STATE_IDLE:
                    if (view.getFirstVisiblePosition() == 0) {


                    }
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {

        }
    };


    /**
     * 下拉刷新的监听器
     */
    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            {
                if (hasOldChat) {
                    getOldChatList();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlmsg.setRefreshing(false);
                    }
                }, 3000);
            }

        }
    };

    /**
     * ViewPager页面选项卡进行切换时候的监听器处理
     */
    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            index = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

    };
}
