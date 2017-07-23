package com.app.client.chat;

import java.awt.Color;
import java.awt.Font;

import com.app.client.Client;
import com.app.client.chat.Chat;

public class Broadcast extends Chat {
	private final int BROADCAST_ID = 0;
	private final String NAME = "Broadcast";
	private static Broadcast broadcast = null;

	private Broadcast(Client client) {
		super(client);
		createChatPanel(this);
		// getChatPanel().getChatHistory().setBackground(new Color(148, 155,
		// 168));
		// getChatPanel().getChatHistory().setForeground(Color.WHITE);
	}

	public static Broadcast getBroadcast(Client client) {
		if (broadcast == null) {
			broadcast = new Broadcast(client);
		}
		return broadcast;
	}

	public int getBROADCAST_ID() {
		return BROADCAST_ID;
	}

	public String getNAME() {
		return NAME;
	}
}
