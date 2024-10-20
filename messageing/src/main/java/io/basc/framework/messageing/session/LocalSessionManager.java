package io.basc.framework.messageing.session;

import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;

public class LocalSessionManager extends AbstractSessionManager {
	private static Logger logger = LogManager.getLogger(LocalSessionManager.class);
	private ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<String, Session>();

	public Session getSession(String sessionId) {
		return sessionMap.get(sessionId);
	}

	public void addSession(Session session) {
		Session oldSession = sessionMap.put(session.getId(), session);
		if (oldSession != null) {
			try {
				oldSession.close();
			} catch (IOException e) {
				logCloseError(e, oldSession);
			}
		}
	}

	public void removeSession(String sessionId) {
		sessionMap.remove(sessionId);
	}

	public Enumeration<String> sessionIds() {
		return sessionMap.keys();
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

}
