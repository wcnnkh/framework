/*
 * Copyright 2002-2018 the original author or authors.
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

package io.basc.framework.websocket.server.support;

import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.websocket.WebSocketHandler;
import io.basc.framework.websocket.handler.ExceptionWebSocketHandlerDecorator;
import io.basc.framework.websocket.handler.LoggingWebSocketHandlerDecorator;
import io.basc.framework.websocket.server.HandshakeFailureException;
import io.basc.framework.websocket.server.HandshakeHandler;
import io.basc.framework.websocket.server.HandshakeInterceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebSocketHttpRequestHandler {

	private final Logger logger = LoggerFactory.getLogger(WebSocketHttpRequestHandler.class);

	private final WebSocketHandler wsHandler;

	private final HandshakeHandler handshakeHandler;

	private final List<HandshakeInterceptor> interceptors = new ArrayList<HandshakeInterceptor>();

	public WebSocketHttpRequestHandler(WebSocketHandler wsHandler, HandshakeHandler handshakeHandler) {
		Assert.notNull(wsHandler, "wsHandler must not be null");
		Assert.notNull(handshakeHandler, "handshakeHandler must not be null");
		this.wsHandler = new ExceptionWebSocketHandlerDecorator(new LoggingWebSocketHandlerDecorator(wsHandler));
		this.handshakeHandler = handshakeHandler;
	}

	public WebSocketHandler getWebSocketHandler() {
		return this.wsHandler;
	}

	public HandshakeHandler getHandshakeHandler() {
		return this.handshakeHandler;
	}

	public void setHandshakeInterceptors(List<HandshakeInterceptor> interceptors) {
		this.interceptors.clear();
		if (interceptors != null) {
			this.interceptors.addAll(interceptors);
		}
	}

	public List<HandshakeInterceptor> getHandshakeInterceptors() {
		return this.interceptors;
	}

	public void handleRequest(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		HandshakeInterceptorChain chain = new HandshakeInterceptorChain(this.interceptors, this.wsHandler);
		HandshakeFailureException failure = null;

		try {
			if (logger.isDebugEnabled()) {
				logger.debug(request.getMethod() + " " + request.getURI());
			}
			Map<String, Object> attributes = new HashMap<String, Object>();
			if (!chain.applyBeforeHandshake(request, response, attributes)) {
				return;
			}
			this.handshakeHandler.doHandshake(request, response, this.wsHandler, attributes);
			chain.applyAfterHandshake(request, response, null);
		} catch (HandshakeFailureException ex) {
			failure = ex;
		} catch (Throwable ex) {
			failure = new HandshakeFailureException("Uncaught failure for request " + request.getURI(), ex);
		} finally {
			if (failure != null) {
				chain.applyAfterHandshake(request, response, failure);
				throw failure;
			}
		}
	}

}
