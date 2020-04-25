package scw.db.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import scw.transaction.TransactionException;
import scw.transaction.TransactionResource;
import scw.transaction.savepoint.Savepoint;

public class HibernateTransactionResource implements TransactionResource {
	private final SessionFactory sessionFactory;
	private final boolean isActive;
	
	private Session session;
	private Transaction transaction;

	public HibernateTransactionResource(SessionFactory sessionFactory,
			boolean isActive) {
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

	public void process() throws Throwable {
		if (transaction != null) {
			transaction.commit();
		}
	}

	public void rollback() {
		if (transaction != null) {
			transaction.rollback();
		}
	}

	public void end() {
		if (session != null) {
			HibernateUtils.closeProxySession(session);
		}
	}

	public Savepoint createSavepoint() throws TransactionException {
		return null;
	}

}
