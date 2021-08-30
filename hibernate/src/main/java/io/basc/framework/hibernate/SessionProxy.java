package io.basc.framework.hibernate;

import org.hibernate.Session;

public interface SessionProxy extends Session{
	Session getTargetSession();
}
