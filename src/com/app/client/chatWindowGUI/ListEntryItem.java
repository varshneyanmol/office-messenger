package com.app.client.chatWindowGUI;

import javax.swing.Icon;

public class ListEntryItem {
	private String text;
	private Icon icon;
	private boolean online;

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

	public boolean getOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

}
