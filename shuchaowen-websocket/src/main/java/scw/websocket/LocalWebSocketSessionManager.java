package scw.websocket;

import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.message.session.Session;

public class LocalWebSocketSessionManager extends WebSocketSessionManager {
	private static Logger logger = LoggerUtils
			.getLogger(LocalWebSocketSessionManager.class);

	private ConcurrentHashMap<String, WebSocketSession> sessionMap = new ConcurrentHashMap<String, WebSocketSession>();

	public void addSession(Session session) {
		Session oldSession = sessionMap.put(session.getId(),
				(WebSocketSession) session);
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
	public WebSocketSession getSession(String sessionId) {
		return sessionMap.get(sessionId);
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

}
