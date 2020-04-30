package scw.db.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import scw.beans.annotation.Bean;
import scw.core.instance.annotation.Configuration;

@Configuration(order = Integer.MIN_VALUE, value = Hibernate.class)
@Bean(proxy = false)
public class DefaultHibernate implements Hibernate {
	private final SessionFactory sessionFactory;

	public DefaultHibernate(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public Session getTransactionSession() {
		return HibernateUtils.getTransactionSession(getSessionFactory());
	}
}
