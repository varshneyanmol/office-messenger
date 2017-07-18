package com.app.server.hibernate.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.app.server.RegisteredClient;
import com.app.server.hibernate.util.HibernateUtil;

public class ServerDao {
	public static boolean saveClient(RegisteredClient client) {
		int id = -1;
		Session session = HibernateUtil.openSession();
		Transaction tx = session.beginTransaction();
		id = (Integer) session.save(client);
		tx.commit();
		session.close();
		if (id != -1) {
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

	public static RegisteredClient fetchClient(int id) {
		RegisteredClient client = null;

		Session session = HibernateUtil.openSession();
		Criteria criteria = session.createCriteria(RegisteredClient.class);
		Criterion idCriterion = Restrictions.eq("id", id);
		criteria.add(idCriterion);
		client = (RegisteredClient) criteria.uniqueResult();
		session.close();

		return client;
	}

}
