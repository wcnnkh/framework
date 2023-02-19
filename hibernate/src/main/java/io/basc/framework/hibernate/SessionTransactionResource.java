package io.basc.framework.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import io.basc.framework.transaction.Resource;

public class SessionTransactionResource implements Resource {
	private final SessionFactory sessionFactory;
	private final boolean isActive;

	private Session session;
	private Transaction transaction;

	public SessionTransactionResource(SessionFactory sessionFactory, boolean isActive) {
		this.sessionFactory = sessionFactory;
		this.isActive = isActive;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public Session getSession() {
		if (session == null) {
			session = HibernateUtils.proxySession(sessionFactory.openSession());
			if (isActive) {
				this.transaction = session.beginTransaction();
			}
		}
		return session;
	}

	public void commit() throws Throwable {
		if (transaction != null) {
			transaction.commit();
		}
	}

	public void rollback() {
		if (transaction != null) {
			transaction.rollback();
		}
	}

	public void close() {
		if (session != null) {
			HibernateUtils.closeProxySession(session);
		}
	}
}
