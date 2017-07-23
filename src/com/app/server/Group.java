package com.app.server;

import java.util.ArrayList;

public class Group {
	private int id;
	private String name;
	private ArrayList<String> memberIDs;

	public Group(int id, String name, ArrayList<String> memberIDs) {
		super();
		this.id = id;
		this.name = name;
		this.memberIDs = memberIDs;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getMemberIDs() {
		return memberIDs;
	}

	public void setMemberIDs(ArrayList<String> memberIDs) {
		this.memberIDs = memberIDs;
	}

}
