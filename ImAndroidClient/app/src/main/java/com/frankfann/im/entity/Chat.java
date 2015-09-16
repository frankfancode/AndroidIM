package com.frankfann.im.entity;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class Chat {
	/**
	 * 命令
	 */
	public String command;
	
	/**
	 * 消息内容
	 */
	public String message;


	/**
	 * 服务器生成的唯一标示
	 */
	public String messageID;

	/**
	 * 客户端生成的唯一标示
	 */
	public String messageKey;



	public Chat(String json) throws JSONException {

		JSONObject object=new JSONObject(json);
		command=getString(object,"command");
		message=getString(object,"message");
		messageKey=getString(object,"messagekey");
	}

	private String getString(JSONObject object,String node){

		if (null==object|| TextUtils.isEmpty(node)){
			return "";
		}
		String value="";
		try {
			value=object.getString(node);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return value;
	}
}
