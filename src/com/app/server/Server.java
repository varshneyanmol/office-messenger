package com.app.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.app.server.hibernate.dao.ServerDao;

public class Server implements Runnable {
	private boolean running = false;
	private InetAddress ip;
	private int port;

	private ServerNetworking serverNetworking;
	private Thread runServer, listen;

	private ResourceBundle config = ResourceBundle.getBundle("com.app.config");
	private String registerIdentifier = config.getString("register-identifier");
	private String loginIdentifier = config.getString("login-identifier");
	private String identityIdentifier = config.getString("identity-identifier");
	private String errorIdentifier = config.getString("error-identifier");

	private ArrayList<LoggedInClient> loggedInClients = new ArrayList<LoggedInClient>();

	public Server(InetAddress ip, int port) {
		this.ip = ip;
		this.port = port;
		serverNetworking = new ServerNetworking(port);
		startServer();
	}

	private void startServer() {
		if (serverNetworking.openSocket()) {
			runServer = new Thread(this, "RunServer");
			runServer.start();
		}
	}

	@Override
	public void run() {
		System.out.println("SERVER RUNNING ON: " + ip + ":" + port);
		running = true;
		manageClients();
		listen();
		adminCommands();
	}

	private void adminCommands() {
		// admin commands
	}

	private void manageClients() {
		// manage clients
	}

	public void listen() {
		listen = new Thread("Listen") {
			public void run() {
				while (running) {
					DatagramPacket packet = serverNetworking.receive();
					process(packet);
				}
			}
		};
		listen.start();
	}

	private void process(DatagramPacket packet) {
		String message = new String(packet.getData()).trim();

		if (message.startsWith(registerIdentifier)) {
			/**
			 * receives a message like:
			 * "/r/clientID/i/clientName/i/DEVELOPER/i/password"
			 */
			message = message.substring(registerIdentifier.length(), message.length());
			registerClient(message, packet.getAddress(), packet.getPort());

		} else if (message.startsWith(loginIdentifier)) {
			/**
			 * receives a message like: "/l/username/i/password"
			 */
			message = message.substring(loginIdentifier.length(), message.length());
			loginClient(message, packet.getAddress(), packet.getPort());

		}
	}

	private void loginClient(String message, InetAddress clientIP, int clientPort) {
		/**
		 * recieves a message = "username/i/password"
		 */
		String[] arr = message.split(identityIdentifier);
		RegisteredClient client = ServerDao.fetchClient(arr[0], arr[1]);
		if (client != null) {
			loggedInClients.add(new LoggedInClient(client, clientIP, clientPort));
			String ackToLoggedInClient = loginIdentifier + client.getUserName();
			serverNetworking.send(ackToLoggedInClient.getBytes(), clientIP, clientPort);

		} else {
			String ackToRegisteredClient = errorIdentifier + "UserName or Password Incorrect";
			serverNetworking.send(ackToRegisteredClient.getBytes(), clientIP, clientPort);
		}
	}

	private void registerClient(String message, InetAddress clientIP, int clientPort) {
		/**
		 * recieves a message = "clientID/i/clientName/i/DEVELOPER/i/password"
		 */

		String[] arr = message.split(identityIdentifier);
		int clientID = Integer.parseInt(arr[0]);
		String clientName = arr[1];
		String clientDesignation = arr[2];
		String clientPassword = arr[3];

		RegisteredClient client = ServerDao.fetchClient(clientID);

		if (client != null) {
			/**
			 * sends an acknowledgement message like: "/e/could not register"
			 * back to newly registered client
			 */
			String ackToRegisteredClient = errorIdentifier + "ID already registered";
			serverNetworking.send(ackToRegisteredClient.getBytes(), clientIP, clientPort);
			return;
		}
		/**
		 * this user name is generated by server and sent back to the client.
		 */
		String clientUserName = clientName + "_" + clientID;

		client = new RegisteredClient(clientID, clientName, clientDesignation, clientUserName, clientPassword);

		ServerDao.saveClient(client);

		/**
		 * sends an acknowledgement message like: "/r/clientUserName" back to
		 * newly registered client
		 */
		String ackToRegisteredClient = registerIdentifier + clientUserName;
		serverNetworking.send(ackToRegisteredClient.getBytes(), clientIP, clientPort);
		loggedInClients.add(new LoggedInClient(client, clientIP, clientPort));
	}
}
