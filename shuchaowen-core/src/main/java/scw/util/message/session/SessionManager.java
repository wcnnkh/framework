package scw.util.message.session;

import java.util.Enumeration;

import scw.util.message.Message;

public interface SessionManager {
	Session getSession(String sessionId);

	void addSession(Session session);

	void removeSession(String sessionId);

	Enumeration<String> sessionIds();
	
	void send(String sessionId, Message<?> message) throws Exception;
	
	void sendAll(Message<?> message);
}
