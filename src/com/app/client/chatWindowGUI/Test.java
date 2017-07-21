package com.app.client.chatWindowGUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Test extends JFrame {

	private JPanel contentPane;

	public Test() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FlowLayout());

		DefaultListModel<ListEntryItem> model = new DefaultListModel<ListEntryItem>();
		JList<ListEntryItem> list = new JList<ListEntryItem>(model);

		model.addElement(
				new ListEntryItem("kuch bhi", new ImageIcon("src/com/app/client/resources/icons/onlineClient.png")));
		model.addElement(
				new ListEntryItem("kuch bhi 2", new ImageIcon("src/com/app/client/resources/icons/onlineClient.png")));
		model.addElement(
				new ListEntryItem("kuch bhi 3", new ImageIcon("src/com/app/client/resources/icons/offlineClient.png")));

		list.setCellRenderer(new ListRenderer());
		// list.setModel(model);
		list.setBounds(63, 70, 1, 1);
		contentPane.add(list);

		JButton btnPopulate = new JButton("populate");
		btnPopulate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnPopulate.setBounds(323, 33, 117, 25);
		contentPane.add(btnPopulate);

		JButton btnClear = new JButton("clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnClear.setBounds(323, 174, 117, 25);
		contentPane.add(btnClear);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Test frame = new Test();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
