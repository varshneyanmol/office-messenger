package com.app.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.JTextArea;

import com.app.client.chatWindowGUI.MainChatWindow;
import com.app.client.chat.Broadcast;
import com.app.client.chat.Chat;
import com.app.client.chat.Group;
import com.app.client.chat.PrivateChat;

public class Client {
	private boolean running = false;
	private String id;
	private String name;
	private String userName;
	private String designation;
	private InetAddress serverIP;
	private int serverPort;
	private ClientNetworking clientNetworking;
	private PrivateChat privateChat;
	private Group group;

	// private final int BROADCAST_GROUP_ID = 0;

	private ResourceBundle config = ResourceBundle.getBundle("com.app.config");
	private String registerIdentifier = config.getString("register-identifier");
	private String loginIdentifier = config.getString("login-identifier");
	private String ackIdentifier = config.getString("ack-identifier");
	private String logoutIdentifier = config.getString("logout-identifier");
	private String identityIdentifier = config.getString("identity-identifier");
	private String broadcastIdentifier = config.getString("broadcast-identifier");
	private String groupIdentifier = config.getString("group-identifier");
	private String privateChatIdentifier = config.getString("private-chat-identifier");
	private String privateMessageIdentifier = config.getString("private-message-identifier");
	private String chatFormIdentifier = config.getString("chat-form-identifier");
	private String errorIdentifier = config.getString("error-identifier");
	private String updateListIdentifier = config.getString("update-list-identifier");
	private String listClientsIdentifier = config.getString("list-clients-identifier");
	private String pingIdentifier = config.getString("ping-identifier");

	private MainChatWindow mainChatWindow;

	private Thread listen;

	public Client() {
	}

	public Client(String id, String name, String designation, InetAddress serverIP, int serverPort) {
		this.id = id;
		this.name = name;
		this.designation = designation;
		this.serverIP = serverIP;
		this.serverPort = serverPort;
	}

	public boolean registerClient(String password) {
		boolean result = false;
		clientNetworking = new ClientNetworking(serverIP, serverPort);
		if (clientNetworking.openConnection()) {
			startClient();
			/**
			 * sends a msg like:
			 * "/r/clientID/i/clientName/i/DEVELOPER/i/password"
			 */
			String connectionMessage = registerIdentifier + id + identityIdentifier + name + identityIdentifier
					+ designation + identityIdentifier + password;
			clientNetworking.send(connectionMessage.getBytes());
		}
		return result;
	}

