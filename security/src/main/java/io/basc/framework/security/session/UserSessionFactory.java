package io.basc.framework.security.session;

public interface UserSessionFactory {
	<T> UserSessions<T> getUserSessions(T uid);

	default <T> UserSession<T> getUserSession(T uid, String sessionId) {
		return getUserSession(uid, sessionId, false);
	}

	<T> UserSession<T> getUserSession(T uid, String sessionId, boolean create);
}
