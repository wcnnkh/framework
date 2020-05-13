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

package scw.websocket.sockjs.transport.handler;

import java.util.Map;

import scw.core.utils.StringUtils;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.ServerHttpRequest;
import scw.net.http.HttpStatus;
import scw.net.http.JavaScriptUtils;
import scw.net.http.MediaType;
import scw.websocket.CloseStatus;
import scw.websocket.WebSocketHandler;
import scw.websocket.sockjs.SockJsException;
import scw.websocket.sockjs.SockJsTransportFailureException;
import scw.websocket.sockjs.frame.DefaultSockJsFrameFormat;
import scw.websocket.sockjs.frame.SockJsFrameFormat;
import scw.websocket.sockjs.transport.SockJsSession;
import scw.websocket.sockjs.transport.TransportType;
import scw.websocket.sockjs.transport.session.AbstractHttpSockJsSession;
import scw.websocket.sockjs.transport.session.PollingSockJsSession;

/**
 * A TransportHandler that sends messages via JSONP polling.
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 * @deprecated Will be removed as of Spring Framework 5.1, use others transports instead.
 */
@Deprecated
public class JsonpPollingTransportHandler extends AbstractHttpSendingTransportHandler {

	public TransportType getTransportType() {
		return TransportType.JSONP;
	}

	@Override
	protected MediaType getContentType() {
		return new MediaType("application", "javascript", UTF8_CHARSET);
	}

	public boolean checkSessionType(SockJsSession session) {
		return session instanceof PollingSockJsSession;
	}

	public PollingSockJsSession createSession(
			String sessionId, WebSocketHandler handler, Map<String, Object> attributes) {

		return new PollingSockJsSession(sessionId, getServiceConfig(), handler, attributes);
	}

	public void handleRequestInternal(HttpChannel httpChannel,
			AbstractHttpSockJsSession sockJsSession) throws SockJsException {

		try {
			String callback = getCallbackParam(httpChannel.getRequest());
			if (!StringUtils.hasText(callback)) {
				httpChannel.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
				httpChannel.getResponse().getBody().write("\"callback\" parameter required".getBytes(UTF8_CHARSET));
				return;
			}
		}
		catch (Throwable ex) {
			sockJsSession.tryCloseWithSockJsTransportError(ex, CloseStatus.SERVER_ERROR);
			throw new SockJsTransportFailureException("Failed to send error", sockJsSession.getId(), ex);
		}

		super.handleRequestInternal(httpChannel, sockJsSession);
	}

	@Override
	protected SockJsFrameFormat getFrameFormat(ServerHttpRequest request) {
		// We already validated the parameter above...
		String callback = getCallbackParam(request);

		return new DefaultSockJsFrameFormat("/**/" + callback + "(\"%s\");\r\n") {
			@Override
			protected String preProcessContent(String content) {
				return JavaScriptUtils.javaScriptEscape(content);
			}
		};
	}

}
