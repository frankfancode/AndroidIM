package com.frankfancode.im.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.frankfancode.im.BuildConfig;
import com.frankfancode.im.ImAPPlication;
import com.frankfancode.im.R;
import com.frankfancode.im.adapter.ChatAdapter;
import com.frankfancode.im.adapter.ChatBottomGridViewAdapter;
import com.frankfancode.im.adapter.GridViewPagerAdapter;
import com.frankfancode.im.bean.AppConstants;
import com.frankfancode.im.bean.Chat;
import com.frankfancode.im.bean.ChatMoreTypeItem;
import com.frankfancode.im.database.ChatDbManager;
import com.frankfancode.im.net.HttpAssist;
import com.frankfancode.im.service.ChatService;
import com.frankfancode.im.utils.ChatCommand;
import com.frankfancode.im.utils.DialogUtils;
import com.frankfancode.im.utils.FLog;
import com.frankfancode.im.utils.FileTranUtils;
import com.frankfancode.im.utils.StringUtils;
import com.frankfancode.im.utils.Utils;
import com.frankfancode.im.widget.FixedGridView;
import com.frankfancode.im.widget.WrapContentHeightViewPager;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.PanelLayout;


public class ChatActivity extends BaseActivity {
    public static final int JUST_TOAST = 0;
    public static final int REFRESH = 1;
    public static final int GET_OLD_CHAT_LIST = 2;
    public static final int GET_NEW_CHAT_LIST = 3;
    //resultCode的定义
    private final int PICK_IMAGE = 101;
    private final int CAPTURE = PICK_IMAGE + 1;
    @Bind(R.id.tv_header_left)
    TextView tvHeaderLeft;
    @Bind(R.id.ll_header_left)
    LinearLayout llHeaderLeft;
    @Bind(R.id.tv_header_middle)
    TextView tvHeaderMiddle;
    @Bind(R.id.ll_header_middle)
    LinearLayout llHeaderMiddle;
    @Bind(R.id.tv_header_right)
    TextView tvHeaderRight;
    @Bind(R.id.ll_header_right)
    LinearLayout llHeaderRight;
    @Bind(R.id.header_bar)
    RelativeLayout headerBar;
    @Bind(R.id.lv_msg)
    ListView lvMsg;
    @Bind(R.id.srl_msg)
    SwipeRefreshLayout srlMsg;
    @Bind(R.id.btn_set_mode_voice)
    Button btnSetModeVoice;
    @Bind(R.id.btn_more)
    Button btnMore;
    @Bind(R.id.et_text_message)
    EditText etTextMessage;
    @Bind(R.id.iv_emoticons_normal)
    ImageView ivEmoticonsNormal;
    @Bind(R.id.iv_emoticons_checked)
    ImageView ivEmoticonsChecked;
    @Bind(R.id.edittext_layout)
    RelativeLayout edittextLayout;
    @Bind(R.id.btn_send)
    Button btnSend;
    @Bind(R.id.rl_bottom)
    LinearLayout rlBottom;
    @Bind(R.id.bar_bottom)
    LinearLayout barBottom;
    @Bind(R.id.vp_chat_bottom)
    WrapContentHeightViewPager vpChatBottom;
    @Bind(R.id.panel_root)
    PanelLayout panelRoot;
    AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            //hidePanel();

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
    private Activity activity = this;
    private Dialog mDialog;
    private String mTargetType;//单聊 CHAT_TEXT 或 群聊 GROUP_CHAT_TEXT
    private String mToId;//对方id，可能是用户id，可能是群id，获取以后还可以发广播，专门的id
    private String mTitle;
    private int PAGE_INDEX = 1;
    private long top_id = -1;//列表最上一条chat 的_id，用于获取老聊天记录时，查找小于 top_id 的chat
    private long bottom_id = Long.MAX_VALUE;//列表最下一条chat的_id，用于获取新聊天记录时，查找大于 top_id 的chat
    private boolean hasOldChat = true;
    /**
     * 底部更多种消息类型用到的变量
     */
    private int index = 0;
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
    private List<GridView> gvList;
    private GridViewPagerAdapter gvpAdapter;
    private List<ChatMoreTypeItem> moreTypeList;
    private List<Chat> chatlist = new ArrayList<Chat>();
    private ChatAdapter chatadapter;
    private ChatDbManager chatDbManager;
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

