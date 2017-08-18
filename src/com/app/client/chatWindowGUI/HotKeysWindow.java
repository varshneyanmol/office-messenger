package com.app.client.chatWindowGUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.GridBagConstraints;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Insets;

public class HotKeysWindow extends JFrame {

	private JPanel contentPane;
	private JTable table;

	public HotKeysWindow() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(400, 450);
		setLocationRelativeTo(null);
		setResizable(false);
		setTitle("Manage hot keys");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 20, 320, 50, 10 };
		gbl_contentPane.rowHeights = new int[] { 10, 40, 340, 50, 10 };
		gbl_contentPane.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		table = new JTable();
		GridBagConstraints gbc_table = new GridBagConstraints();
		gbc_table.insets = new Insets(0, 0, 0, 5);
		gbc_table.fill = GridBagConstraints.BOTH;
		gbc_table.gridx = 1;
		gbc_table.gridy = 2;
		gbc_table.gridwidth = 2;
		contentPane.add(table, gbc_table);

		JButton btnAddHotKey = new JButton();
		btnAddHotKey.setIcon(new ImageIcon("src/com/app/client/resources/icons/plus.png"));
		btnAddHotKey.setBorder(BorderFactory.createEmptyBorder());
		btnAddHotKey.setContentAreaFilled(false);

		GridBagConstraints gbc_btnAddHotKey = new GridBagConstraints();
		gbc_btnAddHotKey.gridx = 2;
		gbc_btnAddHotKey.gridy = 1;
		contentPane.add(btnAddHotKey, gbc_btnAddHotKey);

		JButton btnSave = new JButton("Save");
		GridBagConstraints gbc_btnSave = new GridBagConstraints();
		gbc_btnSave.gridx = 2;
		gbc_btnSave.gridy = 3;
		contentPane.add(btnSave, gbc_btnSave);

		setVisible(true);
	}
}
