package scw.db.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import scw.db.DB;

public interface HibernateDB extends DB{
	SessionFactory getSessionFactory();
	
	Session getSession();
}
