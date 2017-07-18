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

public class Client {
	private boolean running = false;
	private int id;
	private String name;
	private String userName;
	private String designation;
	private InetAddress serverIP;
	private int serverPort;
	private ClientNetworking clientNetworking;

	private ResourceBundle config = ResourceBundle.getBundle("com.app.config");
	private String registerIdentifier = config.getString("register-identifier");
	private String loginIdentifier = config.getString("login-identifier");
	private String identityIdentifier = config.getString("identity-identifier");
	private String errorIdentifier = config.getString("error-identifier");

	private Thread listen;

	public Client() {
	}

	public Client(int id, String name, String designation, InetAddress serverIP, int serverPort) {
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
		ResourceBundle clientServerBundle = ResourceBundle.getBundle("com.app.client.ClientServer");
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

		if (message.startsWith(registerIdentifier)) {
			/**
			 * receives a message like: "/r/clientUserName"
			 */
			message = message.substring(registerIdentifier.length(), message.length());
			processRegisterAck(message, packet.getAddress(), packet.getPort());

		} else if (message.startsWith(loginIdentifier)) {
			/**
			 * receives a message like: "/l/clientUserName"
			 */
			message = message.substring(registerIdentifier.length(), message.length());
			processLoginAck(message);
		} else if (message.startsWith(errorIdentifier)) {

		}
	}

	private void processLoginAck(String message) {
		System.out.println("Logged in with user name: " + message);
	}

	private void processRegisterAck(String message, InetAddress serverIP, int serverPort) {
		setUserName(message);

		Properties properties = new Properties();
		properties.setProperty("username", userName);
		properties.setProperty("serverIP", serverIP.getHostAddress());
		properties.setProperty("serverPort", Integer.toString(serverPort));

		try {
			OutputStream outputStream = new FileOutputStream("src/com/app/client/ClientServer.properties");
			String comment = "This file contains the username, IP and port of the client so that client can login to the server next time without having to enter the server IP and server Port again.";
			properties.store(outputStream, comment);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void startClient() {
		running = true;
		listen();
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
