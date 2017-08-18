package com.app.server;

import java.util.Date;
import java.util.ResourceBundle;

import com.app.server.hibernate.dao.ServerDao;
import com.app.server.hibernate.model.Message;
import com.app.server.hibernate.model.PendingPrivateChatForm;
import com.app.server.hibernate.model.PendingPrivateMessage;
import com.app.server.hibernate.model.PrivateMessage;
import com.app.server.hibernate.model.RegisteredClient;

public class PrivateChatManager {
	private static PrivateChatManager privateChatManager = null;
	private Server server;

	private ResourceBundle config = ResourceBundle.getBundle("com.app.config");
	private String identityIdentifier = config.getString("identity-identifier");
	private String ackIdentifier = config.getString("ack-identifier");
	private String privateChatIdentifier = config.getString("private-chat-identifier");
	private String messageIdentifier = config.getString("message-identifier");
	private String chatFormIdentifier = config.getString("chat-form-identifier");

	private PrivateChatManager(Server server) {
		this.server = server;
	}

	public static PrivateChatManager getPrivateChatManager(Server server) {
		if (privateChatManager == null) {
			privateChatManager = new PrivateChatManager(server);
		}
		return privateChatManager;
	}

	public void process(String message) {
		if (message.startsWith(messageIdentifier)) {
			/**
			 * receives a msg like:
			 * "/m/privateChatID/i/senderID/i/receiverID/i/message"
			 */
			String[] arr = message.split(messageIdentifier + "|" + identityIdentifier);
			String privateChatID = arr[1];
			String senderID = arr[2];
			String receiverID = arr[3];
			message = arr[4];

			processMessage(privateChatID, senderID, receiverID, message);
		} else if (message.startsWith(chatFormIdentifier)) {
			/**
			 * receives a msg like: "/f/senderClientID/i/receiverClientUserName"
			 */
			String[] arr = message.split(chatFormIdentifier + "|" + identityIdentifier);

			String senderClientID = arr[1];
			String receiverClientUserName = arr[2];

			formPrivateChat(senderClientID, receiverClientUserName);
		}

	}

	private void formPrivateChat(String senderClientID, String receiverClientUserName) {
		LoggedInClient sender = server.getLoggedInClient(senderClientID);
		if (sender == null) {
			return;
		}
		RegisteredClient receiver = server.getClientByUserName(receiverClientUserName);
		if (receiver == null) {
			return;
		}
		String receiverID = receiver.getId();

		String privateChatID1 = sender.getClient().getId() + "_" + receiverID;
		String privateChatID2 = receiverID + "_" + sender.getClient().getId();
		PrivateChat privateChat;
		privateChat = ServerDao.fetchPrivateChat(privateChatID1, privateChatID2);
		if (privateChat == null) {
			privateChat = new PrivateChat(privateChatID1, sender.getClient(), receiver, new Date());
			if (ServerDao.savePrivateChat(privateChat)) {
				System.out.println("PRIVATE CHAT SAVED");
			} else {
				System.out.println("PRIVATE CHAT NOT SAVED");
			}
		}

		/**
		 * sends a ack to the sender like:
		 * "/p//a/privateChatID/i/receiverID/i/receiverUserName"
		 */
		String messageToSender = privateChatIdentifier + ackIdentifier + privateChat.getId() + identityIdentifier
				+ receiverID + identityIdentifier + receiverClientUserName;
		server.send(messageToSender, sender.getIp(), sender.getPort());

		LoggedInClient receiverLoggedIn = server.getLoggedInClient(receiver.getId());
		if (receiverLoggedIn != null) {
			/**
			 * sends a msg to the receiver like:
			 * "/p//f/privateChatID/i/senderClientID/i/senderClientUserName"
			 */
			String messageToReceiver = privateChatIdentifier + chatFormIdentifier + privateChat.getId()
					+ identityIdentifier + senderClientID + identityIdentifier + sender.getClient().getUserName();
			server.send(messageToReceiver, receiverLoggedIn.getIp(), receiverLoggedIn.getPort());

		} else {
			// client is not logged in
			PendingPrivateChatForm pendingPrivateChatForm = new PendingPrivateChatForm(receiver, privateChat);
			ServerDao.savePendingPrivateChatForm(pendingPrivateChatForm);

		}

	}

