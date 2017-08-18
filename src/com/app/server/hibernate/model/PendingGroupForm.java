package com.app.server.hibernate.model;

import com.app.server.LoggedInClient;
import com.app.server.hibernate.model.Group;;

public class PendingGroupForm extends Pending {
	private Group group;

	public PendingGroupForm() {
	}

	public PendingGroupForm(RegisteredClient client, Group group) {
		super(client);
		this.group = group;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}
}
