package com.app.server.hibernate.model;

import com.app.server.PrivateChat;

public class PendingPrivateChatForm extends Pending {
	private PrivateChat privateChat;

	public PendingPrivateChatForm() {
	}

	public PendingPrivateChatForm(RegisteredClient client, PrivateChat privateChat) {
		super(client);
		this.privateChat = privateChat;
	}

	public PrivateChat getPrivateChat() {
		return privateChat;
	}

	public void setPrivateChat(PrivateChat privateChat) {
		this.privateChat = privateChat;
	}

}
