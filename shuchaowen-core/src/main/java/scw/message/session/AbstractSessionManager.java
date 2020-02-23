package scw.message.session;

import java.util.Enumeration;

import scw.logger.Logger;
import scw.message.Message;

public abstract class AbstractSessionManager implements SessionManager {
	protected abstract Logger getLogger();

	public void send(String sessionId, Message<?> message) throws Exception {
		Session session = getSession(sessionId);
		if (session == null || !session.isOpen()) {
			logNotOpenSession(sessionId);
			return;
		}

		session.send(message);
	}
	
	protected void logCloseError(Throwable e, Session session){
		getLogger().error(e, "session close error: {}", session);
	}

	protected void logNotOpenSession(String sessionId) {
		if (getLogger().isDebugEnabled()) {
			getLogger().debug("not found or session not open:{}", sessionId);
		}
	}

	protected void logSendMessageError(Throwable e, String sessionId,
			Object message) {
		getLogger().error(e, "session [{}] send message [{}]", sessionId,
				message);
	}

	public void sendAll(Message<?> message) {
		Enumeration<String> enumeration = sessionIds();
		while (enumeration.hasMoreElements()) {
			String sessionId = enumeration.nextElement();
			try {
				send(sessionId, message);
			} catch (Exception e) {
				logSendMessageError(e, sessionId, message);
			}
		}
	}
}
