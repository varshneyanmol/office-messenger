package com.app.client.chatWindowGUI.list;

import com.app.client.Client;

import javax.swing.*;

public class TableWithIcons {
    private Client client;
    private JTable table;
    private TableWithIconsModel tableModel;

    private ImageIcon trueIcon = null;
    private ImageIcon falseIcon = null;

    public TableWithIcons(Client client) {
        this.client = client;
        tableModel = new TableWithIconsModel();
        table = new JTable(tableModel);
        table.setShowVerticalLines(false);
        table.setRowHeight(50);
        table.setFocusable(false);
    }

    public void setColumns(String[] cols) {
        for (int i = 0; i < cols.length; i++) {
            String col = cols[i];
            tableModel.addColumn(col);
        }
        table.getColumnModel().getColumn(0).setMaxWidth(60);
    }

    public void addRow(Object[] row) {
        Object[] _row = new Object[4];
        if ((boolean) row[0])
            _row[0] = trueIcon;
        else
            _row[0] = falseIcon;

        _row[1] = row[1];
        _row[2] = row[2];
        _row[3] = row[3];

        this.tableModel.addRow(_row);
    }

    public JTable getTable() {
        return this.table;
    }

    public void setTrueIcon(ImageIcon trueIcon) {
        this.trueIcon = trueIcon;
    }

    public void setFalseIcon(ImageIcon falseIcon) {
        this.falseIcon = falseIcon;
    }

    public TableWithIcons search(String text) {
        if (text.equals("")) {
            return null;
        }

        TableWithIcons temp = new TableWithIcons(client);
        int colcount = table.getColumnCount();
        String[] cols = new String[colcount];
        for (int i = 0; i < colcount; i++) {
            cols[i] = table.getColumnName(i);
        }
        temp.setColumns(cols);

        int rowcount = table.getRowCount();
        for (int i = 0; i < rowcount; i++) {
            boolean flag = false;
            String val = (String) table.getValueAt(i, 2);
            text = text.toLowerCase();
            val = val.toLowerCase();
            if (val.startsWith(text)) {
                flag = true;
            } else if (val.contains(text)) {
                flag = true;
            }

            if (flag) {
                Object[] row = new Object[colcount];
                for (int j = 0; j < colcount; j++) {
                    row[j] = table.getValueAt(i, j);
                }
                temp.getTableModel().addRow(row);
            }
        }
        return temp;
    }

    public TableWithIconsModel getTableModel() {
        return this.tableModel;
    }

    public String[][] getFilesToDownload() {
        int[] selectedRows = this.table.getSelectedRows();
        int selectedRowsCount = this.table.getSelectedRowCount();
        if (selectedRowsCount == 0) {
            return null;
        }

        String[][] arr = new String[selectedRowsCount][2];
        for (int i = 0; i < selectedRowsCount; i++) {
            arr[i][0] = (String) this.table.getValueAt(selectedRows[i], 1);
            arr[i][1] = (String) this.table.getValueAt(selectedRows[i], 2);
        }
        return arr;
    }
}
