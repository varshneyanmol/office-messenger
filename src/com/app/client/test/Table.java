package com.app.client.test;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class Table extends JFrame {

	private JPanel contentPane;
	private JTable table;

	public Table() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		String[] row1 = { "you", "are", "the", "angle" };
		String[] row2 = { "of", "my", "nightmare", "dear" };

		table = new JTable();
		table.setFillsViewportHeight(true);
		table.setShowGrid(false);
		table.setShowVerticalLines(false);
		table.setShowHorizontalLines(false);
		DefaultTableModel model = new DefaultTableModel(0, 4);
		table.setModel(model);

		model.insertRow(0, row1);
		model.insertRow(0, row2);
		model.addRow(row1);
		model.addRow(row2);

		Icon icon = new ImageIcon("src/com/app/client/resources/icons/onlineClient.png");
		table.setDefaultRenderer(Icon.class, new TableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				return null;

			}
		});
		table.setValueAt(icon, 0, 0);

		contentPane.add(table, BorderLayout.CENTER);
		setVisible(true);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new Table();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
