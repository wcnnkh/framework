/*
 * Copyright 2002-2014 the original author or authors.
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

package io.basc.framework.websocket.server;

import io.basc.framework.http.server.ServerHttpRequest;
import io.basc.framework.http.server.ServerHttpResponse;
import io.basc.framework.websocket.WebSocketHandler;
import io.basc.framework.websocket.handler.PerConnectionWebSocketHandler;

import java.util.Map;

/**
 * Contract for processing a WebSocket handshake request.
 *
 * @author Rossen Stoyanchev
 * @see HandshakeInterceptor
 * @see io.basc.framework.websocket.server.support.WebSocketHttpRequestHandler
 */
public interface HandshakeHandler {

	/**
	 * Initiate the handshake.
	 * @param request the current request
	 * @param response the current response
	 * @param wsHandler the handler to process WebSocket messages; see
	 * {@link PerConnectionWebSocketHandler} for providing a handler with
	 * per-connection lifecycle.
	 * @param attributes attributes from the HTTP handshake to associate with the WebSocket
	 * session; the provided attributes are copied, the original map is not used.
	 * @return whether the handshake negotiation was successful or not. In either case the
	 * response status, headers, and body will have been updated to reflect the
	 * result of the negotiation
	 * @throws HandshakeFailureException thrown when handshake processing failed to
	 * complete due to an internal, unrecoverable error, i.e. a server error as
	 * opposed to a failure to successfully negotiate the handshake.
	 */
	boolean doHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws HandshakeFailureException;

}
