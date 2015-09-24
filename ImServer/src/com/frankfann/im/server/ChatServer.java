package com.frankfann.im.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.frankfann.im.entity.Chat;
import com.frankfann.im.server.utils.StringUtils;

/**
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 */
public class ChatServer extends WebSocketServer {
	
	
	
	

	public ChatServer( int port ) throws UnknownHostException {
		super( new InetSocketAddress( port ) );
	}

	public ChatServer( InetSocketAddress address ) {
		super( address );
	}

	@Override
	public void onOpen( WebSocket conn, ClientHandshake handshake ) {
		Iterator<String> i=	handshake.iterateHttpFields();
		while(i.hasNext()){
			System.out.println("handshake:"+i.next());
		}
		String userid=handshake.getFieldValue("userid");
		if (StringUtils.isNotEmpty(userid)) {
			setUserMap(userid, conn);
		}
		System.out.println("conn.hasBufferedData():"+conn.hasBufferedData());
		this.sendToAll( "new connection: " + handshake.getResourceDescriptor() );
		System.out.println( conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!" );
		
	}

	@Override
	public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
		this.sendToAll( conn + " has left the room!" );
		System.out.println( conn + " has left the room!" );
	}

	@Override
	public void onMessage( WebSocket conn, String message ) {
		System.out.println("received message from:  "+conn + ": " + message );
		Chat chat=new Chat();
		
		this.sendToAll( message );
	}

	public static void main( String[] args ) throws InterruptedException , IOException {
		WebSocketImpl.DEBUG = true;
		int port = 8887; // 843 flash policy port
		try {
			port = Integer.parseInt( args[ 0 ] );
		} catch ( Exception ex ) {
		}
		ChatServer s = new ChatServer( port );
		s.start();
		System.out.println( "ChatServer started on port: " + s.getPort() );

		BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
		while ( true ) {
			String in = sysin.readLine();
			System.out.println("in:"+in);
			s.sendToAll( in );
		}
	}

	@Override
	public void onError( WebSocket conn, Exception ex ) {
		ex.printStackTrace();
		if( conn != null ) {
			// some errors like port binding failed may not be assignable to a specific websocket
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
	public void sendToAll( String text ) {
		Collection<WebSocket> con = connections();
		synchronized ( con ) {
			for( WebSocket c : con ) {
				System.out.println("send message to:  "+ c + ": " + text );
				c.send( text );
				
			}
		}
	}
	
	
	/**
	 * 存储用户id和socket的对应关系的map
	 */
	private  HashMap<String, WebSocket> userSocketMap;
	
	public  WebSocket getSocketByUserId(String userid){
		if (null==userSocketMap) {
			return null;
			
		}else{
			return userSocketMap.get(userid);	
		}
		
	}
	
	public  void setUserMap(String userid,WebSocket ws){
		if (null==userSocketMap) {
			userSocketMap=new HashMap<>();
		}
		userSocketMap.put(userid, ws);
		
	}
	
	
}
