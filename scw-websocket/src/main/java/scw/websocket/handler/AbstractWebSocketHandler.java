package scw.websocket.handler;

import scw.messageing.BinaryMessage;
import scw.messageing.Message;
import scw.messageing.TextMessage;
import scw.websocket.CloseStatus;
import scw.websocket.PongMessage;
import scw.websocket.WebSocketHandler;
import scw.websocket.WebSocketSession;

public class AbstractWebSocketHandler implements WebSocketHandler {

	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
	}

	public void handleMessage(WebSocketSession session, Message<?> message) throws Exception {
		if (message instanceof TextMessage) {
			handleTextMessage(session, (TextMessage) message);
		} else if (message instanceof BinaryMessage) {
			handleBinaryMessage(session, (BinaryMessage) message);
		} else if (message instanceof PongMessage) {
			handlePongMessage(session, (PongMessage) message);
		} else {
			throw new IllegalStateException("Unexpected WebSocket message type: " + message);
		}
	}

	/**
	 * 收到文本消息时
	 * @param session
	 * @param message
	 * @throws Exception
	 */
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
	}

	/**
	 * 收到二进制消息时
	 * @param session
	 * @param message
	 * @throws Exception
	 */
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
	}

	/**
	 * 收到pong消息
	 * @param session
	 * @param message
	 * @throws Exception
	 */
	protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
	}

	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
	}

	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
	}

	public boolean supportsPartialMessages() {
		return false;
	}

}
