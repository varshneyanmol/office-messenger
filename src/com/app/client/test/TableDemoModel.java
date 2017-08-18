package com.app.client.test;

import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

public class TableDemoModel extends DefaultTableModel {

	ImageIcon toDownloadIcon = new ImageIcon("src/com/app/client/resources/icons/toDownload.png");
	ImageIcon downloadedIcon = new ImageIcon("src/com/app/client/resources/icons/downloaded.png");

	public TableDemoModel(Object[][] data, Object[] columnNames) {
		super(data, columnNames);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return getValueAt(0, columnIndex).getClass();
	}
}
