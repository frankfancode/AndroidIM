package com.frankfancode.im.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.TypeReference;
import com.android.volley.Request;
import com.frankfancode.im.R;
import com.frankfancode.im.api.ActionConstant;
import com.frankfancode.im.api.NetApi;
import com.frankfancode.im.bean.Chat;
import com.frankfancode.im.bean.Contact;
import com.frankfancode.im.bean.Result;
import com.frankfancode.im.net.RequestManager;
import com.frankfancode.im.service.ChatService;
import com.frankfancode.im.utils.ChatCommand;
import com.frankfancode.im.utils.ChatUtils;
import com.frankfancode.im.utils.JsonUtils;
import com.github.lazylibrary.util.ToastUtils;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Frank on 2016/2/8.
 */
public class ChatAddFriendActivity extends BaseActivity {
    @Bind(R.id.bt_cancle)
    Button bt_cancle;
    @Bind(R.id.iv_search)
    ImageView iv_search;
    @Bind(R.id.et_searchtext_search)
    EditText et_searchtext_search;
    @Bind(R.id.ib_searchtext_delete)
    ImageView ib_searchtext_delete;
    @Bind(R.id.rl_search)
    RelativeLayout rl_search;
    @Bind(R.id.lv_search_result)
    ListView lv_search_result;
    private Context context = this;
    private ArrayList<Contact> mContactList;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.bt_cancle) {
                finish();
            } else if (v.getId() == R.id.ib_searchtext_delete) {
                et_searchtext_search.setText("");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_add_friend);
        ButterKnife.bind(this);
        init();
        registerListener();
        //注册EventBus
        EventBus.getDefault().register(this);
    }

    private void init() {
        bt_cancle.setOnClickListener(clickListener);
    }

    private void registerListener() {
        //给删除按钮添加点击事件
        ib_searchtext_delete.setOnClickListener(clickListener);
        //给编辑框添加文本改变事件
        et_searchtext_search.addTextChangedListener(new MyTextWatcher());

    }

    private void requestFriend(String searchWord) {
        HashMap params = new HashMap();
        params.put("action", ActionConstant.SEARCH_FRIEND);
        params.put("searchword", searchWord);


        Request loginRequest = NetApi.getResultRequest(params, String.class, new NetApi.NetListener<Result>() {
            @Override
            public void onPreStart() {

            }

            @Override
            public void onPreResponse() {

            }

            @Override
            public void onSuccess(Result response) {
                final Result result = response;


                if (1 == result.code) {


                    try {
                        mContactList = JsonUtils.fromJson(String.valueOf(result.data), new TypeReference<ArrayList<Contact>>() {
                        });


                        BaseAdapter adapter = new QuickAdapter<Contact>(mContext, R.layout.friend_item, mContactList) {

                            @Override
                            protected void convert(BaseAdapterHelper helper, final Contact item) {
                                helper.setText(R.id.tv_title, item.nickname)
                                        .setImageUrl(R.id.iv_portrait, item.portraituri)
                                        .setOnClickListener(R.id.bt_add, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                ChatUtils.addFriend(item.userid);
                                            }
                                        });


                            }
                        };

                        lv_search_result.setAdapter(adapter);
                    } catch (Exception e) {
                        Logger.e(e.getMessage());
                    }
                } else {
                    ToastUtils.showToast(context, result.message);
                }

            }


            @Override
            public void onJustResponse(String response) {

            }

            @Override
            public void onError(Exception e) {
                Logger.e(e.getMessage());
                ToastUtils.showToast(context, "连接服务器失败");
            }


        });

        RequestManager.getInstance().addRequest(loginRequest, context);

    }

    @Subscribe
    public void onEvent(Chat event)
    {

    }
    /**
     * 使用onEventMainThread来接收事件，那么不论分发事件在哪个线程运行，接收事件永远在UI线程执行，
     * 这对于android应用是非常有意义的
     *
     * @param chat
     */
    @Subscribe
    public void onEventMainThread(Chat chat) {
        if (null!=chat && ChatCommand.CHAT_TEXT.equalsIgnoreCase(chat.command)){

        }

    }

    //文本观察者
    private class MyTextWatcher implements TextWatcher {

        @Override
        public void afterTextChanged(Editable s) {
            requestFriend(s.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        //当文本改变时候的操作
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            //如果编辑框中文本的长度大于0就显示删除按钮否则不显示
            if (s.length() > 0) {
                ib_searchtext_delete.setVisibility(View.VISIBLE);
            } else {
                ib_searchtext_delete.setVisibility(View.GONE);
            }
        }

    }

    @Override
    protected void onDestroy() {
        //取消注册EventBus
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
