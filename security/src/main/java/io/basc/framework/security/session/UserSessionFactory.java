package io.basc.framework.security.session;

public interface UserSessionFactory {
	<T> UserSessions<T> getUserSessions(T uid);

	default <T> UserSession<T> getUserSession(T uid, String sessionId) {
		return getUserSession(uid, sessionId, false);
	}

	/**
	 * 获取指定session
	 * 
	 * @param <T>
	 * @param uid
	 * @param sessionId
	 * @param create    如果不存在是否创建
	 * @return
	 */
	<T> UserSession<T> getUserSession(T uid, String sessionId, boolean create);
}
