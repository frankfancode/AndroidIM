package com.frankfancode.im.utils;

import com.frankfancode.im.bean.Chat;
import com.frankfancode.im.service.ChatService;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Frank on 2015/10/8.
 */
public  class ChatUtils {
    public static boolean addFriend(String userid){
        synchronized (ChatUtils.class){
            Chat chat=new Chat();
            chat.command=ChatCommand.CHAT_ADD;
            chat.fromwho= ChatService.getUserid();
            chat.touserid=userid;
            return false;
        }



    }
}
