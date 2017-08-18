package com.app.client.chatWindowGUI;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.app.client.chatWindowGUI.list.ListEntryItem;

import com.app.client.Client;

public class GroupsPanel {
	private Client client;
	private JPanel panel;

	public GroupsPanel(Client client) {
		this.client = client;
		panel = new JPanel();
		createLayout();
	}

	private void createLayout() {
		GridBagLayout gbl_groupsPanel = new GridBagLayout();
		gbl_groupsPanel.columnWidths = new int[] { 0, 0 };
		gbl_groupsPanel.rowHeights = new int[] { 0, 0, 0 };
		gbl_groupsPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_groupsPanel.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		panel.setLayout(gbl_groupsPanel);

		JLabel lblGroupUsersList = new JLabel("Group Users List");
		GridBagConstraints gbc_lblGroupUsersList = new GridBagConstraints();
		gbc_lblGroupUsersList.insets = new Insets(0, 0, 5, 0);
		gbc_lblGroupUsersList.gridx = 0;
		gbc_lblGroupUsersList.gridy = 0;
		panel.add(lblGroupUsersList, gbc_lblGroupUsersList);

		GroupUsersList userlist = new GroupUsersList();

		// JList<String> list = userlist.getList();

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Group 1");

		ImageIcon onlineClientIcon = new ImageIcon("src/com/app/client/resources/icons/onlineClient.png");
		ImageIcon offlineClientIcon = new ImageIcon("src/com/app/client/resources/icons/offlineClient.png");

		DefaultMutableTreeNode user1 = new DefaultMutableTreeNode(new ListEntryItem("user 1", onlineClientIcon));
		DefaultMutableTreeNode user2 = new DefaultMutableTreeNode(new ListEntryItem("user 2", offlineClientIcon));

		root.add(user1);
		root.add(user2);

		JTree groupTree = new JTree(root);

		JScrollPane groupScroll = new JScrollPane(groupTree);
		GridBagConstraints gbc_list_1 = new GridBagConstraints();
		gbc_list_1.fill = GridBagConstraints.BOTH;
		gbc_list_1.gridx = 0;
		gbc_list_1.gridy = 1;
		panel.add(groupScroll, gbc_list_1);

	}

	public JPanel getPanel() {
		return this.panel;
	}

}
