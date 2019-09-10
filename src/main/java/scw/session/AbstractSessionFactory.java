package scw.session;

import scw.login.DefaultSession;

public abstract class AbstractSessionFactory implements SessionFactory {
	private int defaultMaxInactiveInterval;

	public AbstractSessionFactory(int defaultMaxInactiveInterval) {
		this.defaultMaxInactiveInterval = defaultMaxInactiveInterval;
	}

	public Session getSession(String sessionId, boolean create) {
		SessionData sessionData;
		if (create) {
			sessionData = new SessionData();
			sessionData.setCreateTime(System.currentTimeMillis());
			sessionData.setMaxInactiveInterval(defaultMaxInactiveInterval);
			sessionData.setSessionId(sessionId);
		} else {
			sessionData = getSessionData(sessionId);
			if (sessionData == null) {
				return null;
			}
		}

		setSessionData(sessionData);
		return new DefaultSession(this, sessionData, create);
	}

	/**
	 * 设置最大过期时间
	 * 
	 * @param sessionId
	 * @param maxInactiveInterval
	 */
	public abstract void setMaxInactiveInterval(String sessionId, int maxInactiveInterval);

	public abstract SessionData getSessionData(String sessionId);

	public abstract void setSessionData(SessionData sessionData);

	public abstract void invalidate(String sessionId);
}
