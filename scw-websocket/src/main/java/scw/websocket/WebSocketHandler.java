package scw.websocket;

import scw.messageing.Message;

public interface WebSocketHandler {
	/**
	 * 打开连接之后
	 * Invoked after WebSocket negotiation has succeeded and the WebSocket connection is
	 * opened and ready for use.
	 * @throws Exception this method can handle or propagate exceptions; see class-level
	 * Javadoc for details.
	 */
	void afterConnectionEstablished(WebSocketSession session) throws Exception;

	/**
	 * 收到消息
	 * Invoked when a new WebSocket message arrives.
	 * @throws Exception this method can handle or propagate exceptions; see class-level
	 * Javadoc for details.
	 */
	void handleMessage(WebSocketSession session, Message<?> message) throws Exception;

	/**
	 * 发生错误时
	 * Handle an error from the underlying WebSocket message transport.
	 * @throws Exception this method can handle or propagate exceptions; see class-level
	 * Javadoc for details.
	 */
	void handleTransportError(WebSocketSession session, Throwable exception) throws Exception;

	/**
	 * 连接关闭后
	 * Invoked after the WebSocket connection has been closed by either side, or after a
	 * transport error has occurred. Although the session may technically still be open,
	 * depending on the underlying implementation, sending messages at this point is
	 * discouraged and most likely will not succeed.
	 * @throws Exception this method can handle or propagate exceptions; see class-level
	 * Javadoc for details.
	 */
	void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception;

	/**
	 * 是否支持消息分包
	 * Whether the WebSocketHandler handles partial messages. If this flag is set to
	 * {@code true} and the underlying WebSocket server supports partial messages,
	 * then a large WebSocket message, or one of an unknown size may be split and
	 * maybe received over multiple calls to
	 */
	boolean supportsPartialMessages();
}
