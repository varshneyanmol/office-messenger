package com.app.client.group;

import java.util.ArrayList;

import javax.swing.JPanel;

import com.app.client.Client;
import com.app.client.chatWindowGUI.ChatPanel;

public class Group {
	private Client client;
	private int id;
	private String name;
	private ArrayList<Integer> members;
	private JPanel panel;

	public Group(String name, ArrayList<Integer> members, Client client) {
		this.name = name;
		this.members = members;
		this.client = client;
		panel = new ChatPanel(client).getPanel();
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

	public JPanel chatPanel() {
		return panel;
	}
}
