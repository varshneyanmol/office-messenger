package com.app.client.chatWindowGUI.list;

import javax.swing.table.DefaultTableModel;

public class TableWithIconsModel extends DefaultTableModel {

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

}
