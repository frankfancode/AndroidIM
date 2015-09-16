package com.frankfann.im.test;


import com.frankfann.im.entity.Chat;
import com.google.gson.Gson;

public class Test {

	public static void main(String[] args) {
		Chat c=new Chat();
		c.command="chat";
		c.message="123";
		 Gson gson = new Gson();
		System.out.println(gson.toJson(c));
		
		Chat c2=gson.fromJson(gson.toJson(c), Chat.class);
		System.out.println(c2.toString());	

	}

}
