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

import com.app.client.chatWindowGUI.MainChatWindow;

public class Client {
	private boolean running = false;
	private String id;
	private String name;
	private String userName;
	private String designation;
	private InetAddress serverIP;
	private int serverPort;
	private ClientNetworking clientNetworking;

	private ResourceBundle config = ResourceBundle.getBundle("com.app.config");
	private String registerIdentifier = config.getString("register-identifier");
	private String loginIdentifier = config.getString("login-identifier");
	private String logoutIdentifier = config.getString("logout-identifier");
	private String identityIdentifier = config.getString("identity-identifier");
	private String broadcastIdentifier = config.getString("broadcast-identifier");
	private String groupIdentifier = config.getString("group-identifier");
	private String errorIdentifier = config.getString("error-identifier");
	private String updateListIdentifier = config.getString("update-list-identifier");

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
		ResourceBundle clientServerBundle = ResourceBundle.getBundle("com.app.client." + bundleName);
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
		System.out.println(message);

		if (message.startsWith(broadcastIdentifier)) {
			mainChatWindow.process(message);

		} else if (message.startsWith(registerIdentifier)) {
			/**
			 * receives a message like:
			 * "/r/clientUserName/i/client1UserName,client2UserName...,"
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

		} else if (message.startsWith(errorIdentifier)) {

		}
	}

	private void processLogoutMessage() {
		mainChatWindow.process("You have been logged out.");
		mainChatWindow.clearAllLists();
	}

	private void processUpdateListMessage(String message) {
		if (message.startsWith(registerIdentifier)) {
			/**
			 * receives a msg like: "/r/clientUserName/i/groupID"
			 */
			String[] arr = message.split(registerIdentifier + "|" + identityIdentifier);
			String clientUserName = arr[1];
			int groupID = Integer.parseInt(arr[2]);
			mainChatWindow.updateList(clientUserName + "**", groupID);

		} else if (message.startsWith(loginIdentifier)) {
			/**
			 * receives a msg like: "/l/username"
			 */
			String clientUserName = message.substring(loginIdentifier.length(), message.length());
			mainChatWindow.removeFromList(clientUserName);
			mainChatWindow.updateList(clientUserName + "**");

		} else if (message.startsWith(logoutIdentifier)) {
			/**
			 * receives a msg like: "/x/clientUserName"
			 */
			String clientUserName = message.substring(loginIdentifier.length(), message.length());
			mainChatWindow.removeFromList(clientUserName + "**");
			mainChatWindow.addToList(clientUserName);

		}
	}

	private void processLoginAck(String message) {
		String[] arr = message.split(identityIdentifier);
		setUserName(arr[0]);
		setId(arr[1]);
		mainChatWindow = new MainChatWindow(this);
		mainChatWindow.process("Successfully logged in");
	}

	private void processRegisterAck(String message, InetAddress serverIP, int serverPort) {
		/**
		 * receives a message like:
		 * "clientUserName/i/groupID/i/client1UserName,client2UserName...,"
		 */

		String[] arr = message.split(identityIdentifier);
		setUserName(arr[0]);
		int broadcastGroupID = Integer.parseInt(arr[1]);
		String[] registeredClients = arr[2].split(",");

		Properties properties = new Properties();
		properties.setProperty("username", userName);
		properties.setProperty("serverIP", serverIP.getHostAddress());
		properties.setProperty("serverPort", Integer.toString(serverPort));

		String fileName = "ClientServer_" + userName;
		try {
			OutputStream outputStream = new FileOutputStream("src/com/app/client/" + fileName + ".properties");
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
		mainChatWindow.updateList(registeredClients, broadcastGroupID);
		mainChatWindow.removeFromList(userName);
		mainChatWindow.updateList(userName + "**");
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
}
