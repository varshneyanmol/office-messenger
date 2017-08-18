package com.app.client.test;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import com.app.client.chatWindowGUI.list.ListEntryItem;

public class JTreeRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer {

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {

		ListEntryItem item = (ListEntryItem) value;
		setIcon(item.getIcon());
		setText(item.getText());

		if (sel) {
			setBackground(tree.getBackground());
			setForeground(tree.getForeground());
		} else {
			setBackground(tree.getBackground());
			setForeground(tree.getForeground());
		}
		setEnabled(true);
		setFont(tree.getFont());
		return this;
	}
}
