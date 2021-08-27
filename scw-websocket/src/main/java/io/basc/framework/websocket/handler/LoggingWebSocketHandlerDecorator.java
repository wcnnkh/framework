package io.basc.framework.websocket.handler;

import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.messageing.Message;
import io.basc.framework.websocket.CloseStatus;
import io.basc.framework.websocket.WebSocketHandler;
import io.basc.framework.websocket.WebSocketSession;

public class LoggingWebSocketHandlerDecorator extends WebSocketHandlerDecorator {

	private static final Logger logger = LoggerFactory.getLogger(LoggingWebSocketHandlerDecorator.class);


	public LoggingWebSocketHandlerDecorator(WebSocketHandler delegate) {
		super(delegate);
	}


	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("New "	+ session);
		}
		super.afterConnectionEstablished(session);
	}

	@Override
	public void handleMessage(WebSocketSession session, Message<?> message) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace("Handling " + message + " in " + session);
		}
		super.handleMessage(session, message);
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Transport error in " + session, exception);
		}
		super.handleTransportError(session, exception);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug(session + " closed with " + closeStatus);
		}
		super.afterConnectionClosed(session, closeStatus);
	}

}
