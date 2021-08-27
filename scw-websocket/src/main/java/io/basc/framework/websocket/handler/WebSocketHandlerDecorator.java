package io.basc.framework.websocket.handler;

import io.basc.framework.core.Assert;
import io.basc.framework.messageing.Message;
import io.basc.framework.websocket.CloseStatus;
import io.basc.framework.websocket.WebSocketHandler;
import io.basc.framework.websocket.WebSocketSession;

public class WebSocketHandlerDecorator implements WebSocketHandler {

	private final WebSocketHandler delegate;


	public WebSocketHandlerDecorator(WebSocketHandler delegate) {
		Assert.notNull(delegate, "Delegate must not be null");
		this.delegate = delegate;
	}


	public WebSocketHandler getDelegate() {
		return this.delegate;
	}

	public WebSocketHandler getLastHandler() {
		WebSocketHandler result = this.delegate;
		while (result instanceof WebSocketHandlerDecorator) {
			result = ((WebSocketHandlerDecorator) result).getDelegate();
		}
		return result;
	}

	public static WebSocketHandler unwrap(WebSocketHandler handler) {
		if (handler instanceof WebSocketHandlerDecorator) {
			return ((WebSocketHandlerDecorator) handler).getLastHandler();
		}
		else {
			return handler;
		}
	}

	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		this.delegate.afterConnectionEstablished(session);
	}

	public void handleMessage(WebSocketSession session, Message<?> message) throws Exception {
		this.delegate.handleMessage(session, message);
	}

	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		this.delegate.handleTransportError(session, exception);
	}

	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		this.delegate.afterConnectionClosed(session, closeStatus);
	}

	public boolean supportsPartialMessages() {
		return this.delegate.supportsPartialMessages();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [delegate=" + this.delegate + "]";
	}

}
