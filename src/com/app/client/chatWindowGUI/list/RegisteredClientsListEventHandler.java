package com.app.client.chatWindowGUI.list;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import com.app.client.Client;
import com.app.client.chatWindowGUI.list.ListEntryItem;

public class RegisteredClientsListEventHandler extends MouseAdapter {
	private Client client;
	JList<ListEntryItem> list;
	JPopupMenu popup = new JPopupMenu();

	public RegisteredClientsListEventHandler(Client client) {
		this.client = client;
		addPopup();
	}

	private void addPopup() {
		popup.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, false));
		JMenuItem privateChat = new JMenuItem("Private Chat");
		JMenuItem closePane = new JMenuItem("Close Pane");
		popup.add(privateChat);
		popup.add(closePane);
		privateChat.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ListEntryItem item = list.getSelectedValue();
				// System.out.println("private chat with " + item.getText());
				String receiverUserName = item.getText();
				startChat(receiverUserName);
			}
		});
	}

	private void startChat(String receiverUserName) {
		client.startChat(receiverUserName);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		list = (JList<ListEntryItem>) e.getSource();
		if (SwingUtilities.isRightMouseButton(e)) {
			int index = list.locationToIndex(e.getPoint());
			list.setSelectedIndex(index);
			popup.show(list, e.getPoint().x, e.getPoint().y);

		}
	}
}
