package com.app.client.chatWindowGUI;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;

public class GroupUsersList {
	private JList<String> groupUsersList;
	private DefaultListModel<String> groupUsersModel;
	private String[] arr = { "group 1", "group 2", "group 3", "group 4" };

	public GroupUsersList() {
		groupUsersModel = new DefaultListModel<String>();
		groupUsersList = new JList<String>(groupUsersModel);
		for (int i = 0; i < arr.length; i++) {
			groupUsersModel.addElement(arr[i]);
		}
	}

	public JList<String> getList() {
		return groupUsersList;
	}
}
