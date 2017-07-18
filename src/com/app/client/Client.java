package com.app.client;

import java.net.InetAddress;
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

	ResourceBundle config = ResourceBundle.getBundle("com.app.config");
	Thread listen;

	public Client() {
	}

	public Client(int id, String name, String designation, InetAddress serverIP, int serverPort) {
		this.id = id;
		this.name = name;
		this.designation = designation;
		this.serverIP = serverIP;
		this.serverPort = serverPort;
		clientNetworking = new ClientNetworking(serverIP, serverPort);
	}

	public boolean registerClient(String password) {
		boolean result = false;
		if (clientNetworking.openConnection()) {
			String registerIdentifier = config.getString("register-identifier");
			String identityIdentifier = config.getString("identity-identifier");

			/**
			 * sends a msg like:
			 * "/r/clientID/i/clientName/i/DEVELOPER/i/password"
			 */
			String connectionMessage = registerIdentifier + id + identityIdentifier + name + identityIdentifier
					+ designation + identityIdentifier + password;
			clientNetworking.send(connectionMessage.getBytes());
			running = true;
			listen();
		}
		return result;
	}

	public void listen() {
		listen = new Thread("Listen") {
			public void run() {
				while (running) {
					String message = clientNetworking.receive();
					process(message);
				}
			}
		};
		listen.start();
	}

	private void process(String message) {
		System.out.println(message);
	}

	public boolean loginClient(String userName, String password) {
		boolean result = false;
		if (clientNetworking.openConnection()) {
			String loginIdentifier = config.getString("login-identifier");
			String identityIdentifier = config.getString("identity-identifier");

			/**
			 * sends a msg like: "/l/anmol_111/i/anaconda"
			 */
			String loginMessage = loginIdentifier + userName + identityIdentifier + password;
			clientNetworking.send(loginMessage.getBytes());
		}

		return result;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
