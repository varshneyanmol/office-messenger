package com.app.server.hibernate.dao;

import java.util.ArrayList;
import java.util.Iterator;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;

import com.app.server.Group;
import com.app.server.PrivateChat;
import com.app.server.RegisteredClient;
import com.app.server.hibernate.model.GroupMessage;
import com.app.server.hibernate.model.Message;
import com.app.server.hibernate.model.Pending;
import com.app.server.hibernate.model.PendingGroupForm;
import com.app.server.hibernate.model.PendingGroupMessage;
import com.app.server.hibernate.model.PendingPrivateChatForm;
import com.app.server.hibernate.model.PendingPrivateMessage;
import com.app.server.hibernate.model.PrivateMessage;
import com.app.server.hibernate.util.HibernateUtil;

public class ServerDao {

	private static Session session;

	public static boolean saveClient(RegisteredClient client) {
		String id = null;
		Session session = HibernateUtil.openSession();
		Transaction tx = session.beginTransaction();
		id = (String) session.save(client);
		tx.commit();
		session.close();
		if (id != null) {
			return true;
		}
		return false;
	}

	public static RegisteredClient fetchClient(String userName, String password) {
		RegisteredClient client = null;

		Session session = HibernateUtil.openSession();
		Criteria criteria = session.createCriteria(RegisteredClient.class);
		Criterion userNameCriterion = Restrictions.eq("userName", userName);
		Criterion passwordCriterion = Restrictions.eq("password", password);
		criteria.add(Restrictions.and(userNameCriterion, passwordCriterion));
		client = (RegisteredClient) criteria.uniqueResult();
		session.close();

		return client;
	}

	public static RegisteredClient fetchClient(String id) {
		RegisteredClient client = null;

		Session session = HibernateUtil.openSession();
		Criteria criteria = session.createCriteria(RegisteredClient.class);
		Criterion idCriterion = Restrictions.eq("id", id);
		criteria.add(idCriterion);
		client = (RegisteredClient) criteria.uniqueResult();
		session.close();

		return client;
	}

	public static RegisteredClient fetchClientByUserName(String userName) {
		RegisteredClient client = null;

		Session session = HibernateUtil.openSession();
		Criteria criteria = session.createCriteria(RegisteredClient.class);
		Criterion idCriterion = Restrictions.eq("userName", userName);
		criteria.add(idCriterion);
		client = (RegisteredClient) criteria.uniqueResult();
		session.close();

		return client;
	}

	public static ArrayList<RegisteredClient> fetchAllClients() {
		ArrayList<RegisteredClient> list = new ArrayList<RegisteredClient>();
		Session session = HibernateUtil.openSession();
		Criteria criteria = session.createCriteria(RegisteredClient.class);
		list = (ArrayList<RegisteredClient>) criteria.list();
		session.close();
		return list;
	}

	public static boolean saveGroup(Group group) {
		int id = -1;
		Session session = HibernateUtil.openSession();
		Transaction tx = session.beginTransaction();
		id = (int) session.save(group);
		tx.commit();
		session.close();
		if (id != -1) {
			return true;
		}
		return false;
	}

	public static Group fetchGroup(int groupID, boolean fetchEager) {
		Group group = null;

		Session session = HibernateUtil.openSession();

		Criteria criteria = session.createCriteria(Group.class);
		Criterion idCriterion = Restrictions.eq("id", groupID);
		criteria.add(idCriterion);
		group = (Group) criteria.uniqueResult();
		if (group != null && fetchEager) {
			int i = (group.getMembers().size());
		}
		session.close();

		return group;
	}

	public static GroupMessage fetchParent(Group group) {
		GroupMessage parent = null;

		Session session = HibernateUtil.openSession();
		String queryStr = "FROM GroupMessage WHERE group.id=" + group.getId() + " ORDER BY id DESC";
		Query query = session.createQuery(queryStr);
		query.setMaxResults(1);
		parent = (GroupMessage) query.uniqueResult();
		session.close();
		return parent;
	}

