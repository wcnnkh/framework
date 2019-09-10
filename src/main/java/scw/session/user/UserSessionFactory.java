package scw.session.user;

import scw.session.Session;

public interface UserSessionFactory<T> {
	T getUid(String sessionId);

	Session getSession(String sessionId);

	Session getSession(T uid, String sessionId, boolean create);
}
