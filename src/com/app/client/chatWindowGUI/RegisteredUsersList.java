package com.app.client.chatWindowGUI;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;

public class RegisteredUsersList {
	private JList<String> registeredUsersList;
	private DefaultListModel<String> registeredUsersModel;
	private String[] arr = { "hello", "amigos", "sayonaya", "mochi cochi" };

	public RegisteredUsersList() {
		registeredUsersModel = new DefaultListModel<String>();
		registeredUsersList = new JList<String>(registeredUsersModel);
		for (int i = 0; i < arr.length; i++) {
			registeredUsersModel.addElement(arr[i]);
		}
	}

	public JList<String> getList() {
		return registeredUsersList;
	}
}
