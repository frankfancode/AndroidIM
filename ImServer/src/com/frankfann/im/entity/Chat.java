package com.frankfann.im.entity;


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
	 * 
	 */
	public String messageKey;

	public Chat() {
	}

	@Override
	public String toString() {
		return "Chat [command=" + command + ", message=" + message
				+ ", messageKey=" + messageKey + "]";
	}

}
