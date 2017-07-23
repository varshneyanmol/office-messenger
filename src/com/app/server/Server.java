package com.app.server;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import com.app.server.hibernate.dao.ServerDao;

public class Server implements Runnable {
	private boolean running = false;
	private InetAddress ip;
	private int port;

	private final int BROADCAST_GROUP_ID = 0;
	private ServerNetworking serverNetworking;
	private Thread runServer, listen;

	private ResourceBundle config = ResourceBundle.getBundle("com.app.config");
	private String registerIdentifier = config.getString("register-identifier");
	private String loginIdentifier = config.getString("login-identifier");
	private String logoutIdentifier = config.getString("logout-identifier");
	private String identityIdentifier = config.getString("identity-identifier");
	private String ackIdentifier = config.getString("ack-identifier");
	private String broadcastIdentifier = config.getString("broadcast-identifier");
	private String groupIdentifier = config.getString("group-identifier");
	private String privateChatIdentifier = config.getString("private-chat-identifier");
	private String privatemessageIdentifier = config.getString("private-message-identifier");
	private String chatFormIdentifier = config.getString("chat-form-identifier");
	private String errorIdentifier = config.getString("error-identifier");
	private String updateListIdentifier = config.getString("update-list-identifier");
	private String listClientsIdentifier = config.getString("list-clients-identifier");

