package com.app.client.chat;

import java.util.ArrayList;

import javax.swing.JPanel;

import com.app.client.Client;
import com.app.client.chat.Chat;
import com.app.client.chatWindowGUI.ChatPanel;

public class Group extends Chat {
	private int id;
	private String name;
	private ArrayList<Integer> members;

	public Group(String name, ArrayList<Integer> members, Client client) {
		super(client);
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

	public ArrayList<Integer> getMembers() {
		return members;
	}

}
