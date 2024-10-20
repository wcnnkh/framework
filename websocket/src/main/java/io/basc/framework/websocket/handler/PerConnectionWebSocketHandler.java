/*
 * Copyright 2002-2015 the original author or authors.
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

package io.basc.framework.websocket.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.beans.factory.support.BeanFactoryAccessor;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;
import io.basc.framework.websocket.CloseStatus;
import io.basc.framework.websocket.WebSocketHandler;
import io.basc.framework.websocket.WebSocketSession;

/**
 * A {@link WebSocketHandler} that initializes and destroys a
 * {@link WebSocketHandler} instance for each WebSocket connection and delegates
 * all other methods to it.
 *
 * <p>
 * Essentially create an instance of this class once, providing the type of
 * {@link WebSocketHandler} class to create for each connection, and then pass
 * it to any API method that expects a {@link WebSocketHandler}.
 *
 * @author Rossen Stoyanchev
 */
public class PerConnectionWebSocketHandler extends BeanFactoryAccessor implements WebSocketHandler {

	private static final Logger logger = LogManager.getLogger(PerConnectionWebSocketHandler.class);
	private Class<? extends WebSocketHandler> handlerType;

	private final Map<WebSocketSession, WebSocketHandler> handlers = new ConcurrentHashMap<WebSocketSession, WebSocketHandler>();

	private final boolean supportsPartialMessages;

	public PerConnectionWebSocketHandler(Class<? extends WebSocketHandler> handlerType) {
		this(handlerType, false);
	}

	public PerConnectionWebSocketHandler(Class<? extends WebSocketHandler> handlerType,
			boolean supportsPartialMessages) {
		this.handlerType = handlerType;
		this.supportsPartialMessages = supportsPartialMessages;
	}

	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		WebSocketHandler handler = getBeanFactory().getBean(handlerType);
		this.handlers.put(session, handler);
		handler.afterConnectionEstablished(session);
	}

	public void handleMessage(WebSocketSession session, io.basc.framework.messageing.Message<?> message) throws Exception {
		getHandler(session).handleMessage(session, message);
	}

	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		getHandler(session).handleTransportError(session, exception);
	}

	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		try {
			getHandler(session).afterConnectionClosed(session, closeStatus);
		} finally {
			destroyHandler(session);
		}
	}

	public boolean supportsPartialMessages() {
		return this.supportsPartialMessages;
	}

	private WebSocketHandler getHandler(WebSocketSession session) {
		WebSocketHandler handler = this.handlers.get(session);
		if (handler == null) {
			throw new IllegalStateException("WebSocketHandler not found for " + session);
		}
		return handler;
	}

	private void destroyHandler(WebSocketSession session) {
		WebSocketHandler handler = this.handlers.remove(session);
		try {
			if (handler != null) {
				this.provider.destroy(handler);
			}
			if (!getBeanFactory().isSingleton(handlerType)) {
				if (handler != null) {
					BeanDefinition definition = getBeanFactory().getDefinition(handlerType);
					if(definition != null && !definition.isSingleton()){
						definition.destroy(handler);
					}
				}
			}
		} catch (Throwable ex) {
			if (logger.isWarnEnabled()) {
				logger.warn("Error while destroying " + handler, ex);
			}
		}
	}

	@Override
	public String toString() {
		return "PerConnectionWebSocketHandlerProxy[handlerType=" + handlerType + "]";
	}

}
