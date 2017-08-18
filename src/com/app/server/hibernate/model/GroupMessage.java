package com.app.server.hibernate.model;

import java.util.Date;

import com.app.server.hibernate.model.Group;

public class GroupMessage extends Message {
	private Group group;

	public GroupMessage() {

	}

	public GroupMessage(String body, Date time, RegisteredClient sender, Group group) {
		super(body, time, sender);
		this.group = group;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	@Override
	public String toString() {
		String str = null;
		if (this.getParent() != null) {
			str = this.getId() + " " + this.getParent().getId() + " " + this.getBody() + " " + this.getSender().getId()
					+ " " + this.group.getId();
		} else {
			str = this.getId() + " " + "NULL" + " " + this.getBody() + " " + this.getSender().getId() + " "
					+ this.group.getId();

		}
		return str;
	}

}
