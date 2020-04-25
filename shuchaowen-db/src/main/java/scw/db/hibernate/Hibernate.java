package scw.db.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public interface Hibernate {
	SessionFactory getSessionFactory();
	
	Session getSession();
}
