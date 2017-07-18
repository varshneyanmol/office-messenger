package com.app.server.hibernate.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
	private static Configuration configuration = null;
	private static SessionFactory sessionFactory = null;

	private HibernateUtil() {
	}

	public static Session openSession() {
		Session session = null;
		if (configuration == null) {
			configuration = new Configuration();
			configuration.configure("com/app/server/hibernate/hibernate.cfg.xml");
			sessionFactory = configuration.buildSessionFactory();
		}

		session = sessionFactory.openSession();
		return session;
	}
}