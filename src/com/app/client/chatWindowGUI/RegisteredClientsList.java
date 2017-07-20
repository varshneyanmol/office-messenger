package com.app.client.chatWindowGUI;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;

public class RegisteredClientsList {
	private JList<String> registeredUsersList;
	private DefaultListModel<String> registeredUsersModel;
	private String[] arr = { "hello", "amigos", "sayonaya", "mochi cochi" };

	public RegisteredClientsList() {
		registeredUsersModel = new DefaultListModel<String>();
		registeredUsersList = new JList<String>(registeredUsersModel);
		// for (int i = 0; i < arr.length; i++) {
		// registeredUsersModel.addElement(arr[i]);
		// }
	}

	public void addClient(String clientUserName) {
		registeredUsersModel.add(0, clientUserName);
	}

	public void addClientToEnd(String clientUserName) {
		registeredUsersModel.addElement(clientUserName);
	}

	public boolean removeClient(String clientUserName) {
		return registeredUsersModel.removeElement(clientUserName);
	}

	public void clear() {
		registeredUsersModel.clear();
	}

	public JList<String> getList() {
		return registeredUsersList;
	}
}
