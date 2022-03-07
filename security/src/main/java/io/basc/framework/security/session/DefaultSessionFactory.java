package io.basc.framework.security.session;

import java.util.concurrent.TimeUnit;

import io.basc.framework.data.TemporaryStorageOperations;

public class DefaultSessionFactory extends AbstractSessionFactory {
	private final TemporaryStorageOperations storageOperations;

	public DefaultSessionFactory(int defaultMaxInactiveInterval, TemporaryStorageOperations storageOperations) {
		super(defaultMaxInactiveInterval);
		this.storageOperations = storageOperations;
	}

	protected String getKey(String sessionId) {
		return "session-factory:" + sessionId;
	}

	@Override
	public SessionData getSessionData(String sessionId) {
		return storageOperations.get(SessionData.class, getKey(sessionId));
	}

	@Override
	public void setSessionData(SessionData sessionData) {
		storageOperations.set(getKey(sessionData.getSessionId()), sessionData, sessionData.getMaxInactiveInterval(),
				TimeUnit.SECONDS);
	}

	@Override
	public void invalidate(String sessionId) {
		storageOperations.delete(getKey(sessionId));
	}

}