	public void processMessage(String privateChatID, String senderID, String receiverID, String message) {
		RegisteredClient sender = ServerDao.fetchClient(senderID);

		PrivateChat privateChat = ServerDao.fetchPrivateChat(privateChatID);
		if (privateChat == null) {
			System.out.println("PRIVATE CHAT RETURNED NULL");
			return;
		}
		RegisteredClient receiver;
		if (sender.getId().equals(privateChat.getClient1().getId())) {
			receiver = privateChat.getClient2();
		} else {
			receiver = privateChat.getClient1();
		}

		Message parent = ServerDao.fetchParent(privateChat);
		PrivateMessage messageDB = new PrivateMessage(message, new Date(), sender, privateChat);
		messageDB.setParent(parent);

		if (ServerDao.savePrivateMessage(messageDB)) {
			System.out.println("PRIVATE MESSAGE SAVED");
		} else {
			System.out.println("PRIVATE MESSAGE NOT SAVED");
		}

		LoggedInClient receiverLoggedIn = server.getLoggedInClient(receiver.getId());
		if (receiverLoggedIn != null) {
			/**
			 * sends a msg like: "/p//m/privateChatID/i/message"
			 */
			message = privateChatIdentifier + messageIdentifier + privateChatID + identityIdentifier + message;
			System.out.println("PENDING PRIVATE MESSAGE SENT: " + message);
			server.send(message, receiverLoggedIn.getIp(), receiverLoggedIn.getPort());

		} else {
			// client is not logged in
			PendingPrivateMessage pendingPrivateMessage = ServerDao.fetchPendingPrivateMessage(privateChat, true);
			if (pendingPrivateMessage == null) {
				System.out.println("pendingPrivateMessage FETCHED NULL");
				// pending private chat does not exist
				pendingPrivateMessage = new PendingPrivateMessage(receiver, privateChat, messageDB);
				pendingPrivateMessage.setTotalPending(1);
				ServerDao.savePendingPrivateMessage(pendingPrivateMessage);
			} else {
				pendingPrivateMessage.toString();
				ServerDao.updatePendingPrivateMessage(pendingPrivateMessage, messageDB,
						pendingPrivateMessage.getTotalPending() + 1);
			}
		}

	}

	public void sendPrivateChatFormAck(PrivateChat privateChat, LoggedInClient loggedInClient) {
		/**
		 * sends a msg to the receiver like:
		 * "/p//f/privateChatID/i/senderClientID/i/senderClientUserName"
		 */

		if (privateChat.getClient1() == null) {
			System.out.println("sendPrivateChat.getClient fetched null ");
			return;
		}
		String messageToReceiver = privateChatIdentifier + chatFormIdentifier + privateChat.getId() + identityIdentifier
				+ privateChat.getClient1().getId() + identityIdentifier + privateChat.getClient1().getUserName();
		System.out.println("SENT: " + messageToReceiver);
		server.send(messageToReceiver, loggedInClient.getIp(), loggedInClient.getPort());
	}

	public void sendMessage(PrivateMessage privateMessage, LoggedInClient loggedInClient) {
		/**
		 * sends a msg like: "/p//m/privateChatID/i/message"
		 */
		String message = privateChatIdentifier + messageIdentifier + privateMessage.getPrivateChat().getId()
				+ identityIdentifier + privateMessage.getBody();
		// System.out.println("PENDING PRIVATE MESSAGE SENT: " + message);
		server.send(message, loggedInClient.getIp(), loggedInClient.getPort());

	}
}
