package com.app.client.chatWindowGUI.list;

import javax.swing.Icon;

public class ListEntryItem {
	private String text;
	private Icon icon;
	private boolean status;

	public ListEntryItem(String text, Icon icon) {
		this.text = text;
		this.icon = icon;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

}