	public static PrivateMessage fetchParent(PrivateChat privateChat) {
		PrivateMessage parent = null;

		Session session = HibernateUtil.openSession();
		String queryStr = "FROM PrivateMessage WHERE privateChat.id='" + privateChat.getId() + "' ORDER BY id DESC";
		Query query = session.createQuery(queryStr);
		query.setMaxResults(1);
		parent = (PrivateMessage) query.uniqueResult();
		session.close();
		return parent;
	}

	public static boolean saveGroupMessage(GroupMessage messageDB) {
		long id = -1L;
		Session session = HibernateUtil.openSession();
		Transaction tx = session.beginTransaction();
		id = (long) session.save(messageDB);
		tx.commit();
		session.close();
		if (id != -1L) {
			return true;
		}
		return false;
	}

	public static boolean savePrivateMessage(PrivateMessage messageDB) {
		long id = -1L;
		Session session = HibernateUtil.openSession();
		Transaction tx = session.beginTransaction();
		id = (long) session.save(messageDB);
		tx.commit();
		session.close();
		if (id != -1L) {
			return true;
		}
		return false;
	}

	public static boolean savePrivateChat(PrivateChat privateChat) {
		String id = null;
		Session session = HibernateUtil.openSession();
		Transaction tx = session.beginTransaction();
		id = (String) session.save(privateChat);
		tx.commit();
		session.close();
		if (id != null) {
			return true;
		}
		return false;
	}

	public static PrivateChat fetchPrivateChat(String privateChatID) {

		PrivateChat privateChat = null;

		Session session = HibernateUtil.openSession();
		Criteria criteria = session.createCriteria(PrivateChat.class);
		Criterion idCriterion = Restrictions.eq("id", privateChatID);
		criteria.add(idCriterion);
		privateChat = (PrivateChat) criteria.uniqueResult();
		session.close();

		return privateChat;

	}

	public static PrivateChat fetchPrivateChat(String privateChatID1, String privateChatID2) {

		PrivateChat privateChat = null;

		Session session = HibernateUtil.openSession();
		Criteria criteria = session.createCriteria(PrivateChat.class);
		Criterion idCriterion1 = Restrictions.eq("id", privateChatID1);
		Criterion idCriterion2 = Restrictions.eq("id", privateChatID2);
		criteria.add(Restrictions.or(idCriterion1, idCriterion2));
		privateChat = (PrivateChat) criteria.uniqueResult();
		session.close();

		return privateChat;

	}

	public static boolean savePendingGroupForm(PendingGroupForm pendingGroupForm) {
		long id = -1L;
		Session session = HibernateUtil.openSession();
		Transaction tx = session.beginTransaction();
		id = (long) session.save(pendingGroupForm);
		tx.commit();
		session.close();
		if (id != -1L) {
			return true;
		}
		return false;

	}

	public static boolean savePendingPrivateChatForm(PendingPrivateChatForm pendingPrivateChatForm) {
		long id = -1L;
		Session session = HibernateUtil.openSession();
		Transaction tx = session.beginTransaction();
		id = (long) session.save(pendingPrivateChatForm);
		tx.commit();
		session.close();
		System.out.println("PRIVATE CHAT FORM ID: " + id);
		if (id != -1L) {
			return true;
		}
		return false;

	}

	public static boolean savePendingGroupMessage(PendingGroupMessage pendingGroupMessage) {
		long id = -1L;
		Session session = HibernateUtil.openSession();
		Transaction tx = session.beginTransaction();
		id = (long) session.save(pendingGroupMessage);
		tx.commit();
		session.close();
		if (id != -1L) {
			return true;
		}
		return false;

	}

	public static boolean savePendingPrivateMessage(PendingPrivateMessage pendingPrivateMessage) {
		long id = -1L;
		Session session = HibernateUtil.openSession();
		Transaction tx = session.beginTransaction();
		id = (long) session.save(pendingPrivateMessage);
		tx.commit();
		session.close();
		if (id != -1L) {
			return true;
		}
		return false;
	}

