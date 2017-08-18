package com.app.server.hibernate.model;

import com.app.server.hibernate.model.Group;

public class PendingGroupMessage extends Pending {
	private Message latestMessage;
	private Group group;
	private int totalPending;

	public PendingGroupMessage() {

	}

	public PendingGroupMessage(RegisteredClient client, Group group, Message message) {
		super(client);
		this.group = group;
		this.latestMessage = message;
	}

	public Message getLatestMessage() {
		return latestMessage;
	}

	public void setLatestMessage(Message latestMessage) {
		this.latestMessage = latestMessage;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
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
				+ this.group.getId() + " : " + this.totalPending;
		return str;
	}

}
