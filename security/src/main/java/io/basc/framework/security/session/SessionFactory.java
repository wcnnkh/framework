package io.basc.framework.security.session;

public interface SessionFactory {
	int getMaxInactiveInterval();

	Session getSession(String sessionId);

	Session getSession(String sessionId, boolean create);
}
