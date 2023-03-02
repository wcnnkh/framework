package io.basc.framework.websocket;

import io.basc.framework.messageing.Message;

public interface WebSocketHandler {
	void afterConnectionEstablished(WebSocketSession session) throws Exception;

	void handleMessage(WebSocketSession session, Message<?> message) throws Exception;

	void handleTransportError(WebSocketSession session, Throwable exception) throws Exception;

	void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception;

	boolean supportsPartialMessages();
}
