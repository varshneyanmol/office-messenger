package com.app.client.chat;

import com.app.client.Client;
import com.app.client.chat.Chat;

public class PrivateChat extends Chat {
	private String receiverID;
	private String receiverUserName;

	public PrivateChat(String receiverID, String receiverUserName, Client client) {
		super(client);
		createChatPanel(this);
		this.receiverID = receiverID;
		this.receiverUserName = receiverUserName;
	}

	public String getReceiverID() {
		return receiverID;
	}

	public String getReceiverUserName() {
		return receiverUserName;
	}

	@Override
	public String toString() {
		return receiverID + "@" + receiverUserName;
	}
}
