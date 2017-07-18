package com.app.client;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class ClientWindow extends JFrame {

	private JPanel contentPane;

	public ClientWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 430);
		setLocationRelativeTo(null);
		setTitle("Client chat window");

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		setVisible(true);
	}

}
