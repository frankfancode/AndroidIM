package com.frankfann.im.test;


import java.util.Random;

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
		
		
		String jsona="{\"command\":\"pairsomeonerandom\",\"messagekey\":\"203e94e4-1059-4802-907e-05baa813540d\",\"resultcode\":\"-1\"}";
		Chat chat=gson.fromJson(jsona, Chat.class);
		
		
		
		 int max=20;
	        int min=10;
	        Random random = new Random();

	        int s = random.nextInt(1);
	        System.out.println(random.nextBoolean());

	}

}