	public static PendingPrivateMessage fetchPendingPrivateMessage(PrivateChat privateChat, boolean fetchEager) {
		PendingPrivateMessage pendingPrivateMessage = null;

		Session session = HibernateUtil.openSession();
		Criteria criteria = session.createCriteria(PendingPrivateMessage.class);
		Criterion criterion = Restrictions.eq("privateChat.id", privateChat.getId());
		criteria.add(criterion);
		pendingPrivateMessage = (PendingPrivateMessage) criteria.uniqueResult();
		if (pendingPrivateMessage != null && fetchEager) {
			pendingPrivateMessage.getLatestMessage().getBody();
		}
		session.close();
		return pendingPrivateMessage;
	}

	public static boolean updatePendingPrivateMessage(PendingPrivateMessage pendingPrivateMessage,
			PrivateMessage messageDB, int totalPending) {
		long id = -1L;
		pendingPrivateMessage.setLatestMessage(messageDB);
		pendingPrivateMessage.setTotalPending(totalPending);
		Session session = HibernateUtil.openSession();
		Transaction tx = session.beginTransaction();
		session.update(pendingPrivateMessage);
		tx.commit();
		session.close();
		if (id != -1L) {
			return true;
		}
		return false;

	}

	public static PendingGroupMessage fetchPendingGroupMessage(RegisteredClient member, Group group,
			boolean fetchEager) {
		PendingGroupMessage pendingGroupMessage = null;
		System.out.println("INSIDE getchPendingGroupMessage()");
		Session session = HibernateUtil.openSession();
		Criteria criteria = session.createCriteria(PendingGroupMessage.class);
		Criterion criterion1 = Restrictions.eq("group.id", group.getId());
		System.out.println("CRITERION1 CREATED");
		Criterion criterion2 = Restrictions.eq("client.id", member.getId());
		System.out.println("CRITERION2 CREATED");
		criteria.add(Restrictions.and(criterion1, criterion2));
		System.out.println("CRITERIAS ADDED");
		pendingGroupMessage = (PendingGroupMessage) criteria.uniqueResult();
		System.out.println("CRITERIA FETCHED");
		if (pendingGroupMessage != null && fetchEager) {
			pendingGroupMessage.getLatestMessage().getBody();
		}
		session.close();
		return pendingGroupMessage;
	}

	public static boolean updatePendingGroupMessage(PendingGroupMessage pendingGroupMessage, GroupMessage messageDB,
			int totalPending) {
		long id = -1L;
		pendingGroupMessage.setLatestMessage(messageDB);
		pendingGroupMessage.setTotalPending(totalPending);
		Session session = HibernateUtil.openSession();
		Transaction tx = session.beginTransaction();
		session.update(pendingGroupMessage);
		tx.commit();
		session.close();
		if (id != -1L) {
			return true;
		}
		return false;
	}

	public static ArrayList<Pending> fetchAllPending(RegisteredClient client) {
		ArrayList<Pending> pendings = null;
		Session session = HibernateUtil.openSession();
		Criteria criteria = session.createCriteria(Pending.class);
		Criterion criterion = Restrictions.eq("client.id", client.getId());
		criteria.add(criterion);
		pendings = (ArrayList<Pending>) criteria.list();

		for (int i = 0; i < pendings.size(); i++) {

			Pending pending = pendings.get(i);
			if (pending instanceof PendingPrivateChatForm) {
				PendingPrivateChatForm pendingPrivateChatForm = (PendingPrivateChatForm) pending;
				pendingPrivateChatForm.getPrivateChat().getClient1();

			} else if (pending instanceof PendingGroupForm) {
				PendingGroupForm pendingGroupForm = (PendingGroupForm) pending;
				pendingGroupForm.getGroup().getCreator().getUserName();
				Iterator<RegisteredClient> iterator = pendingGroupForm.getGroup().getMembers().iterator();
				while (iterator.hasNext()) {
					iterator.next().getUserName();
				}

			} else if (pending instanceof PendingPrivateMessage) {
				PendingPrivateMessage pendingPrivateMessage = (PendingPrivateMessage) pending;

			} else if (pending instanceof PendingGroupMessage) {
				PendingGroupMessage pendingGroupMessage = (PendingGroupMessage) pending;
				pendingGroupMessage.getGroup().getMembers();
			}
		}
		session.close();
		return pendings;
	}
}
