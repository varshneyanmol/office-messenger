package com.app.client.chatWindowGUI.list;

import java.util.List;

import javax.swing.*;

import com.app.client.Client;

public class ListWithIcons {
	private Client client;
	private JList<ListEntryItem> list;
	private DefaultListModel<ListEntryItem> listModel;

	ImageIcon trueStatusIcon = null;
	ImageIcon falseStatusIcon = null;

	public ListWithIcons(Client client) {
		this.client = client;
		listModel = new DefaultListModel<ListEntryItem>();
		list = new JList<ListEntryItem>(listModel);
		list.setCellRenderer(new ListRenderer(false));
		list.setFixedCellHeight(25);
	}

	public ListWithIcons(Client client, ListCellRenderer<Object> cellRenderer, int cellHeight) {
		this.client = client;
		listModel = new DefaultListModel<ListEntryItem>();
		list = new JList<ListEntryItem>(listModel);
		list.setCellRenderer(cellRenderer);
		list.setFixedCellHeight(cellHeight);
	}

	public void addItem(String text, boolean status) {
		ListEntryItem item;
		if (status) {
			item = new ListEntryItem(text, trueStatusIcon);
			item.setStatus(true);
			listModel.add(0, item);
		} else {
			item = new ListEntryItem(text, falseStatusIcon);
			item.setStatus(false);
			listModel.addElement(item);
		}
	}

	public void clear() {
		listModel.clear();
	}

	public void updateItem(String text, boolean status) {
		ListEntryItem item = getItem(text);
		if (item == null) {
			addItem(text, status);
		} else {
			if (item.getStatus() == status) {
				return;
			} else {
				toggleStatus(item);
			}
		}
	}

	protected void toggleStatus(ListEntryItem item) {
		if (item.getStatus()) {
			listModel.removeElement(item);
			item.setStatus(false);
			item.setIcon(falseStatusIcon);
			listModel.addElement(item);
		} else {
			listModel.removeElement(item);
			item.setStatus(true);
			item.setIcon(trueStatusIcon);
			listModel.add(0, item);
		}
	}

	private ListEntryItem getItem(String text) {
		ListEntryItem item = null;
		for (int i = 0; i < listModel.getSize(); i++) {
			if (listModel.get(i).getText().equals(text)) {
				item = listModel.get(i);
				break;
			}
		}
		return item;
	}

	public JList<ListEntryItem> getList() {
		return list;
	}

	public List<ListEntryItem> getSelectedValues() {
		return list.getSelectedValuesList();
	}

	public Client getClient() {
		return client;
	}

	public DefaultListModel<ListEntryItem> getListModel() {
		return listModel;
	}

	public void setTrueStatusIcon(ImageIcon trueStatusIcon) {
		this.trueStatusIcon = trueStatusIcon;
	}

	public void setFalseStatusIcon(ImageIcon falseStatusIcon) {
		this.falseStatusIcon = falseStatusIcon;
	}
}
