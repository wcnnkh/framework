package io.basc.framework.messageing.session;

import io.basc.framework.messageing.Message;

import java.io.IOException;
import java.util.Enumeration;

public interface SessionManager {
	Session getSession(String sessionId);

	void addSession(Session session);

	void removeSession(String sessionId);

	Enumeration<String> sessionIds();
	
	void sendMessage(String sessionId, Message<?> message) throws IOException;
	
	void sendMessageToAll(Message<?> message);
}
