package io.basc.framework.hibernate;

import java.lang.reflect.Proxy;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import io.basc.framework.tx.Transaction;
import io.basc.framework.tx.TransactionUtils;

public final class HibernateUtils {

	private HibernateUtils() {
	};

	public static Session proxySession(Session session) {
		if (session == null) {
			return null;
		}

		if (session instanceof SessionProxy) {
			return session;
		}

		return (Session) Proxy.newProxyInstance(SessionProxy.class.getClassLoader(),
				new Class<?>[] { SessionProxy.class }, new SessionProxyInvocationHandler(session));
	}

	public static void closeProxySession(Session session) {
		if (session == null) {
			return;
		}

		if (session instanceof SessionProxy) {
			((SessionProxy) session).getTargetSession().close();
			return;
		}

		session.close();
	}

	public static Session getTransactionSession(SessionFactory sessionFactory) {
		Transaction transaction = TransactionUtils.getManager().getTransaction();
		if (transaction == null) {
			return sessionFactory.openSession();
		}

		SessionTransactionResource resource = transaction.getResource(sessionFactory);
		if (resource == null) {
			resource = new SessionTransactionResource(sessionFactory, transaction.isActive());
			transaction.registerResource(sessionFactory, resource);
		}
		return resource.getSession();
	}
}
