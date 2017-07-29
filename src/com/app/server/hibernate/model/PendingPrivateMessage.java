package com.app.server.hibernate.model;

import com.app.server.PrivateChat;
import com.app.server.RegisteredClient;

public class PendingPrivateMessage extends Pending {
	private Message latestMessage;
	private PrivateChat privateChat;
	private int totalPending;

	public PendingPrivateMessage() {

	}

	public PendingPrivateMessage(RegisteredClient client, PrivateChat privateChat, Message message) {
		super(client);
		this.privateChat = privateChat;
		this.latestMessage = message;
	}

	public Message getLatestMessage() {
		return latestMessage;
	}

	public void setLatestMessage(Message latestMessage) {
		this.latestMessage = latestMessage;
	}

	public PrivateChat getPrivateChat() {
		return privateChat;
	}

	public void setPrivateChat(PrivateChat privateChat) {
		this.privateChat = privateChat;
	}

	public int getTotalPending() {
		return totalPending;
	}

	public void setTotalPending(int totalPending) {
		this.totalPending = totalPending;
	}

	@Override
	public String toString() {
		String str = this.getId() + " : " + this.getClient().getId() + " : " + this.latestMessage.getBody() + " : "
				+ this.privateChat.getId() + " : " + this.totalPending;
		return str;
	}
}
