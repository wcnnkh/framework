package io.basc.framework.security.session;

import io.basc.framework.data.TemporaryStorage;

public class DefaultSessionFactory extends AbstractSessionFactory {
	private TemporaryStorage temporaryCache;

	public DefaultSessionFactory(int defaultMaxInactiveInterval, TemporaryStorage temporaryCache) {
		super(defaultMaxInactiveInterval);
		this.temporaryCache = temporaryCache;
	}

	protected String getKey(String sessionId) {
		return "session-factory:" + sessionId;
	}

	@Override
	public SessionData getSessionData(String sessionId) {
		return (SessionData) temporaryCache.get(getKey(sessionId));
	}

	@Override
	public void setSessionData(SessionData sessionData) {
		temporaryCache.set(getKey(sessionData.getSessionId()), sessionData.getMaxInactiveInterval(), sessionData);
	}

	@Override
	public void invalidate(String sessionId) {
		temporaryCache.delete(getKey(sessionId));
	}

}
