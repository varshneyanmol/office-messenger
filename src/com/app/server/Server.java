package com.app.server;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import com.app.server.hibernate.dao.ServerDao;
import com.app.server.hibernate.model.GroupMessage;
import com.app.server.hibernate.model.Message;
import com.app.server.hibernate.model.Pending;
import com.app.server.hibernate.model.PendingGroupForm;
import com.app.server.hibernate.model.PendingGroupMessage;
import com.app.server.hibernate.model.PendingPrivateChatForm;
import com.app.server.hibernate.model.PendingPrivateMessage;
import com.app.server.hibernate.model.PrivateMessage;

public class Server implements Runnable {
	private boolean running = false;
	private InetAddress ip;
	private int port;

	private PingClients pingClients;

	private final int BROADCAST_GROUP_ID = 0;
	private ServerNetworking serverNetworking;
	private Thread runServer, listen, manage;

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
	private String pingIdentifier = config.getString("ping-identifier");

	private ArrayList<LoggedInClient> loggedInClients = new ArrayList<LoggedInClient>();
	private ArrayList<RegisteredClient> registeredClients;
	private ArrayList<Group> groups = new ArrayList<Group>();

	public Server(InetAddress ip, int port) {
		this.ip = ip;
		this.port = port;
		serverNetworking = new ServerNetworking(port);
		pingClients = PingClients.getPingClients(this);
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
		manage = new Thread(new Runnable() {
			@Override
			public void run() {
				while (running) {
					pingClients.ping();
					try {
						Thread.sleep(4000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					pingClients.check();
				}
			}
		});
		manage.start();
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

		if (message.startsWith(pingIdentifier)) {
			/**
			 * receives a msg like: "/z/clientID"
			 */
			message = message.substring(pingIdentifier.length(), message.length());
			pingClients.add(message);

		} else if (message.startsWith(registerIdentifier)) {
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
			processLogoutMessage(message, false);

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
		System.out.println("message received: " + message);
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

			Group group = getGroup(groupID);
			if (group == null) {
				System.out.println("NO GROUP FETCHED");
				return;
			}
			Set<RegisteredClient> members = group.getMembers();
			if (members.isEmpty()) {
				System.out.println("NO MEMEBRS IN GROUP");
				return;
			}

			Message parent = ServerDao.fetchParent(group);
			if (parent == null) {
				System.out.println("NO PARENT");
			} else {
				System.out.println("PARENT TOSTRING: " + parent.toString());
			}

			GroupMessage messageDB = new GroupMessage(msg, new Date(), sender, group);
			messageDB.setParent(parent);
			boolean isSaved = ServerDao.saveGroupMessage(messageDB);
			System.out.println("MESSGAE TOSTRING: " + messageDB.toString());
			if (isSaved) {
				System.out.println("SAVED MESSAGE: " + msg);
			} else {
				System.out.println("NOT SAVED MESSAGE: " + msg);
			}
			/**
			 * sends a msg to logged in clients like:
			 * "/g//m/groupID/i/senderUserName/i/message"
			 */
			msg = groupIdentifier + privatemessageIdentifier + groupID + identityIdentifier + sender.getUserName()
					+ identityIdentifier + msg;
			Iterator<RegisteredClient> iterator = members.iterator();
			System.out.println("Total clients: " + members.size());
			while (iterator.hasNext()) {
				RegisteredClient member = iterator.next();
				LoggedInClient loggedInClient = getLoggedInClient(member.getId());
				if (loggedInClient != null) {
					System.out.println("INSIDE");
					serverNetworking.send(msg.getBytes(), loggedInClient.getIp(), loggedInClient.getPort());
				} else {
					// client is not logged in
					PendingGroupMessage pendingGroupMessage = ServerDao.fetchPendingGroupMessage(member, group, true);
					if (pendingGroupMessage == null) {
						System.out.println("pendingGroupMessage FETCHED NULL");
						// pending group does not exist
						pendingGroupMessage = new PendingGroupMessage(member, group, messageDB);
						pendingGroupMessage.setTotalPending(1);
						ServerDao.savePendingGroupMessage(pendingGroupMessage);
					} else {
						pendingGroupMessage.toString();
						ServerDao.updatePendingGroupMessage(pendingGroupMessage, messageDB,
								pendingGroupMessage.getTotalPending() + 1);
					}
				}
			}

		} else if (message.startsWith(chatFormIdentifier)) {
			/**
			 * receives a msg like:
			 * "/f/groupName/i/clientID/i/member1UserName,member2UserName,..,"
			 */
			String[] arr = message.split(chatFormIdentifier + "|" + identityIdentifier);

			int groupID = UniqueIdentifier.getUniqueGroupIdentifier();
			String groupName = arr[1];
			String groupCreaterID = arr[2];
			String[] groupMembers = arr[3].split(",");

			Set<RegisteredClient> members = new HashSet<RegisteredClient>();
			for (int i = 0; i < groupMembers.length; i++) {
				RegisteredClient member = getClientByUserName(groupMembers[i]);
				if (member != null) {
					members.add(member);
				}
			}
			Group group = ServerDao.fetchGroup(groupID, true);
			LoggedInClient groupCreater = getLoggedInClient(groupCreaterID);
			if (group == null) {
				group = new Group(groupID, groupName, new Date());
				group.setMembers(members);
				group.setCreator(groupCreater.getClient());
				ServerDao.saveGroup(group);
			}
			/**
			 * sends a msg to all members like:
			 * "/g//f/groupID/i/groupName/i/createrUserName/i/membersUserNames"
			 */

			String messageToMembers = groupIdentifier + chatFormIdentifier + groupID + identityIdentifier + groupName
					+ identityIdentifier + groupCreater.getClient().getUserName() + identityIdentifier + arr[3];

			Iterator<RegisteredClient> iterator = members.iterator();
			while (iterator.hasNext()) {
				RegisteredClient member = iterator.next();
				LoggedInClient loggedInClient = getLoggedInClient(member.getId());
				if (loggedInClient != null) {
					serverNetworking.send(messageToMembers.getBytes(), loggedInClient.getIp(),
							loggedInClient.getPort());
				} else {
					PendingGroupForm pendingGroupForm = new PendingGroupForm(member, group);
					ServerDao.savePendingGroupForm(pendingGroupForm);
				}
			}
		}
	}

	private Group getGroup(int groupID) {
		Group group = null;
		boolean found = false;
		for (int i = 0; i < groups.size(); i++) {
			if (groups.get(i).getId() == groupID) {
				group = groups.get(i);
				found = true;
				break;
			}
		}
		if (!found) {
			group = ServerDao.fetchGroup(groupID, true);
			groups.add(group);
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
			 * receives a msg like:
			 * "/m/privateChatID/i/senderID/i/receiverID/i/message"
			 */
			String[] arr = message.split(privatemessageIdentifier + "|" + identityIdentifier);
			String privateChatID = arr[1];
			String senderID = arr[2];
			String receiverID = arr[3];
			message = arr[4];
			// LoggedInClient receiver = getLoggedInClient(receiverID);
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

			LoggedInClient receiverLoggedIn = getLoggedInClient(receiver.getId());
			if (receiverLoggedIn != null) {
				/**
				 * sends a msg like: "/p//m/privateChatID/i/message"
				 */
				message = privateChatIdentifier + privatemessageIdentifier + privateChatID + identityIdentifier
						+ message;
				serverNetworking.send(message.getBytes(), receiverLoggedIn.getIp(), receiverLoggedIn.getPort());

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
			serverNetworking.send(messageToSender.getBytes(), sender.getIp(), sender.getPort());

			LoggedInClient receiverLoggedIn = getLoggedInClient(receiver.getId());
			if (receiverLoggedIn != null) {
				/**
				 * sends a msg to the receiver like:
				 * "/p//f/privateChatID/i/senderClientID/i/senderClientUserName"
				 */
				String messageToReceiver = privateChatIdentifier + chatFormIdentifier + privateChat.getId()
						+ identityIdentifier + senderClientID + identityIdentifier + sender.getClient().getUserName();
				serverNetworking.send(messageToReceiver.getBytes(), receiverLoggedIn.getIp(),
						receiverLoggedIn.getPort());

			} else {
				// client is not logged in
				PendingPrivateChatForm pendingPrivateChatForm = new PendingPrivateChatForm(receiver, privateChat);
				ServerDao.savePendingPrivateChatForm(pendingPrivateChatForm);

			}
		}
	}

	private RegisteredClient getClientByUserName(String receiverClientUserName) {
		RegisteredClient client = null;
		client = ServerDao.fetchClientByUserName(receiverClientUserName);
		return client;
	}

	private void processLogoutMessage(String clientID, boolean sendAck) {
		LoggedInClient client = getLoggedInClient(clientID);
		if (client == null) {
			return;
		}
		loggedInClients.remove(client);

		if (sendAck) {
			/**
			 * sends an ack to the logged out client like: "/x/clientUserName"
			 */
			String ackToLoggedOutClient = logoutIdentifier + client.getClient().getUserName();
			serverNetworking.send(ackToLoggedOutClient.getBytes(), client.getIp(), client.getPort());
		}
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

	public LoggedInClient getLoggedInClient(String clientID) {
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

		// Message messageDB = new Message(message, new Date(), sender, );

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

			ArrayList<Pending> pendings = ServerDao.fetchAllPending(client);
			for (int i = 0; i < pendings.size(); i++) {

				Pending pending = pendings.get(i);
				if (pending instanceof PendingPrivateChatForm) {
					PendingPrivateChatForm pendingPrivateChatForm = (PendingPrivateChatForm) pending;
				} else if (pending instanceof PendingGroupForm) {
					PendingGroupForm pendingGroupForm = (PendingGroupForm) pending;
					// if (pendingGroupForm.getGroup() == null) {
					// System.out.println("Group fetched NULL");
					// }
				} else if (pending instanceof PendingPrivateMessage) {
					PendingPrivateMessage pendingPrivateMessage = (PendingPrivateMessage) pending;

				} else if (pending instanceof PendingGroupMessage) {
					PendingGroupMessage pendingGroupMessage = (PendingGroupMessage) pending;

				}

				// System.out.println(pendings.get(i).toString());
				// System.out.println("---------------------------");
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

	public void send(String message, InetAddress clientIP, int clientPort) {
		serverNetworking.send(message.getBytes(), clientIP, clientPort);
	}

	public ArrayList<LoggedInClient> getLoggedInClients() {
		return loggedInClients;
	}

	public void logout(LoggedInClient client, boolean sendAck) {
		processLogoutMessage(client.getClient().getId(), sendAck);
	}
}
