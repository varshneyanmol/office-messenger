package com.app.server.hibernate.model;

import java.util.Date;

import com.app.server.PrivateChat;

public class PrivateMessage extends Message {
	private PrivateChat privateChat;

	public PrivateMessage() {

	}

	public PrivateMessage(String body, Date time, RegisteredClient sender, PrivateChat privateChat) {
		super(body, time, sender);
		this.privateChat = privateChat;
	}

	public PrivateChat getPrivateChat() {
		return privateChat;
	}

	public void setPrivateChat(PrivateChat privateChat) {
		this.privateChat = privateChat;
	}

	@Override
	public String toString() {
		String str = privateChat.getId() + " : " + this.getId() + " : " + this.getBody();
		return str;
	}
}