	private ArrayList<LoggedInClient> loggedInClients = new ArrayList<LoggedInClient>();
	private ArrayList<RegisteredClient> registeredClients;
	private ArrayList<Group> groups = new ArrayList<Group>();

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

		} else if (message.startsWith(broadcastIdentifier)) {
			/**
			 * receives a message like: "/b/clientID/i/message"
			 */
			prepareBroadcastMessage(message);

		} else if (message.startsWith(logoutIdentifier)) {
			/**
			 * receives a msg like: "/x/clientID"
			 */
			message = message.substring(logoutIdentifier.length(), message.length());
			processLogoutMessage(message);

		} else if (message.startsWith(privateChatIdentifier)) {
			/**
			 * receives a msg like: "/p/message"
			 */
			message = message.substring(privateChatIdentifier.length(), message.length());
			processPrivateChatMessage(message);

		} else if (message.startsWith(groupIdentifier)) {
			/**
			 * receives a msg like: "/g/message"
			 */
			message = message.substring(groupIdentifier.length(), message.length());
			processGroupMessage(message);

		}

	}

	private void processGroupMessage(String message) {
		if (message.startsWith(privatemessageIdentifier)) {
			/**
			 * receives a msg like: "/m/groupID/i/senderID/i/message"
			 */
			String[] arr = message.split(privatemessageIdentifier + "|" + identityIdentifier);
			int groupID = Integer.parseInt(arr[1]);
			RegisteredClient sender = ServerDao.fetchClient(arr[2]);
			if (sender == null) {
				return;
			}
			String msg = arr[3];
			System.out.println("ARR[3]: " + msg);
			Group group = getGroup(groupID);
			if (group == null) {
				return;
			}
			ArrayList<String> members = group.getMemberIDs();
			for (int i = 0; i < members.size(); i++) {
				RegisteredClient client = getClientByUserName(members.get(i));
				if (client == null) {
					return;
				}
				LoggedInClient loggedInClient = getLoggedInClient(client.getId());
				if (loggedInClient != null) {
					/**
					 * sends a msg to logged in clients like:
					 * "/g//m/groupID/i/senderUserName/i/message"
					 */
					msg = groupIdentifier + privatemessageIdentifier + groupID + identityIdentifier
							+ sender.getUserName() + identityIdentifier + msg;
					System.out.println("SENT: " + msg);
					serverNetworking.send(msg.getBytes(), loggedInClient.getIp(), loggedInClient.getPort());
					msg = "";

				} else {
					// client is not logged in -> pending
				}
			}

		} else if (message.startsWith(chatFormIdentifier)) {
			/**
			 * receives a msg like:
			 * "/f/groupName/i/clientID/i/member1UserName,member2UserName,..,"
			 */
			String[] arr = message.split(chatFormIdentifier + "|" + identityIdentifier);
			String groupName = arr[1];
			String groupCreaterID = arr[2];
			String[] groupMembers = arr[3].split(",");
			int groupID = UniqueIdentifier.getUniqueGroupIdentifier();
			ArrayList<String> groupMembersList = new ArrayList<String>(Arrays.asList(groupMembers));
			groups.add(new Group(groupID, groupName, groupMembersList));
			LoggedInClient groupCreater = getLoggedInClient(groupCreaterID);

			/**
			 * sends a msg to all members like:
			 * "/g//f/groupID/i/groupName/i/createrUserName/i/membersUserNames"
			 */
			String messageToMembers = groupIdentifier + chatFormIdentifier + groupID + identityIdentifier + groupName
					+ identityIdentifier + groupCreater.getClient().getUserName() + identityIdentifier + arr[3];
			for (int i = 0; i < groupMembersList.size(); i++) {
				RegisteredClient client = getClientByUserName(groupMembersList.get(i));
				if (client == null) {
					continue;
				}
				LoggedInClient loggedInClient = getLoggedInClient(client.getId());
				if (loggedInClient != null) {
					serverNetworking.send(messageToMembers.getBytes(), loggedInClient.getIp(),
							loggedInClient.getPort());
				} else {
					// client is not logged in -> pending
				}
			}
		}
	}

	private Group getGroup(int groupID) {
		Group group = null;

		for (int i = 0; i < groups.size(); i++) {
			if (groups.get(i).getId() == groupID) {
				group = groups.get(i);
				break;
			}
		}

		return group;
	}

	private boolean isClientLoggedIn(String clientID) {
		for (int i = 0; i < loggedInClients.size(); i++) {
			LoggedInClient c = loggedInClients.get(i);
			if (c.getClient().getId().equals(clientID)) {
				return true;
			}
		}
		return false;
	}

	private void processPrivateChatMessage(String message) {
		if (message.startsWith(privatemessageIdentifier)) {
			/**
			 * receives a msg like: "/m/senderID/i/receiverID/i/message"
			 */
			String[] arr = message.split(privatemessageIdentifier + "|" + identityIdentifier);
			String senderID = arr[1];
			String receiverID = arr[2];
			message = arr[3];
			LoggedInClient receiver = getLoggedInClient(receiverID);
			if (receiver != null) {
				/**
				 * sends a msg like: "/p//m/senderID/i/message"
				 */
				message = privateChatIdentifier + privatemessageIdentifier + senderID + identityIdentifier + message;
				serverNetworking.send(message.getBytes(), receiver.getIp(), receiver.getPort());

			} else {
				// receiver is not logged in -> pending
			}

		} else if (message.startsWith(chatFormIdentifier)) {
			/**
			 * receives a msg like: "/f/senderClientID/i/receiverClientUserName"
			 */
			String[] arr = message.split(chatFormIdentifier + "|" + identityIdentifier);

			String senderClientID = arr[1];
			LoggedInClient sender = getLoggedInClient(senderClientID);
			if (sender == null) {
				return;
			}
			String receiverClientUserName = arr[2];
			RegisteredClient receiver = getClientByUserName(receiverClientUserName);
			String receiverID = receiver.getId();

			for (int i = 0; i < loggedInClients.size(); i++) {
				LoggedInClient c = loggedInClients.get(i);
				if (c.getClient().getId().equals(receiverID)) {
					// client is logged in
					/**
					 * sends a msg to the receiver like:
					 * "/p//f/senderClientID/i/senderClientUserName" AND sends a
					 * ack to the sender like:
					 * "/p//a/receiverID/i/receiverUserName"
					 */
					String messageToReceiver = privateChatIdentifier + chatFormIdentifier + senderClientID
							+ identityIdentifier + sender.getClient().getUserName();
					String messageToSender = privateChatIdentifier + ackIdentifier + receiverID + identityIdentifier
							+ receiverClientUserName;
					serverNetworking.send(messageToReceiver.getBytes(), c.getIp(), c.getPort());
					serverNetworking.send(messageToSender.getBytes(), sender.getIp(), sender.getPort());
					break;
				} else {
					// client is not logged in -> pending
				}
			}

		}
	}

	private RegisteredClient getClientByUserName(String receiverClientUserName) {
		RegisteredClient client = null;
		client = ServerDao.fetchClientByUserName(receiverClientUserName);
		return client;
	}

	private void processLogoutMessage(String clientID) {
		LoggedInClient client = getLoggedInClient(clientID);
		if (client == null) {
			return;
		}
		loggedInClients.remove(client);

		/**
		 * sends an ack to the logged out client like: "/x/clientUserName"
		 */
		String ackToLoggedOutClient = logoutIdentifier + client.getClient().getUserName();
		serverNetworking.send(ackToLoggedOutClient.getBytes(), client.getIp(), client.getPort());

		/**
		 * sends an update list msg to all the logged in clients like :
		 * "/u//x/clientUserName"
		 */
		String msgToLoggedInClients = updateListIdentifier + logoutIdentifier + client.getClient().getUserName();
		for (int i = 0; i < loggedInClients.size(); i++) {
			LoggedInClient c = loggedInClients.get(i);
			serverNetworking.send(msgToLoggedInClients.getBytes(), c.getIp(), c.getPort());

		}
	}

	private LoggedInClient getLoggedInClient(String clientID) {
		for (int i = 0; i < loggedInClients.size(); i++) {
			LoggedInClient c = loggedInClients.get(i);
			if (c.getClient().getId().equals(clientID)) {
				return c;
			}
		}
		return null;
	}

	private void prepareBroadcastMessage(String message) {
		/**
		 * prepares a msg like: "/b/clientUserName/i/message"
		 */
		message = message.substring(broadcastIdentifier.length(), message.length());
		String[] arr = message.split(identityIdentifier);
		String clientID = arr[0];
		message = arr[1];

		for (int i = 0; i < loggedInClients.size(); i++) {
			LoggedInClient c = loggedInClients.get(i);
			if (c.getClient().getId().equals(clientID)) {
				message = c.getClient().getUserName() + identityIdentifier + message;
				break;
			}
		}
		broadcast(message, true);
	}

	private void broadcast(String message, boolean attachBroadcastIdentifier) {
		if (attachBroadcastIdentifier) {
			message = broadcastIdentifier + message;
		}
		LoggedInClient client;
		for (int i = 0; i < loggedInClients.size(); i++) {
			client = loggedInClients.get(i);
			serverNetworking.send(message.getBytes(), client.getIp(), client.getPort());
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

			/**
			 * sends an acknowledgement like: "/l/username/i/clientID"
			 */
			String ackToLoggedInClient = loginIdentifier + client.getUserName() + identityIdentifier + client.getId();
			serverNetworking.send(ackToLoggedInClient.getBytes(), clientIP, clientPort);

			/**
			 * sends an msg like:
			 * "/u//c/loggedInClientsUserNames/i/RestClientsUsernames"
			 */
			String messageToClient = updateListIdentifier + listClientsIdentifier + getAllClientsString();
			serverNetworking.send(messageToClient.getBytes(), clientIP, clientPort);

			/**
			 * sends an ack like: "/u//l/username" to already logged in clients
			 * but not to the new;y logged in client.
			 */
			String ackToAllLoggedInClients = updateListIdentifier + loginIdentifier + client.getUserName();
			for (int i = 0; i < loggedInClients.size(); i++) {
				LoggedInClient c = loggedInClients.get(i);
				if (c.getClient() == client) {
					continue;
				}
				serverNetworking.send(ackToAllLoggedInClients.getBytes(), c.getIp(), c.getPort());
			}

		} else {
			/**
			 * sends an error message like: "/e/Username or Password Incorrect"
			 */
			String ackToRegisteredClient = errorIdentifier + "Username or Password Incorrect";
			serverNetworking.send(ackToRegisteredClient.getBytes(), clientIP, clientPort);
		}
	}

	private String getAllClientsString() {
		/**
		 * return a string like:
		 * "loggedInClientsUserNames/i/RestClientsUsernames"
		 */
		String loggedInClientsStr = "";
		String restClientsStr = "";
		ArrayList<RegisteredClient> allClients = ServerDao.fetchAllClients();
		for (int i = 0; i < allClients.size(); i++) {
			RegisteredClient cr = allClients.get(i);
			boolean flag = false;

			for (int j = 0; j < loggedInClients.size(); j++) {
				LoggedInClient cl = loggedInClients.get(j);
				if (cl.getClient().getId().equals(cr.getId())) {
					flag = true;
					loggedInClientsStr = loggedInClientsStr + cr.getUserName() + ",";
					break;
				}
			}
			if (!flag) {
				restClientsStr = restClientsStr + cr.getUserName() + ",";
			}
		}
		return loggedInClientsStr + identityIdentifier + restClientsStr;
	}

	private void registerClient(String message, InetAddress clientIP, int clientPort) {
		/**
		 * recieves a message = "clientID/i/clientName/i/DEVELOPER/i/password"
		 */

		String[] arr = message.split(identityIdentifier);
		String clientID = arr[0];
		String clientName = arr[1];
		String clientDesignation = arr[2];
		String clientPassword = arr[3];

		RegisteredClient client = ServerDao.fetchClient(clientID);

		if (client != null) {
			/**
			 * sends an acknowledgement message like: "/e/ID already registered"
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

		// registeredClients
		// =
		// ServerDao.fetchAllClients();
		// for (int i = 0; i < registeredClients.size(); i++) {
		// ackToRegisteredClient = ackToRegisteredClient +
		// registeredClients.get(i).getUserName() + ",";
		// }

		/**
		 * sends an acknowledgement to all logged in clients like:
		 * "/u//r/clientUserName/i/groupID"
		 */
		String ackToLoggedInClients = updateListIdentifier + registerIdentifier + clientUserName + identityIdentifier
				+ BROADCAST_GROUP_ID;

		for (int i = 0; i < loggedInClients.size(); i++) {
			LoggedInClient c = loggedInClients.get(i);
			serverNetworking.send(ackToLoggedInClients.getBytes(), c.getIp(), c.getPort());
		}

		loggedInClients.add(new LoggedInClient(client, clientIP, clientPort));
		/**
		 * sends an acknowledgement message like: "/r/clientUserName" back to
		 * newly registered client
		 */
		String ackToRegisteredClient = registerIdentifier + clientUserName;
		/**
		 * sends an msg like:
		 * "/u//c/loggedInClientsUserNames/i/RestClientsUsernames" back to newly
		 * registered client
		 */
		String messageToClient = updateListIdentifier + listClientsIdentifier + getAllClientsString();
		System.out.println(ackToRegisteredClient);
		serverNetworking.send(ackToRegisteredClient.getBytes(), clientIP, clientPort);
		serverNetworking.send(messageToClient.getBytes(), clientIP, clientPort);
	}
}
