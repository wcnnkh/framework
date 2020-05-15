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

package scw.websocket.sockjs.support;

import java.io.IOException;

import scw.core.Assert;
import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.ServerHttpResponse;
import scw.websocket.WebSocketHandler;
import scw.websocket.handler.ExceptionWebSocketHandlerDecorator;
import scw.websocket.handler.LoggingWebSocketHandlerDecorator;
import scw.websocket.sockjs.SockJsException;
import scw.websocket.sockjs.SockJsService;

/**
 * An {@link HttpRequestHandler} that allows mapping a {@link SockJsService} to requests
 * in a Servlet container.
 *
 * @author Rossen Stoyanchev
 * @author Sebastien Deleuze
 * @since 4.0
 */
public class SockJsHttpRequestHandler {
	private static final String PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE = SockJsHttpRequestHandler.class.getName() + ".pathWithinHandlerMapping";

	// No logging: HTTP transports too verbose and we don't know enough to log anything of value

	private final SockJsService sockJsService;

	private final WebSocketHandler webSocketHandler;


	/**
	 * Create a new SockJsHttpRequestHandler.
	 * @param sockJsService the SockJS service
	 * @param webSocketHandler the websocket handler
	 */
	public SockJsHttpRequestHandler(SockJsService sockJsService, WebSocketHandler webSocketHandler) {
		Assert.notNull(sockJsService, "SockJsService must not be null");
		Assert.notNull(webSocketHandler, "WebSocketHandler must not be null");
		this.sockJsService = sockJsService;
		this.webSocketHandler =
				new ExceptionWebSocketHandlerDecorator(new LoggingWebSocketHandlerDecorator(webSocketHandler));
	}


	/**
	 * Return the {@link SockJsService}.
	 */
	public SockJsService getSockJsService() {
		return this.sockJsService;
	}

	/**
	 * Return the {@link WebSocketHandler}.
	 */
	public WebSocketHandler getWebSocketHandler() {
		return this.webSocketHandler;
	}


	public void handleRequest(ServerHttpRequest request, ServerHttpResponse response)
			throws IOException {
		try {
			this.sockJsService.handleRequest(request, response, getSockJsPath(request, response), this.webSocketHandler);
		}
		catch (Throwable ex) {
			throw new SockJsException("Uncaught failure in SockJS request, uri=" + request.getURI(), ex);
		}
	}

	private String getSockJsPath(ServerHttpRequest request, ServerHttpResponse response) {
		String path = (String) request.getAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		return (path.length() > 0 && path.charAt(0) != '/' ? "/" + path : path);
	}
}