                        int scrolledX = lvMsg.getScrollX();
                        int scrolledY = lvMsg.getScrollY();
                        chatadapter.update(chatlist);
                        int scrollCount = lvMsg.getLastVisiblePosition() - lvMsg.getFirstVisiblePosition();
                        if (lvMsg.getLastVisiblePosition() >= lvMsg.getCount() - scrollCount) {//如果当前展示的位置是列表的最后几个，说明滑到了最底部，那么有新消息来时就滑到最后一条
                            lvMsg.smoothScrollToPosition(lvMsg.getCount() - 1);
                        } else {
                            lvMsg.scrollTo(scrolledX, scrolledY);
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
                            lvMsg.setSelection(chatlist.size() - 1);
                        } else {
                            lvMsg.setSelection(oldList.size());
                            lvMsg.smoothScrollToPositionFromTop(oldList.size(), 50);
                        }
                        srlMsg.setRefreshing(false);

                    }
                    break;
                case GET_NEW_CHAT_LIST:
                    List<Chat> newList = (List<Chat>) msg.obj;
                    if (null != newList && newList.size() > 0) {
                        chatlist.addAll(chatlist.size(), newList);
                    }
                    int scrolledX = lvMsg.getScrollX();
                    int scrolledY = lvMsg.getScrollY();
                    chatadapter.update(chatlist);
                    if (lvMsg.getLastVisiblePosition() >= lvMsg.getCount() - 2) {//如果当前展示的位置是最后一个或最后第二个，说明滑到了最底部，那么有新消息来时就滑到最后一条
                        lvMsg.smoothScrollToPosition(lvMsg.getCount() - 1);
                    } else {
                        lvMsg.scrollTo(scrolledX, scrolledY);
                    }
                    break;


            }


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
                        srlMsg.setRefreshing(false);
                    }
                }, 3000);
            }

        }
    };
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_send:
                    String textMessage = etTextMessage.getText().toString().trim();
                    if (StringUtils.isNullOrEmpty(textMessage)) {
                        Utils.toast(activity, "不能发送空消息！");
                        return;
                    }
                    sendMessage(textMessage, ChatCommand.CHAT_TEXT, null);
                    etTextMessage.setText("");
                    break;
                case R.id.et_text_message:
                    //hidePanel();
                    break;
                case R.id.btn_more:
                    int scrolledX = lvMsg.getScrollX();
                    int scrolledY = lvMsg.getScrollY();
                    switchPanel();
                    //lvMsg.scrollTo(scrolledX, scrolledY);

                    break;


            }
        }
    };

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        registerListener();
        initIntentData();
        initData();

        if (BuildConfig.DEBUG) {
            initTestData();
        }
        if (StringUtils.isNullOrEmpty(mToId)) {
            pairSomeone();
        }
    }

    private void initTestData() {

        for (int i = 0; i < 100; i++) {
            Chat chat = new Chat();
            chat.command = ChatCommand.CHAT_TEXT;
            chat.message = "this is " + i + "st";
            chat.receivedorsend = String.valueOf((int) (Math.random() * 2 + 1));
            chatlist.add(chat);
        }


    }

    private void initIntentData() {
        handleIntent();
        setTitle(mTitle);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent();
        setTitle(mTitle);
    }

    private void handleIntent() {
        mToId = getIntent().getStringExtra("id");
        mTitle = getIntent().getStringExtra("title");//也可以根据 id 去联系人表找
        mTargetType = getIntent().getStringExtra("targettype");//聊天对象，可能是服务器server或个人或群组

        if (TextUtils.isEmpty(mToId)) {
            Utils.toast(getApplicationContext(), "I want a ID ");
            finish();
        }
    }

    private void registerListener() {
        btnSend.setOnClickListener(clickListener);
        lvMsg.setOnScrollListener(onScrollListener);
        srlMsg.setOnRefreshListener(refreshListener);
        btnMore.setOnClickListener(clickListener);
        vpChatBottom.addOnPageChangeListener(pageChangeListener);
        etTextMessage.setOnClickListener(clickListener);
        //etTextMessage.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);


        /*etTextMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    panelRoot.setVisibility(View.GONE);
                }
            }
        });*/
    }

    private void initData() {
        chatDbManager = new ChatDbManager(activity);
        chatadapter = new ChatAdapter(activity, chatlist);
        lvMsg.setAdapter(chatadapter);
        lvMsg.setSelection(chatlist.size() - 1);
        etTextMessage.clearFocus();
        initMoreType();
        initGridView();
        gvpAdapter = new GridViewPagerAdapter(this, gvList);
        vpChatBottom.setAdapter(gvpAdapter);
        gvpAdapter.notifyDataSetChanged();
    }

    private void initGridView() {
        gvList = new ArrayList<>();
        final int PageCount = (int) Math.ceil(moreTypeList.size() / 8.0f);
        for (int i = 0; i < PageCount; i++) {
            FixedGridView gv = new FixedGridView(this);
            ViewGroup.LayoutParams gvvglp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            gv.setLayoutParams(gvvglp);

            List<ChatMoreTypeItem> imoreTypeList = null;
            if (moreTypeList.size() > i * 8) {
                imoreTypeList = moreTypeList.subList(i * 8, Math.min(i * 8 + 8, moreTypeList.size()));
            }
            if (null == imoreTypeList) {
                return;
            }
            gv.setAdapter(new ChatBottomGridViewAdapter(panelRoot,activity, imoreTypeList, R.layout.chat_more_type_grid_view_item));
            gv.setGravity(Gravity.CENTER);
            gv.setClickable(true);
            gv.setFocusable(true);
            gv.setNumColumns(4);

            final List<ChatMoreTypeItem> finalImoreTypeList = imoreTypeList;
            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {
                    if (ChatMoreTypeItem.Type.CAPTURE == finalImoreTypeList.get(position).chat_bottom_type_id) {
                        String SDState = Environment.getExternalStorageState();
                        if (SDState.equals(Environment.MEDIA_MOUNTED)) {

                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//"android.media.action.IMAGE_CAPTURE"
                            /***
                             * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的
                             * 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
                             * 如果不实用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
                             */
                            ContentValues values = new ContentValues();
                            Uri photoUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            /**-----------------*/
                            startActivityForResult(intent, CAPTURE);
                        } else {
                            Utils.toast(activity, "内存卡不存在");
                        }


                    } else if (ChatMoreTypeItem.Type.PICKIMAGE == finalImoreTypeList.get(position).chat_bottom_type_id) {
                        Intent pickImageIntent = new Intent(activity, ImagePickerActivity.class);
                        startActivityForResult(pickImageIntent, PICK_IMAGE);
                    }


                }
            });
            gvList.add(gv);
        }
    }

    private void initMoreType() {
        moreTypeList = new ArrayList<>();
        ChatMoreTypeItem moreTypeItem = new ChatMoreTypeItem();
        //拍照
        moreTypeItem.chat_bottom_type_id = ChatMoreTypeItem.Type.CAPTURE;
        moreTypeItem.chat_bottom_type_icon = R.drawable.icon_chat_capture;
        moreTypeItem.chat_bottom_type_title = "拍照";
        moreTypeList.add(moreTypeItem);

        moreTypeItem = new ChatMoreTypeItem();
        //选择图片
        moreTypeItem.chat_bottom_type_id = ChatMoreTypeItem.Type.PICKIMAGE;
        moreTypeItem.chat_bottom_type_icon = R.drawable.icon_chat_image;
        moreTypeItem.chat_bottom_type_title = "图片";
        moreTypeList.add(moreTypeItem);
        moreTypeItem = new ChatMoreTypeItem();
        //选择图片
        moreTypeItem.chat_bottom_type_id = ChatMoreTypeItem.Type.PICKIMAGE;
        moreTypeItem.chat_bottom_type_icon = R.drawable.icon_chat_image;
        moreTypeItem.chat_bottom_type_title = "图片";
        moreTypeList.add(moreTypeItem);
        moreTypeItem = new ChatMoreTypeItem();
        //选择图片
        moreTypeItem.chat_bottom_type_id = ChatMoreTypeItem.Type.PICKIMAGE;
        moreTypeItem.chat_bottom_type_icon = R.drawable.icon_chat_image;
        moreTypeItem.chat_bottom_type_title = "图片";
        moreTypeList.add(moreTypeItem);
        moreTypeItem = new ChatMoreTypeItem();
        //选择图片
        moreTypeItem.chat_bottom_type_id = ChatMoreTypeItem.Type.PICKIMAGE;
        moreTypeItem.chat_bottom_type_icon = R.drawable.icon_chat_image;
        moreTypeItem.chat_bottom_type_title = "图片";
        moreTypeList.add(moreTypeItem);
        moreTypeItem = new ChatMoreTypeItem();

        //选择图片
        moreTypeItem.chat_bottom_type_id = ChatMoreTypeItem.Type.PICKIMAGE;
        moreTypeItem.chat_bottom_type_icon = R.drawable.icon_chat_image;
        moreTypeItem.chat_bottom_type_title = "图片";
        moreTypeList.add(moreTypeItem);
        moreTypeItem = new ChatMoreTypeItem();
        //选择图片
        moreTypeItem.chat_bottom_type_id = ChatMoreTypeItem.Type.PICKIMAGE;
        moreTypeItem.chat_bottom_type_icon = R.drawable.icon_chat_image;
        moreTypeItem.chat_bottom_type_title = "图片";
        moreTypeList.add(moreTypeItem);
        moreTypeItem = new ChatMoreTypeItem();
        //选择图片
        moreTypeItem.chat_bottom_type_id = ChatMoreTypeItem.Type.PICKIMAGE;
        moreTypeItem.chat_bottom_type_icon = R.drawable.icon_chat_image;
        moreTypeItem.chat_bottom_type_title = "图片";
        moreTypeList.add(moreTypeItem);
        moreTypeItem = new ChatMoreTypeItem();
        //选择图片
        moreTypeItem.chat_bottom_type_id = ChatMoreTypeItem.Type.PICKIMAGE;
        moreTypeItem.chat_bottom_type_icon = R.drawable.icon_chat_image;
        moreTypeItem.chat_bottom_type_title = "图片";
        moreTypeList.add(moreTypeItem);
        moreTypeItem = new ChatMoreTypeItem();
        //选择图片
        moreTypeItem.chat_bottom_type_id = ChatMoreTypeItem.Type.PICKIMAGE;
        moreTypeItem.chat_bottom_type_icon = R.drawable.icon_chat_image;
        moreTypeItem.chat_bottom_type_title = "图片";
        moreTypeList.add(moreTypeItem);
        moreTypeItem = new ChatMoreTypeItem();
        //选择图片
        moreTypeItem.chat_bottom_type_id = ChatMoreTypeItem.Type.PICKIMAGE;
        moreTypeItem.chat_bottom_type_icon = R.drawable.icon_chat_image;
        moreTypeItem.chat_bottom_type_title = "图片";
        moreTypeList.add(moreTypeItem);
        moreTypeItem = new ChatMoreTypeItem();
        //选择图片
        moreTypeItem.chat_bottom_type_id = ChatMoreTypeItem.Type.PICKIMAGE;
        moreTypeItem.chat_bottom_type_icon = R.drawable.icon_chat_image;
        moreTypeItem.chat_bottom_type_title = "图片";
        moreTypeList.add(moreTypeItem);
        moreTypeItem = new ChatMoreTypeItem();
        //选择图片
        moreTypeItem.chat_bottom_type_id = ChatMoreTypeItem.Type.PICKIMAGE;
        moreTypeItem.chat_bottom_type_icon = R.drawable.icon_chat_image;
        moreTypeItem.chat_bottom_type_title = "图片";
        moreTypeList.add(moreTypeItem);
        moreTypeItem = new ChatMoreTypeItem();

        //选择图片
        moreTypeItem.chat_bottom_type_id = ChatMoreTypeItem.Type.PICKIMAGE;
        moreTypeItem.chat_bottom_type_icon = R.drawable.icon_chat_image;
        moreTypeItem.chat_bottom_type_title = "图片";
        moreTypeList.add(moreTypeItem);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case PICK_IMAGE:
                    List<String> imagePathList = data.getStringArrayListExtra("imagepaths");
                    //Log.i(TAG, "最终选择的图片="+picPath);
                    for (final String imagePath : imagePathList) {
                        com.orhanobut.logger.Logger.e(imagePath);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //FileTranUtils.run(new File(imagePath));
                                    HttpAssist.uploadFile(new File(imagePath));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();


                    }


                    break;

            }


        }


    }

    private void sendMessage(String textMessage, String command, String localDataPath) {

        if (ChatCommand.CHAT_TEXT.equalsIgnoreCase(command)) {
            Chat c = new Chat();
            c.fromwho = ImAPPlication.getSelf().getUserInfo().userid;
            c.sendto = mToId;
            c.message = textMessage;
            c.command = command;
            c.localdatapath = localDataPath;
            c.receivedorsend = Chat.SEND;

            ChatService.sendMessage(c.toJson());
            chatlist.add(c);
            chatadapter.update(chatlist);
            //lvMsg.smoothScrollToPosition(lvMsg.getCount() - 1);
            lvMsg.setSelection(lvMsg.getCount() - 1);
        }

        //HashMap<String, String> map = assembleChatMap(c);;

        /*ChatService.sendMessage(ChatUtils.getJsonFromMap(map));*/
    }

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
        mDialog = DialogUtils.showProcessDialog(activity, getString(R.string.pair_someone));
        try {
            /*HashMap<String,String> map = new HashMap<String, String>();
            map.put("command", ChatCommand.PAIR_SOMEONE_RANDOM);
            map.put("from", APP.getSelf().getUserInfo().userid);
            String messagekey= UUID.randomUUID().toString();
            map.put("messagekey", messagekey);
            ChatService.sendMessage(ChatUtils.getJsonFromMap(map));*/

            Chat chatRequest = new Chat();
            chatRequest.command = ChatCommand.PAIR_SOMEONE_RANDOM;
            chatRequest.fromwho = ImAPPlication.getSelf().getUserInfo().userid;
            chatRequest.messagekey = UUID.randomUUID().toString();
            ChatService.sendMessage(chatRequest.toJson());
            ImAPPlication.getSelf().getEBus().register(activity, ChatCommand.PAIR_SOMEONE_RANDOM, chatRequest.messagekey);
        } catch (Exception e) {
            if (null != mDialog) {
                mDialog.dismiss();
            }

        }
    }

    public void pairsomeonerandomStart(String string) {
        if (null != mDialog) {
            mDialog.dismiss();
        }
        FLog.e("ebus", "Start=====" + string);

        Chat chat = new Chat().ChatFromJson(string);
        if (null != chat) {

            if ("1".equals(chat.resultcode)) {
                mToId = chat.message;

            } else {
                Message msg = new Message();
                msg.what = JUST_TOAST;
                msg.obj = chat.message;
                handler.sendMessage(msg);
            }

        }


    }

    public void pairsomeonerandomEnd(String string) {
        FLog.e("ebus", "End=====" + string);
        if (null != mDialog) {
            mDialog.dismiss();
        }
        Utils.toast(activity, "匹配失败");

    }

    public void refreshNewChat(Chat chat) {
        if (StringUtils.equals(mToId, chat.fromwho)) {
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

                List<Chat> newList = chatDbManager.getNewChatList(mToId, bottom_id);
                Message msg = Message.obtain();
                msg.what = GET_NEW_CHAT_LIST;
                msg.obj = newList;
                handler.sendMessage(msg);
            }
        }).start();

    }

    private void getOldChatList() {
        List<Chat> oldList = null;
        oldList = chatDbManager.getChatList(mToId, top_id);
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
        ButterKnife.unbind(this);
        ChatService.chatActivity = null;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (panelRoot.getVisibility() == View.VISIBLE) {
                panelRoot.setVisibility(View.GONE);
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    // Keyboard与面板相互切换
    public void switchPanel() {
       /* if (panelRoot.getVisibility() == View.VISIBLE) {
            KeyboardUtil.showKeyboard(etTextMessage);
        } else {
            KeyboardUtil.hideKeyboard(etTextMessage);
            showPanel();
        }*/

        if (panelRoot.getVisibility() == View.VISIBLE) {
            KeyboardUtil.showKeyboard(etTextMessage);
        } else {
            KeyboardUtil.hideKeyboard(etTextMessage);
            panelRoot.setVisibility(View.VISIBLE);
            panelRoot.post(new Runnable() {
                @Override
                public void run() {

                    Logger.e("panelRoot: "+panelRoot.getMeasuredHeight());
                    Logger.e("vpChatBottom: "+vpChatBottom.getMeasuredHeight());

                    ViewGroup.LayoutParams lp=  vpChatBottom.getLayoutParams();
                    lp.height=panelRoot.getMeasuredHeight();
                    if (lp.height>vpChatBottom.getMeasuredHeight()){
                        vpChatBottom.setLayoutParams(lp);

                    }

                    vpChatBottom.post(new Runnable() {
                        @Override
                        public void run() {
                            for (GridView gv:gvList){
                                ((BaseAdapter)gv.getAdapter()).notifyDataSetChanged();
                            }
                        }
                    });


                }
            });
        }
    }

    @Override
    protected void setTitle(String title) {
        tvHeaderMiddle.setText(title);
    }

    @OnClick({R.id.ll_header_left, R.id.ll_header_right})
    public void headerClick(View v) {
        if (v.getId() == R.id.ll_header_left) {
            finish();
        } else if (v.getId() == R.id.ll_header_right) {

        }
    }


}
