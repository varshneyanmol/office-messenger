package com.app.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import com.app.server.hibernate.dao.ServerDao;
import com.app.server.hibernate.model.Group;
import com.app.server.hibernate.model.GroupMessage;
import com.app.server.hibernate.model.Message;
import com.app.server.hibernate.model.PendingGroupForm;
import com.app.server.hibernate.model.PendingGroupMessage;
import com.app.server.hibernate.model.RegisteredClient;

public class GroupManager {
	private Server server;
	private static GroupManager groupManager = null;

	private ArrayList<Group> groups = new ArrayList<Group>();

	private ResourceBundle config = ResourceBundle.getBundle("com.app.config");
	private String identityIdentifier = config.getString("identity-identifier");
	private String groupIdentifier = config.getString("group-identifier");
	private String messageIdentifier = config.getString("message-identifier");
	private String chatFormIdentifier = config.getString("chat-form-identifier");

	private GroupManager(Server server) {
		this.server = server;
	}

	public static GroupManager GetGroupManager(Server server) {
		if (groupManager == null) {
			groupManager = new GroupManager(server);
		}
		return groupManager;
	}

	public void process(String message) {
		if (message.startsWith(messageIdentifier)) {
			/**
			 * receives a msg like: "/m/groupID/i/senderID/i/message"
			 */

			String[] arr = message.split(messageIdentifier + "|" + identityIdentifier);
			int groupID = Integer.parseInt(arr[1]);
			String senderID = arr[2];
			String msg = arr[3];

			processMessage(groupID, senderID, msg);

		} else if (message.startsWith(chatFormIdentifier)) {
			/**
			 * receives a msg like:
			 * "/f/groupName/i/clientID/i/member1UserName,member2UserName,..,"
			 */
			String[] arr = message.split(chatFormIdentifier + "|" + identityIdentifier);
			String groupName = arr[1];
			String groupCreaterID = arr[2];
			String groupMembersStr = arr[3];

			formGroup(groupName, groupCreaterID, groupMembersStr);

		}

	}

	private void processMessage(int groupID, String senderID, String msg) {
		RegisteredClient sender = ServerDao.fetchClient(senderID);
		if (sender == null) {
			return;
		}

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
		msg = groupIdentifier + messageIdentifier + groupID + identityIdentifier + sender.getUserName()
				+ identityIdentifier + msg;
		Iterator<RegisteredClient> iterator = members.iterator();
		System.out.println("Total clients: " + members.size());
		while (iterator.hasNext()) {
			RegisteredClient member = iterator.next();
			LoggedInClient loggedInClient = server.getLoggedInClient(member.getId());
			if (loggedInClient != null) {
				System.out.println("INSIDE");
				server.send(msg, loggedInClient.getIp(), loggedInClient.getPort());
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

	}

	private void formGroup(String groupName, String groupCreaterID, String groupMembersStr) {
		int groupID = UniqueIdentifier.getUniqueGroupIdentifier();
		String[] groupMembers = groupMembersStr.split(",");
		Set<RegisteredClient> members = new HashSet<RegisteredClient>();
		for (int i = 0; i < groupMembers.length; i++) {
			RegisteredClient member = server.getClientByUserName(groupMembers[i]);
			if (member != null) {
				members.add(member);
			}
		}
		Group group = ServerDao.fetchGroup(groupID, true);
		LoggedInClient groupCreater = server.getLoggedInClient(groupCreaterID);
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
				+ identityIdentifier + groupCreater.getClient().getUserName() + identityIdentifier + groupMembersStr;

		Iterator<RegisteredClient> iterator = members.iterator();
		while (iterator.hasNext()) {
			RegisteredClient member = iterator.next();
			LoggedInClient loggedInClient = server.getLoggedInClient(member.getId());
			if (loggedInClient != null) {
				server.send(messageToMembers, loggedInClient.getIp(), loggedInClient.getPort());
			} else {
				PendingGroupForm pendingGroupForm = new PendingGroupForm(member, group);
				ServerDao.savePendingGroupForm(pendingGroupForm);
			}
		}
	}

	public Group getGroup(int groupID) {
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

	public void sendGroupFormAck(Group group, LoggedInClient loggedInClient) {
		/**
		 * sends a msg to the member like:
		 * "/g//f/groupID/i/groupName/i/createrUserName/i/membersUserNames"
		 */

		Set<RegisteredClient> members = group.getMembers();
		RegisteredClient groupCreator = group.getCreator();
		String messageToMember = groupIdentifier + chatFormIdentifier + group.getId() + identityIdentifier
				+ group.getName() + identityIdentifier + groupCreator.getUserName() + identityIdentifier;

		Iterator<RegisteredClient> iterator = members.iterator();
		String membersStr = "";
		while (iterator.hasNext()) {
			RegisteredClient member = iterator.next();
			membersStr += member.getUserName() + ",";
		}
		messageToMember = messageToMember + membersStr;
		System.out.println("messageToMemeber: " + messageToMember);
		server.send(messageToMember, loggedInClient.getIp(), loggedInClient.getPort());
	}

	public void sendMessage(GroupMessage message, LoggedInClient loggedInClient) {
		/**
		 * sends a msg to logged in member like:
		 * "/g//m/groupID/i/senderUserName/i/message"
		 */
		String msg = groupIdentifier + messageIdentifier + message.getGroup().getId() + identityIdentifier
				+ message.getSender().getUserName() + identityIdentifier + message.getBody();
		server.send(msg, loggedInClient.getIp(), loggedInClient.getPort());

	}
}
