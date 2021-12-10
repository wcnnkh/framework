package io.basc.framework.messageing.session;

import io.basc.framework.logger.Logger;
import io.basc.framework.messageing.Message;

import java.io.IOException;
import java.util.Enumeration;

public abstract class AbstractSessionManager implements SessionManager {
	protected abstract Logger getLogger();

	public void sendMessage(String sessionId, Message<?> message) throws IOException {
		Session session = getSession(sessionId);
		if (session == null || !session.isOpen()) {
			logNotOpenSession(sessionId);
			return;
		}

		session.sendMessage(message);
	}

	protected void logCloseError(Throwable e, Session session) {
		getLogger().error(e, "session close error: {}", session);
	}

	protected void logNotOpenSession(String sessionId) {
		if (getLogger().isDebugEnabled()) {
			getLogger().debug("not found or session not open:{}", sessionId);
		}
	}

	protected void logSendMessageError(Throwable e, String sessionId, Object message) {
		getLogger().error(e, "session [{}] send message [{}]", sessionId, message);
	}

	public void sendMessageToAll(Message<?> message) {
		Enumeration<String> enumeration = sessionIds();
		while (enumeration.hasMoreElements()) {
			String sessionId = enumeration.nextElement();
			try {
				sendMessage(sessionId, message);
			} catch (Exception e) {
				logSendMessageError(e, sessionId, message);
			}
		}
	}
}
