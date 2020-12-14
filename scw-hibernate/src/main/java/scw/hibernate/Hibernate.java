package scw.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public interface Hibernate {
	SessionFactory getSessionFactory();
	
	Session getTransactionSession();
}
