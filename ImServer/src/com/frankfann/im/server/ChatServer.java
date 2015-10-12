package com.frankfann.im.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.frankfann.im.entity.Chat;
import com.frankfann.im.entity.Contact;
import com.frankfann.im.server.utils.ChatCommand;
import com.frankfann.im.server.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 */
public class ChatServer extends WebSocketServer {
	// add()和remove()方法在失败的时候会抛出异常(不推荐)
	List<String> userList = new ArrayList<>();

	public ChatServer(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
	}

	public ChatServer(InetSocketAddress address) {
		super(address);
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		Iterator<String> i = handshake.iterateHttpFields();
		while (i.hasNext()) {
			System.out.println("handshake:" + i.next());
		}
		String userid = handshake.getFieldValue("userid");
		String username = handshake.getFieldValue("username");
		try {
			username = java.net.URLDecoder.decode(handshake.getFieldValue("username"),"utf-8");
			userid = java.net.URLDecoder.decode(handshake.getFieldValue("userid"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Contact contact=new Contact();
		contact.userid=userid;
		contact.username=username;
		System.out.println(contact.toString());
		addContactMap(userid, contact);
		
		if (StringUtils.isNotEmpty(userid)) {
			setUserSocketMap(userid, conn);
		}

		System.out.println("目前在线" + getOnlineCount() + "人");

		System.out.println("conn.hasBufferedData():" + conn.hasBufferedData());
		this.sendToAll("new connection: " + handshake.getResourceDescriptor());
		System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress()+ " entered the room!");
		if (!userList.contains(userid)) {
			userList.add(userid);
		}

	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
		if (conn != null) {
			// some errors like port binding failed may not be assignable to a
			// specific websocket
		}
		removeContactMap(getUseridBySocket(conn));
		removeUserSocketMapBySocket(conn);
		

		
		System.out.println("目前在线" + getOnlineCount() + "人");
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		this.sendToAll(conn + " has left the room!");
		System.out.println(conn + " has left the room!");

		removeContactMap(getUseridBySocket(conn));
		removeUserSocketMapBySocket(conn);

		System.out.println("目前在线" + getOnlineCount() + "人");
	}

	@Override
	public void onMessage(WebSocket conn, String message) {

		if ("t".equals(message)) {// 这是心跳包
			return;

		}
		System.out.println("received message from:  " + conn + ": " + message);

		Chat chatRequest = new Chat();
		Gson gson = new Gson();
		try {
			chatRequest = gson.fromJson(message, Chat.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (null == chatRequest) {
			return;
		}
		Chat chatResponse;
		if (ChatCommand.TEXT.equals(chatRequest.command)) {
			WebSocket pairWebSocket = getSocketByUserId(chatRequest.sendto);
			if (pairWebSocket != null) {
				chatResponse = chatRequest;
				chatResponse.messageservertime=String.valueOf(System.currentTimeMillis());
				chatResponse.receivedorsend="1";
				sendChat(pairWebSocket,chatResponse);
			}

		} else if (ChatCommand.PAIR_SOMEONE_RANDOM.equals(chatRequest.command)) {
			if (wsCount <= 1) {
				chatResponse = new Chat();
				chatResponse.command = ChatCommand.PAIR_SOMEONE_RANDOM;
				chatResponse.resultcode = "-1";// 匹配失败
				chatResponse.messagekey = chatRequest.messagekey;
				chatResponse.message = "现在只有你在线";
				
			} else {
				String pairUserid = getRandomPairUserid(chatRequest.fromwho);
				if (StringUtils.isNotEmpty(pairUserid)) {
					chatResponse = new Chat();
					chatResponse.command = ChatCommand.PAIR_SOMEONE_RANDOM;
					chatResponse.resultcode = "1";// 匹配成功
					chatResponse.message = pairUserid;
					chatResponse.messagekey = chatRequest.messagekey;
					
					WebSocket pairWebSocket = getSocketByUserId(pairUserid);

				} else {
					chatResponse = new Chat();
					chatResponse.command = ChatCommand.PAIR_SOMEONE_RANDOM;
					chatResponse.resultcode = "-1";// 匹配失败
					chatResponse.messagekey = chatRequest.messagekey;
					chatResponse.message = "匹配失败，现在有" + wsCount + "人在线";
					
				}
				

			}
			sendChat(conn, chatResponse);

		} else if (ChatCommand.GET_CONTACTS_USERID_RANDOM
				.equals(chatRequest.command)) {
			userList= new ArrayList(userSocketMap.keySet());
			chatResponse = new Chat();
			chatResponse.command = ChatCommand.GET_CONTACTS_USERID_RANDOM;
			chatResponse.resultcode = "1";
			chatResponse.messagekey = chatRequest.messagekey;
			String userids = "";
			int count=20;
			try {
				count=Integer.valueOf(chatRequest.other);
			} catch (Exception e2) {
				count=20;
			}
			
//			for (int i = 0; i < userList.size()&&i<count; i++) {
//				userids=userids+","+userList.get(i);
//			}
			
			try {
				String contactJson=new Gson().toJson(contactMap.values());
				chatResponse.message=contactJson;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			
			sendChat(conn, chatResponse);
		}

		// this.sendToAll( message );
	}

	private String getRandomPairUserid(String curUserid) {
		/*
		 * if (1==userList.size()) {//队列里只有一个人就等等 return null; }else{
		 * userList.remove(curUserid); Random random = new
		 * Random(System.currentTimeMillis()); int
		 * randomPos=random.nextInt(userList.size()); String pairUserid=
		 * userList.get(randomPos); userList.remove(pairUserid); return
		 * pairUserid; }
		 */

		List<String> list = new ArrayList(userSocketMap.keySet());
		if (1 == list.size()) {
			return null;
		} else {
			list.remove(curUserid);
			Random random = new Random(System.currentTimeMillis());
			int randomPos = random.nextInt(list.size());

			return list.get(randomPos);
		}
	}

	public static void main(String[] args) throws InterruptedException,
			IOException {
		WebSocketImpl.DEBUG = true;
		int port = 8887; // 843 flash policy port
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception ex) {
		}
		ChatServer s = new ChatServer(port);
		s.start();
		System.out.println("ChatServer started on port: " + s.getPort());

		BufferedReader sysin = new BufferedReader(new InputStreamReader(
				System.in));
		while (true) {
			String in = sysin.readLine();
			System.out.println("in:" + in);
			s.sendToAll(in);
		}
	}

	/**
	 * Sends <var>text</var> to all currently connected WebSocket clients.
	 * 
	 * @param text
	 *            The String to send across the network.
	 * @throws InterruptedException
	 *             When socket related I/O errors occur.
	 */
	public void sendToAll(String text) {
		Collection<WebSocket> con = connections();
		synchronized (con) {
			for (WebSocket c : con) {
				System.out.println("send message to:  " + c + ": " + text);
				c.send(text);

			}
		}
	}

	/**
	 * 存储用户id和socket的对应关系的map
	 */
	private HashMap<String, WebSocket> userSocketMap;
	private HashMap<String, Contact> contactMap;
	
	private int wsCount = 0;

	public WebSocket getSocketByUserId(String userid) {
		if (null == userSocketMap) {
			return null;

		} else {
			return userSocketMap.get(userid);
		}

	}


	public void setUserSocketMap(String userid, WebSocket ws) {
		if (null == userSocketMap) {
			userSocketMap = new HashMap<>();
		}
		userSocketMap.put(userid, ws);

	}
	
	
	public Contact getContactByUserId(String userid) {
		if (null == contactMap) {
			return null;

		} else {
			return contactMap.get(userid);
		}

	}
	public void addContactMap(String userid, Contact contact) {
		if (null == contactMap) {
			contactMap = new HashMap<>();
		}
		contactMap.put(userid, contact);

	}
	
	public void removeContactMap(String userid) {
		if (null == contactMap) {
			return;
		}
		contactMap.remove(userid);
	}

	public void removeUserSocketMapBySocket(WebSocket ws) {
		if (null==userSocketMap||userSocketMap.size()<=0) {
			return;
			
		}
		Iterator entries = userSocketMap.entrySet().iterator();
		while (entries.hasNext()) {

			Map.Entry entry = (Map.Entry) entries.next();

			String key = (String) entry.getKey();
			WebSocket value = (WebSocket) entry.getValue();
			if (ws.equals(value)) {
				userSocketMap.remove(key);
			}
		}

	}

	public String getUseridBySocket(WebSocket ws) {

		if (null==userSocketMap||userSocketMap.size()<=0) {
			return "";
			
		}
		Iterator entries = userSocketMap.entrySet().iterator();
		while (entries.hasNext()) {

			Map.Entry entry = (Map.Entry) entries.next();

			String key = (String) entry.getKey();
			WebSocket value = (WebSocket) entry.getValue();
			if (ws.equals(value)) {
				return key;
			}
		}
		return "";

	}

	public int getOnlineCount() {
		if (null == userSocketMap) {
			wsCount = 0;
		} else {
			List<String> list = new ArrayList(userSocketMap.keySet());
			wsCount = list.size();
		}

		return wsCount;
	}
	
	
	/**
	 * 
	 * 发送消息前先处理一下
	 * ws 发送消息的socket
	 * c 发送的内容
	 * 
	 */
	
	private void sendChat(WebSocket ws,Chat c){
		c.messageid=UUID.randomUUID().toString();
		ws.send(c.toJson());
		
		
	}
	
}
