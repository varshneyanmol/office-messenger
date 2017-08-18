package com.app.client.chatWindowGUI;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.app.client.Client;
import com.app.client.chatWindowGUI.list.RegisteredClientsList;
import com.app.client.chatWindowGUI.list.RegisteredClientsListEventHandler;

public class BroadcastPanel {
	private Client client;
	private JPanel panel;

	private RegisteredClientsList registeredClientslist;

	public BroadcastPanel(Client client) {
		this.client = client;
		panel = new JPanel();
		createLayout();
	}

	private void createLayout() {
		GridBagLayout gbl_broadcastPanel = new GridBagLayout();
		gbl_broadcastPanel.columnWidths = new int[] { 0, 0 };
		gbl_broadcastPanel.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_broadcastPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_broadcastPanel.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		panel.setLayout(gbl_broadcastPanel);

		JLabel lblBroadcastUsersList = new JLabel("Broadcast Users List");
		GridBagConstraints gbc_lblBroadcastUsersList = new GridBagConstraints();
		gbc_lblBroadcastUsersList.insets = new Insets(0, 0, 5, 0);
		gbc_lblBroadcastUsersList.gridx = 0;
		gbc_lblBroadcastUsersList.gridy = 0;
		panel.add(lblBroadcastUsersList, gbc_lblBroadcastUsersList);

		registeredClientslist = new RegisteredClientsList(client);
		registeredClientslist.getList().addMouseListener(new RegisteredClientsListEventHandler(client));

		JScrollPane listScroll = new JScrollPane(registeredClientslist.getList());
		GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.fill = GridBagConstraints.BOTH;
		gbc_list.gridx = 0;
		gbc_list.gridy = 2;
		panel.add(listScroll, gbc_list);
	}

	public JPanel getPanel() {
		return this.panel;
	}

	public void updateClient(String clientUserName, boolean isOnline) {
		registeredClientslist.updateItem(clientUserName, isOnline);
	}

	public void logoutAllFromLists() {
		registeredClientslist.logoutAllFromLists();
	}

	public String getSelectedValues() {
		String members = "";
		members = registeredClientslist.getSelectedUserNames();
		return members;
	}
}
