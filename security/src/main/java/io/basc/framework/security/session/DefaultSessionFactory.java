package io.basc.framework.security.session;

import java.util.concurrent.TimeUnit;

import io.basc.framework.data.TemporaryDataOperations;

public class DefaultSessionFactory extends AbstractSessionFactory {
	private final TemporaryDataOperations dataOperations;

	public DefaultSessionFactory(int defaultMaxInactiveInterval, TemporaryDataOperations dataOperations) {
		super(defaultMaxInactiveInterval);
		this.dataOperations = dataOperations;
	}

	protected String getKey(String sessionId) {
		return "session-factory:" + sessionId;
	}

	@Override
	public SessionData getSessionData(String sessionId) {
		return dataOperations.get(SessionData.class, getKey(sessionId));
	}

	@Override
	public void setSessionData(SessionData sessionData) {
		dataOperations.set(getKey(sessionData.getSessionId()), sessionData, sessionData.getMaxInactiveInterval(),
				TimeUnit.SECONDS);
	}

	@Override
	public void invalidate(String sessionId) {
		dataOperations.delete(getKey(sessionId));
	}

}
