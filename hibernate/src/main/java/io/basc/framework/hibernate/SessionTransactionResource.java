package io.basc.framework.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import io.basc.framework.transaction.Savepoint;
import io.basc.framework.transaction.Synchronization;
import io.basc.framework.transaction.TransactionException;
import io.basc.framework.transaction.TransactionStatus;

public class SessionTransactionResource implements Synchronization {
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

	public void complete() {
		if (session != null) {
			HibernateUtils.closeProxySession(session);
		}
	}

	public Savepoint createSavepoint() throws TransactionException {
		return null;
	}

	@Override
	public void beforeCompletion() throws Throwable {
		commit();
	}

	@Override
	public void afterCompletion(TransactionStatus status) {
		if (status.equals(TransactionStatus.ROLLING_BACK)) {
			rollback();
		}

		if (status.equals(TransactionStatus.COMPLETED)) {
			complete();
		}
	}

}
