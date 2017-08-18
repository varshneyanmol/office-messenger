package com.app.client.chatWindowGUI.list;

import com.app.client.chatWindowGUI.list.ListEntryItem;

import java.awt.*;
import javax.swing.*;

public class ListRenderer extends DefaultListCellRenderer implements ListCellRenderer<Object> {

    boolean createBorder = false;

    public ListRenderer(boolean createBorder) {
        this.createBorder = createBorder;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {

        ListEntryItem item = (ListEntryItem) value;
        setIcon(item.getIcon());
        setText(item.getText());
        if (createBorder) {
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.lightGray));
        }

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setEnabled(true);
        setFont(list.getFont());
        return this;
    }
}
