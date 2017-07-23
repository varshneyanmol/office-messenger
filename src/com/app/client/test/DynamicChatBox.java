package com.app.client.test;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class DynamicChatBox extends JFrame {

	private JPanel contentPane;

	public DynamicChatBox() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int width = 500;
		int height = 450;
		setSize(width, height);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 440, 60 };
		gbl_contentPane.rowHeights = new int[] { 300, 100 };
		gbl_contentPane.columnWeights = new double[] { 1.0, 0.0 };
		gbl_contentPane.rowWeights = new double[] { 1.0, 1.0 };
		contentPane.setLayout(gbl_contentPane);

		JPanel chatPanel = new JPanel();
		JScrollPane scroll = new JScrollPane(chatPanel);
		// chatPanel.setBackground(Color.GRAY);

		GridBagLayout gbl_chatPanel = new GridBagLayout();
		gbl_chatPanel.columnWidths = new int[] { 10, 160, 130, 160, 10 };
		gbl_chatPanel.rowHeights = new int[] { 0 };
		gbl_chatPanel.columnWeights = new double[] { Double.MIN_VALUE };
		gbl_chatPanel.rowWeights = new double[] { Double.MIN_VALUE };
		chatPanel.setLayout(gbl_chatPanel);

		GridBagConstraints gbc_chatPanel = new GridBagConstraints();
		gbc_chatPanel.insets = new Insets(0, 0, 0, 0);
		gbc_chatPanel.fill = GridBagConstraints.BOTH;
		gbc_chatPanel.gridx = 0;
		gbc_chatPanel.gridy = 0;
		gbc_chatPanel.gridwidth = 2;
		contentPane.add(scroll, gbc_chatPanel);

		/**
		 * 
		 */
		// JTextArea server = new JTextArea();
		// GridBagConstraints gbc_server = new GridBagConstraints();
		// gbc_server.insets = new Insets(10, 0, 10, 0);
		// gbc_server.fill = GridBagConstraints.HORIZONTAL;
		// gbc_server.anchor = GridBagConstraints.EAST;
		// gbc_server.gridx = 1;
		// server.setLineWrap(true);
		// server.setEditable(false);
		// server.setOpaque(true);
		// server.setWrapStyleWord(true);
		// // server.setPreferredSize(new Dimension(200, 100));
		// // server.setText(
		// // "Registered with Server /127.0.0.1:7890 with user name 'anmol_e1'.
		// server.setText("SERVER");
		// // You can now use our services.");
		// server.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		// server.setBackground(new Color(196, 55, 37));
		// server.setForeground(Color.WHITE);
		// // server.setFont(server.getFont().deriveFont(Font.BOLD));
		// server.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		// gbc_server.gridwidth = 3;
		// // gbc_button3.gridy = 0;
		// chatPanel.add(server, gbc_server);

		JTextArea area1 = new JTextArea();
		GridBagConstraints gbc_area1 = new GridBagConstraints();
		gbc_area1.insets = new Insets(10, 0, 10, 0);
		gbc_area1.fill = GridBagConstraints.HORIZONTAL;
		// gbc_area1.anchor = GridBagConstraints.EAST;
		gbc_area1.gridx = 2;
		area1.setLineWrap(true);
		area1.setEditable(false);
		area1.setOpaque(true);
		area1.setWrapStyleWord(true);
		// area1.setPreferredSize(new Dimension(200, 100));
		area1.setText("hi there\nlong time no see\nwhere have you been");
		area1.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		area1.setBackground(new Color(177, 188, 204));
		gbc_area1.gridwidth = 2;
		gbc_area1.weightx = 0;
		// gbc_button3.gridy = 0;
		// System.out.println("SIZE: " + area1.getSize());
		chatPanel.add(area1, gbc_area1);

		JTextArea area2 = new JTextArea();
		GridBagConstraints gbc_area2 = new GridBagConstraints();
		gbc_area2.insets = new Insets(10, 0, 10, 0);
		gbc_area2.fill = GridBagConstraints.HORIZONTAL;
		gbc_area2.anchor = GridBagConstraints.WEST;
		gbc_area2.gridx = 1;
		area2.setLineWrap(true);
		area2.setWrapStyleWord(true);
		area2.setText("Yo man\nWill get back Will get back Will get back Will get backWill get backWill get back");
		area2.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		// area2.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1,
		// Color.BLACK));
		// area2.setMargin(new Insets(5, 7, 5, 5));
		area2.setBorder(BorderFactory.createTitledBorder("Anmol"));
		area2.setBackground(new Color(201, 229, 219));
		gbc_area2.gridwidth = 2;
		gbc_area2.weightx = 1;
		// gbc_button3.gridy = 0;
		chatPanel.add(area2, gbc_area2);

		JTextArea area3 = new JTextArea();
		GridBagConstraints gbc_area3 = new GridBagConstraints();
		gbc_area3.insets = new Insets(10, 0, 10, 0);
		gbc_area3.fill = GridBagConstraints.HORIZONTAL;
		// gbc_area3.anchor = GridBagConstraints.EAST;
		gbc_area3.gridx = 2;
		area3.setMinimumSize(new Dimension(308, 100));
		area3.setLineWrap(true);
		area3.setWrapStyleWord(true);
		area3.setText(
				"hi there have youhave you have you have you have you have you \nlong time no see\nwhere have you been");
		area3.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		area3.setBackground(new Color(177, 188, 204));
		gbc_area3.gridwidth = 2;
		gbc_area3.weightx = 0;
		// gbc_button3.gridy = 0;
		chatPanel.add(area3, gbc_area3);

		JTextArea area4 = new JTextArea();
		GridBagConstraints gbc_area4 = new GridBagConstraints();
		gbc_area4.insets = new Insets(10, 0, 10, 0);
		gbc_area4.fill = GridBagConstraints.HORIZONTAL;
		// gbc_area4.anchor = GridBagConstraints.WEST;
		gbc_area4.gridx = 1;
		area4.setWrapStyleWord(true);
		area4.setLineWrap(true);
		area4.setText(
				"hi there\nlong time no see see see see see see long time long time long time\nwhere have you been");
		area4.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		area4.setBackground(new Color(201, 229, 219));
		gbc_area4.gridwidth = 2;
		gbc_area4.weightx = 1;
		// gbc_button3.gridy = 0;
		chatPanel.add(area4, gbc_area4);

		// JLabel label1 = new JLabel();
		// GridBagConstraints gbc_label1 = new GridBagConstraints();
		// gbc_label1.insets = new Insets(10, 0, 10, 0);
		// // gbc_label1.fill = GridBagConstraints.HORIZONTAL;
		// // gbc_label1.anchor = GridBagConstraints.WEST;
		// gbc_label1.gridx = 1;
		// label1.setMaximumSize(new Dimension(290, 100));
		// label1.setText(
		// "<html>hi there<br >where have you been where have you been where
		// have you been where have you been where have you been where have you
		// been</html>");
		// label1.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		// label1.setBackground(new Color(201, 229, 219));
		// gbc_label1.gridwidth = 2;
		// // gbc_button3.gridy = 0;
		// chatPanel.add(label1, gbc_label1);

		boolean flag = false;
		for (int i = 0; i < 10; i++) {
			JTextArea area = new JTextArea();

			GridBagConstraints gbc_area = new GridBagConstraints();
			gbc_area.insets = new Insets(0, 0, 5, 5);
			gbc_area.fill = GridBagConstraints.HORIZONTAL;

			Font font = area.getFont();
			area.setFont(font.deriveFont(Font.BOLD));

			flag = !flag;
			if (flag) {
				gbc_area.gridx = 2;
				area.setText("hi there\nlong time no see\nwhere have you been");
				area.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
				area.setBackground(new Color(156, 171, 193));
			} else {
				gbc_area.gridx = 1;
				area.setText("Yo man\nWill get back in a minute");
				// gbc_area.gridy = GridBagConstraints.RELATIVE;
				area.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
				area.setBackground(new Color(201, 229, 219));
			}
			gbc_area.gridwidth = 2;
			// gbc_button3.gridy = 0;
			chatPanel.add(area, gbc_area);
		}
		// chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.PAGE_AXIS));
		// boolean flag = true;
		//
		// JTextArea area1 = new JTextArea();
		// area1.setMaximumSize(new Dimension(100, 80));
		// area1.setText("dddd\nhhhhhh");
		// // area1.setAlignmentX(Component.LEFT_ALIGNMENT);
		// chatPanel.add(area1);
		//
		// // chatPanel.add(Box.createHorizontalGlue());
		// chatPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		//
		// JTextArea area2 = new JTextArea();
		// area2.setMaximumSize(new Dimension(150, 20));
		// area2.setText("fafasfasfasf");
		// area2.setAlignmentX(Component.RIGHT_ALIGNMENT);
		// chatPanel.add(area2);

		// for (int i = 0; i < 30; i++) {
		// JTextArea area = new JTextArea();
		// if (flag) {
		// // area.setMaximumSize(new Dimension(150, 80));
		// area.setText("dddddddddd");
		// // area.setAlignmentX(Component.RIGHT_ALIGNMENT);
		// chatPanel.add(area);
		// } else {
		// // area.setMaximumSize(new Dimension(150, 80));
		// area.setText("cccccccccc");
		// area.setSize(new Dimension(200, 50));
		// // area.setAlignmentX(Component.LEFT_ALIGNMENT);
		// chatPanel.add(area);
		// }
		// flag = !flag;
		// // chatPanel.add(Box.createHorizontalGlue());
		// chatPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		// }

		// chatPanel.setLayout(new GridLayout(0, 1, 0, 0));
		//
		// for (int i = 0; i < 25; i++) {
		// JButton button = new JButton("CLick");
		// button.setSize(200, 300);
		// button.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 0));
		// // button.setMargin(new Insets(20, 20, 20, 20));
		// chatPanel.add(button);
		// }
		// for (int i = 0; i < 25; i++) {
		// JButton button = new JButton("CLick");
		// button.setSize(200, 100);
		// // button.setMargin(new Insets(20, 20, 20, 20));
		// chatPanel.add(button);
		// }

		JTextArea txtrMessagearea = new JTextArea();
		txtrMessagearea.setText("messageArea");
		GridBagConstraints gbc_txtrMessagearea = new GridBagConstraints();
		gbc_txtrMessagearea.insets = new Insets(0, 0, 0, 5);
		gbc_txtrMessagearea.fill = GridBagConstraints.BOTH;
		gbc_txtrMessagearea.gridx = 0;
		// gbc_txtrMessagearea.gridy = 1;
		contentPane.add(txtrMessagearea, gbc_txtrMessagearea);

		JButton btnSend = new JButton("send");
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.gridx = 1;
		// gbc_btnSend.gridy = 1;
		contentPane.add(btnSend, gbc_btnSend);

		setVisible(true);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new DynamicChatBox();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
