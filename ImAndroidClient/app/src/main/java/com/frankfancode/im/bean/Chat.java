package com.frankfancode.im.bean;

import com.frankfancode.im.ImAPPlication;
import com.frankfancode.im.utils.FLog;
import com.frankfancode.im.utils.JsonUtils;
import com.google.gson.Gson;

import java.util.List;

public class Chat {
    public final static String RECEIVED = "1";
    public final static String SEND = "2";

    public long _id = 0;
    /**
     * 命令
     */
    public String command;
    /**
     * 消息来自
     */
    public String fromwho;

    /**
     * 消息发往
     */
    public String sendto;

    /**
     * 消息内容
     */
    public String message;



    /**
     * 服务器生成的唯一标示
     */
    public String messageid;

    /**
     * 客户端生成的唯一标示
     */
    public String messagekey;

    /**
     * 上传的数据的本地地址，比如图片，视频，声音，文档，等这种文件的地址
     */
    public String localdatapath;

    /**
     * 结果码
     */
    public String resultcode;//1:成功  -1:失败

    /**
     * 接收的消息还是发送的消息
     * 1:接收消息 2:发送消息
     */
    public String receivedorsend;

    /**
     * 消息到达服务器的时间的时间戳,暂时还没用
     */
    public String messageservertime;

    /**
     * 自己
     */
    public String userid;

    /**
     * 对方
     */
    public String touserid;

    /**
     * 不同的命令用处不同
     */
    public String other;


    /**
     * 头像
     */
    public String portait;

    /**
     * 对方的展示名，备注>昵称>ID
     */
    public String showname;


    public Chat() {
    }

    public Chat ChatFromJson(String json) {
        Gson gson = new Gson();
        try {
            Chat chat = gson.fromJson(json, Chat.class);
            if ("1".equals(chat.receivedorsend)) {
                chat.touserid = chat.fromwho;
            } else {
                chat.touserid = chat.sendto;
            }
            chat.userid = ImAPPlication.getSelf().getUserInfo().userid;
            FLog.e("chat", "ChatFromJson:  " + chat.toString());
            return chat;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    @Override
    public String toString() {
        return "Chat{" +
                "_id=" + _id +
                ", command='" + command + '\'' +
                ", fromwho='" + fromwho + '\'' +
                ", sendto='" + sendto + '\'' +
                ", message='" + message + '\'' +
                ", messageid='" + messageid + '\'' +
                ", messagekey='" + messagekey + '\'' +
                ", localdatapath='" + localdatapath + '\'' +
                ", resultcode='" + resultcode + '\'' +
                ", receivedorsend='" + receivedorsend + '\'' +
                ", messageservertime='" + messageservertime + '\'' +
                ", userid='" + userid + '\'' +
                ", touserid='" + touserid + '\'' +
                ", other='" + other + '\'' +
                ", portait='" + portait + '\'' +
                ", showname='" + showname + '\'' +
                '}';
    }

    public String toJson() {
        try {
            String json=JsonUtils.toJson(this);
            return json;
        } catch (Exception e) {
            return "";
        }


    }
}
