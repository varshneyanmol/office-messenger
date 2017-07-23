package com.app.client.chat;

import java.util.ArrayList;

import javax.swing.JPanel;

import com.app.client.Client;
import com.app.client.chat.Chat;
import com.app.client.chatWindowGUI.ChatPanel;

public class Group extends Chat {
	private int id;
	private String name;
	private ArrayList<String> members;
	private String creatorUserName;

	public Group(int id, String name, String creatorUserName, ArrayList<String> members, Client client) {
		super(client);
		this.id = id;
		this.creatorUserName = creatorUserName;
		createChatPanel(this);
		this.name = name;
		this.members = members;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public ArrayList<String> getMembers() {
		return members;
	}

}