	public boolean loginClient(String userName, String password) {
		boolean result = false;
		String bundleName = "ClientServer_" + userName;
		ResourceBundle clientServerBundle = ResourceBundle
				.getBundle("com.app.client.resources.serverInfo." + bundleName);
		try {
			this.serverIP = InetAddress.getByName(clientServerBundle.getString("serverIP"));
			this.serverPort = Integer.parseInt(clientServerBundle.getString("serverPort"));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		clientNetworking = new ClientNetworking(serverIP, serverPort);
		if (clientNetworking.openConnection()) {
			startClient();
			/**
			 * sends a msg like: "/l/username/i/password"
			 */
			String loginMessage = loginIdentifier + userName + identityIdentifier + password;
			clientNetworking.send(loginMessage.getBytes());
			result = true;
		}

		return result;
	}

	public void listen() {
		listen = new Thread("Listen") {
			public void run() {
				while (running) {
					DatagramPacket packet = clientNetworking.receive();
					process(packet);
				}
			}
		};
		listen.start();
	}

	private void process(DatagramPacket packet) {
		String message = new String(packet.getData()).trim();
		// System.out.println("RECEIVED: " + message);

		if (message.startsWith(pingIdentifier)) {
			/**
			 * receives a msg like: "/z/"
			 */
			pingBack();

		} else if (message.startsWith(broadcastIdentifier)) {
			mainChatWindow.process(message);

		} else if (message.startsWith(registerIdentifier)) {
			/**
			 * receives a message like: "/r/clientUserName"
			 */
			message = message.substring(registerIdentifier.length(), message.length());
			processRegisterAck(message, packet.getAddress(), packet.getPort());

		} else if (message.startsWith(loginIdentifier)) {
			/**
			 * receives a message like: "/l/clientUserName/i/id"
			 */
			message = message.substring(loginIdentifier.length(), message.length());
			processLoginAck(message);

		} else if (message.startsWith(updateListIdentifier)) {
			/**
			 * receives an acknowledgement like: "/u/message"
			 */
			message = message.substring(updateListIdentifier.length(), message.length());
			processUpdateListMessage(message);

		} else if (message.startsWith(logoutIdentifier)) {
			/**
			 * receives a msg like: "/x/clientUserName"
			 */
			processLogoutMessage();

		} else if (message.startsWith(privateChatIdentifier)) {
			/**
			 * receives a msg like: "/p/messsage"
			 */
			message = message.substring(updateListIdentifier.length(), message.length());
			processPrivateChatMessage(message);

		} else if (message.startsWith(errorIdentifier)) {

		} else if (message.startsWith(groupIdentifier)) {
			/**
			 * receives a msg like: "/g/message"
			 */
			message = message.substring(groupIdentifier.length(), message.length());
			processGroupMessage(message);

		}
	}

	private void pingBack() {
		/**
		 * sends a msg like: "/z/clientID"
		 */
		String message = pingIdentifier + this.id;
		send(message);
	}

	private void processGroupMessage(String message) {
		if (message.startsWith(chatFormIdentifier)) {
			/**
			 * receives a msg to all members like:
			 * "/f/groupID/i/groupName/i/createrUserName/i/membersUserNames"
			 */
			String[] arr = message.split(chatFormIdentifier + "|" + identityIdentifier);
			int groupID = Integer.parseInt(arr[1]);
			String groupName = arr[2];
			String creatorUserName = arr[3];
			String[] members = arr[4].split(",");
			mainChatWindow.addGroup(groupID, groupName, creatorUserName, members);

		} else if (message.startsWith(privateMessageIdentifier)) {
			/**
			 * receives a msg like: "/m/groupID/i/senderUserName/i/message"
			 */
			String[] arr = message.split(privateMessageIdentifier + "|" + identityIdentifier);
			mainChatWindow.processGroupMessage(Integer.parseInt(arr[1]), arr[2], arr[3]);
		}
	}

	private void processPrivateChatMessage(String message) {
		if (message.startsWith(privateMessageIdentifier)) {
			/**
			 * receives a msg like: "/m/privateChatID/i/message"
			 */
			String[] arr = message.split(privateMessageIdentifier + "|" + identityIdentifier);
			String privateChatID = arr[1];
			message = arr[2];
			mainChatWindow.processPrivateChatMessage(privateChatID, message);

		} else if (message.startsWith(chatFormIdentifier)) {
			/**
			 * receives a msg like: "/f/id/i/senderID/i/senderUsername"
			 */
			String[] arr = message.split(chatFormIdentifier + "|" + identityIdentifier);
			mainChatWindow.addPrivateChat(arr[1], arr[2], arr[3]);

		} else if (message.startsWith(ackIdentifier)) {
			/**
			 * receives a ack like: "/a/key/i/receiverID/i/receiverUserName"
			 */
			String[] arr = message.split(ackIdentifier + "|" + identityIdentifier);
			mainChatWindow.addPrivateChat(arr[1], arr[2], arr[3]);

		} else if (message.startsWith("")) {

		}
	}

	private void processLogoutMessage() {
		mainChatWindow.process("You have been logged out.");
		this.id = null;
		mainChatWindow.logoutAllFromLists();
	}

	private void processUpdateListMessage(String message) {
		if (message.startsWith(registerIdentifier)) {
			/**
			 * receives a msg like: "/r/clientUserName/i/groupID"
			 */
			String[] arr = message.split(registerIdentifier + "|" + identityIdentifier);
			String clientUserName = arr[1];
			int groupID = Integer.parseInt(arr[2]);
			mainChatWindow.updateList(clientUserName, true, groupID);

		} else if (message.startsWith(loginIdentifier)) {
			/**
			 * receives a msg like: "/l/username"
			 */
			String clientUserName = message.substring(loginIdentifier.length(), message.length());
			mainChatWindow.updateList(clientUserName, true);

		} else if (message.startsWith(logoutIdentifier)) {
			/**
			 * receives a msg like: "/x/clientUserName"
			 */
			String clientUserName = message.substring(loginIdentifier.length(), message.length());
			mainChatWindow.updateList(clientUserName, false);

		} else if (message.startsWith(listClientsIdentifier)) {
			/**
			 * receives a msg like:
			 * "/c/loggedInClientsUserNames/i/RestClientsUsernames"
			 */

			String[] arr = message.split(listClientsIdentifier + "|" + identityIdentifier);
			String[] loggedInClients = arr[1].split(",");
			String[] restClients = arr[2].split(",");

			for (int i = 0; i < loggedInClients.length; i++) {
				mainChatWindow.updateList(loggedInClients[i], true);
			}
			for (int i = 0; i < restClients.length; i++) {
				mainChatWindow.updateList(restClients[i], false);
			}
		}
	}

	private void processLoginAck(String message) {
		String[] arr = message.split(identityIdentifier);
		setUserName(arr[0]);
		setId(arr[1]);
		mainChatWindow = new MainChatWindow(this);
		// mainChatWindow.process("Successfully logged in");
	}

	private void processRegisterAck(String message, InetAddress serverIP, int serverPort) {
		/**
		 * receives a message like: "clientUserName"
		 */
		setUserName(message);

		Properties properties = new Properties();
		properties.setProperty("username", userName);
		properties.setProperty("serverIP", serverIP.getHostAddress());
		properties.setProperty("serverPort", Integer.toString(serverPort));

		String fileName = "ClientServer_" + userName;
		try {
			OutputStream outputStream = new FileOutputStream(
					"src/com/app/client/resources/serverInfo/" + fileName + ".properties");
			String comment = "This file contains the username, IP and port of the client so that client can login to the server next time without having to enter the server IP and server Port again.";
			properties.store(outputStream, comment);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mainChatWindow = new MainChatWindow(this);
		String msg = "Successfully REGISTERED with to server" + serverIP + ":" + serverPort + " with user name: "
				+ userName;
		mainChatWindow.process(msg);
	}

	private void startClient() {
		running = true;
		listen();
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void send(String message) {
		clientNetworking.send(message.getBytes());
	}

	public void startChat(String receiverUserName) {
		/**
		 * sends a msg like: "/p//f/senderClientID/i/receiverClientUserName"
		 */
		String msg = privateChatIdentifier + chatFormIdentifier + this.id + identityIdentifier + receiverUserName;
		clientNetworking.send(msg.getBytes());
	}

	public void sendMessage(String message, Chat chat) {
		if (chat instanceof PrivateChat) {
			privateChat = (PrivateChat) chat;
			/**
			 * sends a msg like: "/p//m/id/i/senderID/i/receiverID/i/message"
			 */
			message = privateChatIdentifier + privateMessageIdentifier + privateChat.getId() + identityIdentifier
					+ this.id + identityIdentifier + privateChat.getReceiverID() + identityIdentifier + message;

		} else if (chat instanceof Group) {
			group = (Group) chat;
			/**
			 * sends a msg like: "/g//m/groupID/i/senderID/i/message"
			 */
			message = groupIdentifier + privateMessageIdentifier + group.getId() + identityIdentifier + this.id
					+ identityIdentifier + message;

			// System.out.println("MESSAGE GROUP: " + message);
		} else if (chat instanceof Broadcast) {
			/**
			 * sends a msg like: "/b/clientID/i/message"
			 */
			message = broadcastIdentifier + this.id + identityIdentifier + message;
			// System.out.println("Broadcasting message: " + message);
		}

		clientNetworking.send(message.getBytes());

	}

}