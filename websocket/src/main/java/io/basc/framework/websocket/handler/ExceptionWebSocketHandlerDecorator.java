package io.basc.framework.websocket.handler;

import io.basc.framework.messageing.Message;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LoggerFactory;
import io.basc.framework.websocket.CloseStatus;
import io.basc.framework.websocket.WebSocketHandler;
import io.basc.framework.websocket.WebSocketSession;

public class ExceptionWebSocketHandlerDecorator extends WebSocketHandlerDecorator {
	private static final Logger logger = LoggerFactory.getLogger(ExceptionWebSocketHandlerDecorator.class);

	public ExceptionWebSocketHandlerDecorator(WebSocketHandler delegate) {
		super(delegate);
	}


	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		try {
			getDelegate().afterConnectionEstablished(session);
		}
		catch (Exception ex) {
			tryCloseWithError(session, ex, logger);
		}
	}

	@Override
	public void handleMessage(WebSocketSession session, Message<?> message) {
		try {
			getDelegate().handleMessage(session, message);
		}
		catch (Exception ex) {
			tryCloseWithError(session, ex, logger);
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		try {
			getDelegate().handleTransportError(session, exception);
		}
		catch (Exception ex) {
			tryCloseWithError(session, ex, logger);
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
		try {
			getDelegate().afterConnectionClosed(session, closeStatus);
		}
		catch (Exception ex) {
			if (logger.isWarnEnabled()) {
				logger.warn("Unhandled exception after connection closed for " + this, ex);
			}
		}
	}


	public static void tryCloseWithError(WebSocketSession session, Throwable exception, Logger logger) {
		if (logger.isErrorEnabled()) {
			logger.error("Closing session due to exception for " + session, exception);
		}
		if (session.isOpen()) {
			try {
				session.close(CloseStatus.SERVER_ERROR);
			}
			catch (Throwable ex) {
				// ignore
			}
		}
	}

}