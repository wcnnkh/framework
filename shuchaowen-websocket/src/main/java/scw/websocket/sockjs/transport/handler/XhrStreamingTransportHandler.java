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

import scw.mvc.http.ServerHttpRequest;
import scw.net.http.MediaType;
import scw.websocket.WebSocketHandler;
import scw.websocket.sockjs.frame.DefaultSockJsFrameFormat;
import scw.websocket.sockjs.frame.SockJsFrameFormat;
import scw.websocket.sockjs.transport.SockJsServiceConfig;
import scw.websocket.sockjs.transport.SockJsSession;
import scw.websocket.sockjs.transport.TransportHandler;
import scw.websocket.sockjs.transport.TransportType;
import scw.websocket.sockjs.transport.session.StreamingSockJsSession;

/**
 * A {@link TransportHandler} that sends messages over an HTTP streaming request.
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class XhrStreamingTransportHandler extends AbstractHttpSendingTransportHandler {

	private static final byte[] PRELUDE = new byte[2049];

	static {
		for (int i = 0; i < 2048; i++) {
			PRELUDE[i] = 'h';
		}
		PRELUDE[2048] = '\n';
	}


	public TransportType getTransportType() {
		return TransportType.XHR_STREAMING;
	}

	protected MediaType getContentType() {
		return new MediaType("application", "javascript", UTF8_CHARSET);
	}

	public boolean checkSessionType(SockJsSession session) {
		return (session instanceof XhrStreamingSockJsSession);
	}

	public StreamingSockJsSession createSession(
			String sessionId, WebSocketHandler handler, Map<String, Object> attributes) {

		return new XhrStreamingSockJsSession(sessionId, getServiceConfig(), handler, attributes);
	}

	@Override
	protected SockJsFrameFormat getFrameFormat(ServerHttpRequest request) {
		return new DefaultSockJsFrameFormat("%s\n");
	}


	private class XhrStreamingSockJsSession extends StreamingSockJsSession {

		public XhrStreamingSockJsSession(String sessionId, SockJsServiceConfig config,
				WebSocketHandler wsHandler, Map<String, Object> attributes) {

			super(sessionId, config, wsHandler, attributes);
		}

		@Override
		protected byte[] getPrelude(ServerHttpRequest request) {
			return PRELUDE;
		}
	}

}
