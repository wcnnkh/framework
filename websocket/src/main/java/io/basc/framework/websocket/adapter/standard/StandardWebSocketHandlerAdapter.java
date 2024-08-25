package io.basc.framework.websocket.adapter.standard;

import io.basc.framework.messageing.BinaryFragmentMessage;
import io.basc.framework.messageing.BinaryMessage;
import io.basc.framework.messageing.TextFragmentMessage;
import io.basc.framework.messageing.TextMessage;
import io.basc.framework.util.Assert;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LoggerFactory;
import io.basc.framework.websocket.CloseStatus;
import io.basc.framework.websocket.PongMessage;
import io.basc.framework.websocket.WebSocketHandler;
import io.basc.framework.websocket.handler.ExceptionWebSocketHandlerDecorator;

import java.nio.ByteBuffer;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;

public class StandardWebSocketHandlerAdapter extends Endpoint {
	private static final Logger logger = LoggerFactory.getLogger(StandardWebSocketHandlerAdapter.class);

	private final WebSocketHandler handler;

	private final StandardWebSocketSession wsSession;

	public StandardWebSocketHandlerAdapter(WebSocketHandler handler, StandardWebSocketSession wsSession) {
		Assert.notNull(handler, "WebSocketHandler must not be null");
		Assert.notNull(wsSession, "WebSocketSession must not be null");
		this.handler = handler;
		this.wsSession = wsSession;
	}

	@Override
	public void onOpen(final javax.websocket.Session session, EndpointConfig config) {
		this.wsSession.initializeNativeSession(session);

		// The following inner classes need to remain since lambdas would not
		// retain their
		// declared generic types (which need to be seen by the underlying
		// WebSocket engine)

		if (this.handler.supportsPartialMessages()) {
			session.addMessageHandler(new MessageHandler.Partial<String>() {
				public void onMessage(String message, boolean isLast) {
					handleTextMessage(session, message, isLast);
				}
			});
			session.addMessageHandler(new MessageHandler.Partial<ByteBuffer>() {
				public void onMessage(ByteBuffer message, boolean isLast) {
					handleBinaryMessage(session, message, isLast);
				}
			});
		} else {
			session.addMessageHandler(new MessageHandler.Whole<String>() {
				public void onMessage(String message) {
					handleTextMessage(session, message, true);
				}
			});
			session.addMessageHandler(new MessageHandler.Whole<ByteBuffer>() {
				public void onMessage(ByteBuffer message) {
					handleBinaryMessage(session, message, true);
				}
			});
		}

		session.addMessageHandler(new MessageHandler.Whole<javax.websocket.PongMessage>() {
			public void onMessage(javax.websocket.PongMessage message) {
				handlePongMessage(session, message.getApplicationData());
			}
		});

		try {
			this.handler.afterConnectionEstablished(this.wsSession);
		} catch (Exception ex) {
			ExceptionWebSocketHandlerDecorator.tryCloseWithError(this.wsSession, ex, logger);
		}
	}

	private void handleTextMessage(javax.websocket.Session session, String payload, boolean isLast) {
		TextMessage textMessage = new TextFragmentMessage(payload, isLast);
		try {
			this.handler.handleMessage(this.wsSession, textMessage);
		} catch (Exception ex) {
			ExceptionWebSocketHandlerDecorator.tryCloseWithError(this.wsSession, ex, logger);
		}
	}

	private void handleBinaryMessage(javax.websocket.Session session, ByteBuffer payload, boolean isLast) {
		BinaryMessage binaryMessage = new BinaryFragmentMessage(payload, isLast);
		try {
			this.handler.handleMessage(this.wsSession, binaryMessage);
		} catch (Exception ex) {
			ExceptionWebSocketHandlerDecorator.tryCloseWithError(this.wsSession, ex, logger);
		}
	}

	private void handlePongMessage(javax.websocket.Session session, ByteBuffer payload) {
		PongMessage pongMessage = new PongMessage(payload);
		try {
			this.handler.handleMessage(this.wsSession, pongMessage);
		} catch (Exception ex) {
			ExceptionWebSocketHandlerDecorator.tryCloseWithError(this.wsSession, ex, logger);
		}
	}

	@Override
	public void onClose(javax.websocket.Session session, CloseReason reason) {
		CloseStatus closeStatus = new CloseStatus(reason.getCloseCode().getCode(), reason.getReasonPhrase());
		try {
			this.handler.afterConnectionClosed(this.wsSession, closeStatus);
		} catch (Exception ex) {
			if (logger.isWarnEnabled()) {
				logger.warn("Unhandled on-close exception for " + this.wsSession, ex);
			}
		}
	}

	@Override
	public void onError(javax.websocket.Session session, Throwable exception) {
		try {
			this.handler.handleTransportError(this.wsSession, exception);
		} catch (Exception ex) {
			ExceptionWebSocketHandlerDecorator.tryCloseWithError(this.wsSession, ex, logger);
		}
	}

}
