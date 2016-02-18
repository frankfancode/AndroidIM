package com.frankfancode.im.bean;

/**
 * Created by user on 2015/10/15.
 */
public class ChatMoreTypeItem {
    public int chat_bottom_type_icon;
    public String chat_bottom_type_title;
    public Type chat_bottom_type_id;



    public enum Type{
        PICKIMAGE,CAPTURE,PICVIDEO,TAKEVIDEO
    }
}
