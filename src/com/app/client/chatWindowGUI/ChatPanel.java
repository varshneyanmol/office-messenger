package com.app.client.chatWindowGUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.app.client.Client;
import com.app.client.chat.Broadcast;
import com.app.client.chat.Chat;
import com.app.client.chat.Group;
import com.app.client.chat.PrivateChat;

public class ChatPanel {
	private Client client;
	private Chat chat = null;
	private JPanel panel;

	// private JTextPane chatHistory;
	private JPanel chatHistory;
	private JTextField messageField;

	private ResourceBundle config = ResourceBundle.getBundle("com.app.config");
	private String broadcastIdentifier = config.getString("broadcast-identifier");
	private String groupIdentifier = config.getString("group-identifier");
	private String identityIdentifier = config.getString("identity-identifier");

	public ChatPanel(Client client) {
		this.client = client;
		panel = new JPanel();
		createLayout();
	}

	public ChatPanel(Client client, Chat chat) {
		this.client = client;
		this.chat = chat;
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

		// chatHistory = new JTextPane();
		// chatHistory.setFont(new Font("VERDANA", Font.PLAIN, 14));
		// chatHistory.setForeground(Color.DARK_GRAY);
		// chatHistory.setEditable(false);
		// JScrollPane scrollPane = new JScrollPane(chatHistory);

		chatHistory = new JPanel();
		JScrollPane scrollPane = new JScrollPane(chatHistory);

		GridBagLayout gbl_chatHistory = new GridBagLayout();
		gbl_chatHistory.columnWidths = new int[] { 10, 130, 160, 130, 10 };
		gbl_chatHistory.rowHeights = new int[] { 0 };
		gbl_chatHistory.columnWeights = new double[] { Double.MIN_VALUE };
		gbl_chatHistory.rowWeights = new double[] { Double.MIN_VALUE };
		chatHistory.setLayout(gbl_chatHistory);

		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 0, 0);
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

		JButton btnSend = new JButton();
		btnSend.setIcon(new ImageIcon("src/com/app/client/resources/icons/send.png"));
		btnSend.setBorder(BorderFactory.createEmptyBorder());
		btnSend.setContentAreaFilled(false);
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage(messageField.getText());
			}
		});
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Point point = btnSend.getLocation();
				btnSend.setLocation(new Point(point.x + 4, point.y));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				Point point = btnSend.getLocation();
				btnSend.setLocation(new Point(point.x - 4, point.y));
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
		if (message.equals("")) {
			return;
		}
		if (chat != null) {
			if (chat instanceof PrivateChat) {
				/**
				 * In case of private chat, We print message to the chat panel
				 * before sending to the server because server sends the message
				 * only to the receiver. But in case of broadcast or group chat,
				 * we just send the message to the server and then server sends
				 * the message to everybody in the group including the client
				 * which has sent the message.
				 */
				console(message, client.getUserName());
			}
			client.sendMessage(message, chat);
		}
	}

	public void console(String message, String userName) {
		System.out.println("IN CONSOLE");
		JTextArea area = new JTextArea();
		area.setEditable(false);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setText(message);
		area.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		GridBagConstraints gbc_area = new GridBagConstraints();
		gbc_area.insets = new Insets(5, 0, 5, 0);

		if (chat instanceof PrivateChat) {
			if (client.getUserName().equals(userName)) {
				area.setBackground(new Color(177, 188, 204));
				gbc_area.gridx = 2;

			} else {
				area.setBackground(new Color(201, 229, 219));
				gbc_area.gridx = 1;

			}
		} else if (chat instanceof Broadcast) {
			System.out.println("IN BROADCAST IF CONDITION");
			if (client.getUserName().equals(userName)) {
				System.out.println("IN BROADcast if condition with: " + client.getUserName());
				area.setBackground(new Color(177, 188, 204));
				gbc_area.gridx = 2;

			} else {
				area.setBackground(new Color(201, 229, 219));
				gbc_area.gridx = 1;
				area.setBorder(BorderFactory.createTitledBorder(userName));
			}
		} else if (chat instanceof Group) {

		}

		gbc_area.fill = GridBagConstraints.HORIZONTAL;
		// gbc_area1.anchor = GridBagConstraints.EAST;
		gbc_area.gridwidth = 2;
		chatHistory.add(area, gbc_area);
	}

	// public void styleBroadcastMessage(String message) {
	// /**
	// * gets a msg like: "username: message" prepares a styled message like:
	// * "username<colored|bold>: message"
	// */
	// String[] arr = message.split(":");
	// String userName = arr[0];
	// message = arr[1];
	//
	// StyledDocument doc = chatHistory.getStyledDocument();
	//
	// SimpleAttributeSet attributes = new SimpleAttributeSet();
	// attributes.addAttribute(StyleConstants.CharacterConstants.Bold,
	// Boolean.TRUE);
	// attributes.addAttribute(StyleConstants.CharacterConstants.Foreground, new
	// Color(41, 137, 46));
	//
	// console(userName + ":", attributes, false);
	// console(message, null, true);
	//
	// }
	//
	// public void console(String message, SimpleAttributeSet attributes,
	// boolean newLine) {
	// StyledDocument doc = chatHistory.getStyledDocument();
	//
	// if (newLine) {
	// message = message + "\n\n";
	// }
	// try {
	// doc.insertString(doc.getLength(), message, attributes);
	// } catch (BadLocationException e) {
	// e.printStackTrace();
	// }
	// // update the caret of the JtextArea at the latest message
	// chatHistory.setCaretPosition(doc.getLength());
	// }

	// public JTextPane getChatHistory() {
	// return chatHistory;
	// }
}
