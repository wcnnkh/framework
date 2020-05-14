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

import scw.net.http.MediaType;
import scw.net.http.server.ServerHttpRequest;
import scw.websocket.WebSocketHandler;
import scw.websocket.sockjs.frame.DefaultSockJsFrameFormat;
import scw.websocket.sockjs.frame.SockJsFrameFormat;
import scw.websocket.sockjs.transport.SockJsSession;
import scw.websocket.sockjs.transport.TransportHandler;
import scw.websocket.sockjs.transport.TransportType;
import scw.websocket.sockjs.transport.session.PollingSockJsSession;

/**
 * A {@link TransportHandler} based on XHR (long) polling.
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class XhrPollingTransportHandler extends AbstractHttpSendingTransportHandler {

	public TransportType getTransportType() {
		return TransportType.XHR;
	}

	protected MediaType getContentType() {
		return new MediaType("application", "javascript", UTF8_CHARSET);
	}

	@Override
	protected SockJsFrameFormat getFrameFormat(ServerHttpRequest request) {
		return new DefaultSockJsFrameFormat("%s\n");
	}

	public boolean checkSessionType(SockJsSession session) {
		return (session instanceof PollingSockJsSession);
	}

	public PollingSockJsSession createSession(
			String sessionId, WebSocketHandler handler, Map<String, Object> attributes) {

		return new PollingSockJsSession(sessionId, getServiceConfig(), handler, attributes);
	}

}
