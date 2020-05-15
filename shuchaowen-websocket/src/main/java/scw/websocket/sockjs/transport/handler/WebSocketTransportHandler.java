/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package scw.websocket.sockjs.transport.handler;

import java.util.Map;

import scw.core.Assert;
import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.ServerHttpResponse;
import scw.websocket.CloseStatus;
import scw.websocket.WebSocketHandler;
import scw.websocket.server.HandshakeFailureException;
import scw.websocket.server.HandshakeHandler;
import scw.websocket.sockjs.SockJsException;
import scw.websocket.sockjs.SockJsTransportFailureException;
import scw.websocket.sockjs.transport.SockJsSession;
import scw.websocket.sockjs.transport.SockJsSessionFactory;
import scw.websocket.sockjs.transport.TransportHandler;
import scw.websocket.sockjs.transport.TransportType;
import scw.websocket.sockjs.transport.session.AbstractSockJsSession;
import scw.websocket.sockjs.transport.session.WebSocketServerSockJsSession;

/**
 * WebSocket-based {@link TransportHandler}. Uses {@link SockJsWebSocketHandler} and
 * {@link WebSocketServerSockJsSession} to add SockJS processing.
 *
 * <p>Also implements {@link HandshakeHandler} to support raw WebSocket communication at
 * SockJS URL "/websocket".
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class WebSocketTransportHandler extends AbstractTransportHandler
		implements SockJsSessionFactory, HandshakeHandler {

	private final HandshakeHandler handshakeHandler;


	public WebSocketTransportHandler(HandshakeHandler handshakeHandler) {
		Assert.notNull(handshakeHandler, "HandshakeHandler must not be null");
		this.handshakeHandler = handshakeHandler;
	}


	public TransportType getTransportType() {
		return TransportType.WEBSOCKET;
	}

	public HandshakeHandler getHandshakeHandler() {
		return this.handshakeHandler;
	}

	public boolean checkSessionType(SockJsSession session) {
		return (session instanceof WebSocketServerSockJsSession);
	}

	public AbstractSockJsSession createSession(String id, WebSocketHandler handler, Map<String, Object> attrs) {
		return new WebSocketServerSockJsSession(id, getServiceConfig(), handler, attrs);
	}

	public void handleRequest(ServerHttpRequest request, ServerHttpResponse response,
			WebSocketHandler wsHandler, SockJsSession wsSession) throws SockJsException {
		WebSocketServerSockJsSession sockJsSession = (WebSocketServerSockJsSession) wsSession;
		try {
			wsHandler = new SockJsWebSocketHandler(getServiceConfig(), wsHandler, sockJsSession);
			this.handshakeHandler.doHandshake(request, response, wsHandler, sockJsSession.getAttributes());
		}
		catch (Throwable ex) {
			sockJsSession.tryCloseWithSockJsTransportError(ex, CloseStatus.SERVER_ERROR);
			throw new SockJsTransportFailureException("WebSocket handshake failure", wsSession.getId(), ex);
		}
	}
	
	public boolean doHandshake(ServerHttpRequest request, ServerHttpResponse response,
			WebSocketHandler handler, Map<String, Object> attributes) throws HandshakeFailureException {
		return this.handshakeHandler.doHandshake(request, response, handler, attributes);
	}
}
