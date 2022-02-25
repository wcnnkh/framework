package io.basc.framework.security.session;

import java.util.concurrent.TimeUnit;

import io.basc.framework.data.storage.TemporaryStorage;

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
		return temporaryCache.get(SessionData.class, getKey(sessionId));
	}

	@Override
	public void setSessionData(SessionData sessionData) {
		temporaryCache.set(getKey(sessionData.getSessionId()), sessionData, sessionData.getMaxInactiveInterval(),
				TimeUnit.SECONDS);
	}

	@Override
	public void invalidate(String sessionId) {
		temporaryCache.delete(getKey(sessionId));
	}

}
