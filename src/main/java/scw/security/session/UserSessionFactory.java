package scw.security.session;

import scw.beans.annotation.AutoImpl;

@AutoImpl(impl=DefaultUserSessionFactory.class)
public interface UserSessionFactory<T> {
	UserSession<T> getSession(T uid);

	UserSession<T> getSession(String sessionId);

	UserSession<T> getSession(T uid, String sessionId, boolean create);
}
