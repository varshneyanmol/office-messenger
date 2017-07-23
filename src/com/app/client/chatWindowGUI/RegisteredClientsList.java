package com.app.client.chatWindowGUI;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;

import com.app.client.Client;

public class RegisteredClientsList {
	private Client client;
	private JList<ListEntryItem> registeredUsersList;
	private DefaultListModel<ListEntryItem> registeredUsersModel;

	ImageIcon onlineClientIcon = new ImageIcon("src/com/app/client/resources/icons/onlineClient.png");
	ImageIcon offlineClientIcon = new ImageIcon("src/com/app/client/resources/icons/offlineClient.png");

	public RegisteredClientsList(Client client) {
		this.client = client;
		registeredUsersModel = new DefaultListModel<ListEntryItem>();
		registeredUsersList = new JList<ListEntryItem>(registeredUsersModel);
		registeredUsersList.setCellRenderer(new ListRenderer());
		registeredUsersList.setFixedCellHeight(25);
	}

	public void addClient(String clientUserName, boolean isOnline) {
		ListEntryItem item;
		if (isOnline) {
			item = new ListEntryItem(clientUserName, onlineClientIcon);
			item.setOnline(true);
			registeredUsersModel.add(0, item);
		} else {
			item = new ListEntryItem(clientUserName, offlineClientIcon);
			item.setOnline(false);
			registeredUsersModel.addElement(item);
		}
	}

	public void clear() {
		registeredUsersModel.clear();
	}

	public void updateClient(String clientUserName, boolean isOnline) {
		ListEntryItem item = getItem(clientUserName);
		if (item == null) {
			addClient(clientUserName, isOnline);
		} else {
			if (item.getOnline() == isOnline) {
				return;
			} else {
				toggleClientStatus(item);
			}
		}
	}

	private void toggleClientStatus(ListEntryItem item) {
		if (item.getOnline()) {
			registeredUsersModel.removeElement(item);
			item.setOnline(false);
			item.setIcon(offlineClientIcon);
			registeredUsersModel.addElement(item);
		} else {
			registeredUsersModel.removeElement(item);
			item.setOnline(true);
			item.setIcon(onlineClientIcon);
			registeredUsersModel.add(0, item);
		}
	}

	private ListEntryItem getItem(String clientUserName) {
		ListEntryItem item = null;
		for (int i = 0; i < registeredUsersModel.getSize(); i++) {
			if (registeredUsersModel.get(i).getText().equals(clientUserName)) {
				item = registeredUsersModel.get(i);
				break;
			}
		}
		return item;
	}

	public JList<ListEntryItem> getList() {
		return registeredUsersList;
	}

	public void logoutAllFromLists() {
		for (int i = 0; i < registeredUsersModel.getSize(); i++) {
			ListEntryItem item = registeredUsersModel.get(0);
			if (item.getOnline()) {
				toggleClientStatus(item);
			} else {
				/**
				 * Lists are being displayed in a manner like all the logged in
				 * clients will be displayed first then all the logged out
				 * clients. SO, if the first logged out client is found in the
				 * list then it is guaranteed that no other elements below it
				 * will be logged in. So I do not need to reverse their status
				 * and I will simply return.
				 */
				return;
			}
		}
	}

	public String getSelectedUserNames() {
		String members = "";
		List<ListEntryItem> selectedItems = registeredUsersList.getSelectedValuesList();
		if (!selectedItems.isEmpty()) {
			if (selectedItems.size() == 1 && selectedItems.get(0).getText().equals(client.getUserName())) {
				return members;
			}
			boolean flag = true;
			for (int i = 0; i < selectedItems.size(); i++) {
				ListEntryItem item = selectedItems.get(i);
				if (item.getText().equals(client.getUserName())) {
					flag = false;
				}
				members = members + item.getText() + ",";
			}
			if (flag) {
				members = client.getUserName() + "," + members;
			}
		}
		return members;
	}
}
