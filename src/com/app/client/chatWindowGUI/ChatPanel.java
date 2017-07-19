package com.app.client.chatWindowGUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import com.app.client.Client;

public class ChatPanel {
	private Client client;
	private JPanel panel;

	private JTextPane chatHistory;
	private JTextField messageField;

	private ResourceBundle config = ResourceBundle.getBundle("com.app.config");
	private String broadcastIdentifier = config.getString("broadcast-identifier");
	private String groupIdentifier = config.getString("group-identifier");

	public ChatPanel(Client client) {
		this.client = client;
		panel = new JPanel();
		createLayout();
	}

	private void createLayout() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 10, 540, 50 };
		gbl_contentPane.rowHeights = new int[] { 10, 15, 340, 55 };
		panel.setLayout(gbl_contentPane);

		chatHistory = new JTextPane();
		chatHistory.setFont(new Font("VERDANA", Font.PLAIN, 14));
		chatHistory.setForeground(Color.DARK_GRAY);
		chatHistory.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(chatHistory);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		gbc_scrollPane.gridwidth = 3;
		gbc_scrollPane.gridheight = 2;
		gbc_scrollPane.weightx = 1;
		gbc_scrollPane.weighty = 1;
		panel.add(scrollPane, gbc_scrollPane);

		messageField = new JTextField();
		messageField.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					sendMessage(messageField.getText());
				}
			}
		});
		GridBagConstraints gbc_messageField = new GridBagConstraints();
		gbc_messageField.insets = new Insets(0, 0, 0, 5);
		gbc_messageField.fill = GridBagConstraints.HORIZONTAL;
		gbc_messageField.gridx = 0;
		gbc_messageField.gridy = 3;
		gbc_messageField.gridwidth = 2;
		gbc_messageField.weightx = 1;
		gbc_messageField.weighty = 0;
		panel.add(messageField, gbc_messageField);
		messageField.setColumns(10);

		JButton btnSend = new JButton("send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage(messageField.getText());
			}
		});
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.gridx = 2;
		gbc_btnSend.gridy = 3;
		gbc_btnSend.weightx = 0;
		gbc_btnSend.weighty = 0;
		panel.add(btnSend, gbc_btnSend);

		messageField.requestFocusInWindow();
	}

	public JPanel getPanel() {
		return panel;
	}

	private void sendMessage(String message) {
		messageField.setText("");
		message = message.trim();
		if (!message.equals("")) {
			message = broadcastIdentifier + message;
			client.send(message);
		}
	}

	public void console(String message) {
		StyledDocument doc = chatHistory.getStyledDocument();
		try {
			doc.insertString(doc.getLength(), message + "\n", null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		// update the caret of the JtextArea at the latest message
		chatHistory.setCaretPosition(doc.getLength());
	}

}
