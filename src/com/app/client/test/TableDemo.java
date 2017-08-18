package com.app.client.test;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

public class TableDemo extends JFrame {

	private JPanel contentPane;
	private JTable table;
	ImageIcon toDownloadIcon = new ImageIcon("src/com/app/client/resources/icons/toDownload.png");
	ImageIcon downloadedIcon = new ImageIcon("src/com/app/client/resources/icons/downloaded.png");

	public TableDemo() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 400);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		createTable();
		JScrollPane scroll = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		contentPane.add(scroll);
	}

	private void createTable() {
		String[] cols = { "status", "uploaded by", "file name", "time" };
		Object[][] data = { { toDownloadIcon, "anmol_e1", "file1", new Date().toString() },
				{ downloadedIcon, "sug_e2", "file2", new Date().toString() }, { downloadedIcon, "sug_e2", "file3", new Date().toString() } };
		TableDemoModel model = new TableDemoModel(data, cols);
		table = new JTable(model);

		Object[] arr = new Object[4];
		for (int i = 0; i < 10; i++) {
			if (i < 5) {
				arr[0] = toDownloadIcon;
			} else {
				arr[0] = downloadedIcon;
			}
			arr[1] = "sug_e2";
			arr[2] = "file " + (i + 4);
			arr[3] = new Date().toLocaleString();
			model.addRow(arr);
		}
		table.getColumnModel().getColumn(0).setMaxWidth(57);
		table.setShowVerticalLines(false);
		table.setRowHeight(50);
		table.setFocusable(false);

	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TableDemo frame = new TableDemo();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
