package scw.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Enumeration;

import scw.util.message.session.AbstractSessionManager;
import scw.util.message.session.SessionManager;

public abstract class WebSocketSessionManager extends AbstractSessionManager
		implements SessionManager {
	public abstract WebSocketSession getSession(String sessionId);

	public void sendText(String sessionId, String text) throws Exception {
		WebSocketSession session = getSession(sessionId);
		if (session == null || !session.isOpen()) {
			logNotOpenSession(sessionId);
			return;
		}

		session.sendText(text);
	}

	public void sendTextToAll(String text) {
		Enumeration<String> enumeration = sessionIds();
		while (enumeration.hasMoreElements()) {
			String sessionId = enumeration.nextElement();
			try {
				sendText(sessionId, text);
			} catch (Exception e) {
				logSendMessageError(e, sessionId, text);
			}
		}
	}

	public void sendBinary(String sessionId, ByteBuffer byteBuffer)
			throws Exception {
		WebSocketSession session = getSession(sessionId);
		if (session == null || !session.isOpen()) {
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("{} not found or not open");
			}
			return;
		}

		session.sendBinary(byteBuffer);
	}

	public void sendBinaryToAll(ByteBuffer byteBuffer) {
		Enumeration<String> enumeration = sessionIds();
		while (enumeration.hasMoreElements()) {
			String sessionId = enumeration.nextElement();
			try {
				sendBinary(sessionId, byteBuffer);
			} catch (Exception e) {
				logSendMessageError(e, sessionId, byteBuffer);
			}
		}
	}

	public void sendPing(String sessionId, ByteBuffer byteBuffer)
			throws Exception {
		WebSocketSession session = getSession(sessionId);
		if (session == null || !session.isOpen()) {
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("{} not found or not open");
			}
			return;
		}

		session.sendPing(byteBuffer);
	}

	public void sendPingToAll(ByteBuffer byteBuffer) {
		Enumeration<String> enumeration = sessionIds();
		while (enumeration.hasMoreElements()) {
			String sessionId = enumeration.nextElement();
			try {
				sendPing(sessionId, byteBuffer);
			} catch (Exception e) {
				logSendMessageError(e, sessionId, byteBuffer);
			}
		}
	}

	public void sendPong(String sessionId, ByteBuffer byteBuffer)
			throws IOException {
		WebSocketSession session = getSession(sessionId);
		if (session == null || !session.isOpen()) {
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("{} not found or not open");
			}
			return;
		}

		session.sendPong(byteBuffer);
	}

	public void sendPongToAll(ByteBuffer byteBuffer) {
		Enumeration<String> enumeration = sessionIds();
		while (enumeration.hasMoreElements()) {
			String sessionId = enumeration.nextElement();
			try {
				sendPong(sessionId, byteBuffer);
			} catch (IOException e) {
				logSendMessageError(e, sessionId, byteBuffer);
			}
		}
	}

	public void sendObject(String sessionId, Object data) throws Exception {
		WebSocketSession session = getSession(sessionId);
		if (session == null || !session.isOpen()) {
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("{} not found or not open");
			}
			return;
		}

		session.sendObject(data);
	}

	public void sendObjectToAll(Object data) {
		Enumeration<String> enumeration = sessionIds();
		while (enumeration.hasMoreElements()) {
			String sessionId = enumeration.nextElement();
			try {
				sendObject(sessionId, data);
			} catch (Exception e) {
				logSendMessageError(e, sessionId, data);
			}
		}
	}
}
