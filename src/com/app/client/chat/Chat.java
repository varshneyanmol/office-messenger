package com.app.client.chat;

import javax.swing.JPanel;

import com.app.client.Client;
import com.app.client.chatWindowGUI.ChatPanel;

public class Chat {
	private Client client;
	private ChatPanel chatPanel;
	private JPanel panel;

	public Chat(Client client) {
		this.client = client;
	}

	public void createChatPanel(Chat obj) {
		this.chatPanel = new ChatPanel(client, this);
		this.panel = this.chatPanel.getPanel();
	}

	public Client getClient() {
		return client;
	}

	public JPanel getPanel() {
		return panel;
	}

	public ChatPanel getChatPanel() {
		return this.chatPanel;
	}
}
