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

import java.io.IOException;
import java.util.Map;

import scw.core.utils.StringUtils;
import scw.mvc.Channel;
import scw.net.http.HttpStatus;
import scw.net.http.JavaScriptUtils;
import scw.net.http.MediaType;
import scw.net.http.server.ServerHttpRequest;
import scw.websocket.CloseStatus;
import scw.websocket.WebSocketHandler;
import scw.websocket.sockjs.SockJsException;
import scw.websocket.sockjs.SockJsTransportFailureException;
import scw.websocket.sockjs.frame.DefaultSockJsFrameFormat;
import scw.websocket.sockjs.frame.SockJsFrameFormat;
import scw.websocket.sockjs.transport.SockJsServiceConfig;
import scw.websocket.sockjs.transport.SockJsSession;
import scw.websocket.sockjs.transport.TransportHandler;
import scw.websocket.sockjs.transport.TransportType;
import scw.websocket.sockjs.transport.session.AbstractHttpSockJsSession;
import scw.websocket.sockjs.transport.session.StreamingSockJsSession;

/**
 * An HTTP {@link TransportHandler} that uses a famous browser
 * {@code document.domain technique}. See <a href=
 * "https://stackoverflow.com/questions/1481251/what-does-document-domain-document-domain-do">
 * stackoverflow.com/questions/1481251/what-does-document-domain-document-domain-do</a>
 * for details.
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class HtmlFileTransportHandler extends AbstractHttpSendingTransportHandler {

	private static final String PARTIAL_HTML_CONTENT;

	// Safari needs at least 1024 bytes to parse the website.
	// https://code.google.com/p/browsersec/wiki/Part2#Survey_of_content_sniffing_behaviors
	private static final int MINIMUM_PARTIAL_HTML_CONTENT_LENGTH = 1024;


	static {
		StringBuilder sb = new StringBuilder(
				"<!doctype html>\n" +
				"<html><head>\n" +
				"  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n" +
				"  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
				"</head><body><h2>Don't panic!</h2>\n" +
				"  <script>\n" +
				"    document.domain = document.domain;\n" +
				"    var c = parent.%s;\n" +
				"    c.start();\n" +
				"    function p(d) {c.message(d);};\n" +
				"    window.onload = function() {c.stop();};\n" +
				"  </script>"
				);

		while (sb.length() < MINIMUM_PARTIAL_HTML_CONTENT_LENGTH) {
			sb.append(" ");
		}
		PARTIAL_HTML_CONTENT = sb.toString();
	}


	public TransportType getTransportType() {
		return TransportType.HTML_FILE;
	}

	@Override
	protected MediaType getContentType() {
		return new MediaType("text", "html", UTF8_CHARSET);
	}

	public boolean checkSessionType(SockJsSession session) {
		return (session instanceof HtmlFileStreamingSockJsSession);
	}

	public StreamingSockJsSession createSession(
			String sessionId, WebSocketHandler handler, Map<String, Object> attributes) {

		return new HtmlFileStreamingSockJsSession(sessionId, getServiceConfig(), handler, attributes);
	}

	@Override
	public void handleRequestInternal(Channel channel,
			AbstractHttpSockJsSession sockJsSession) throws SockJsException {

		String callback = getCallbackParam(channel.getRequest());
		if (!StringUtils.hasText(callback)) {
			channel.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
			try {
				channel.getResponse().getBody().write("\"callback\" parameter required".getBytes(UTF8_CHARSET));
			}
			catch (IOException ex) {
				sockJsSession.tryCloseWithSockJsTransportError(ex, CloseStatus.SERVER_ERROR);
				throw new SockJsTransportFailureException("Failed to write to response", sockJsSession.getId(), ex);
			}
			return;
		}

		super.handleRequestInternal(channel, sockJsSession);
	}

	@Override
	protected SockJsFrameFormat getFrameFormat(ServerHttpRequest request) {
		return new DefaultSockJsFrameFormat("<script>\np(\"%s\");\n</script>\r\n") {
			@Override
			protected String preProcessContent(String content) {
				return JavaScriptUtils.javaScriptEscape(content);
			}
		};
	}


	private class HtmlFileStreamingSockJsSession extends StreamingSockJsSession {

		public HtmlFileStreamingSockJsSession(String sessionId, SockJsServiceConfig config,
				WebSocketHandler wsHandler, Map<String, Object> attributes) {

			super(sessionId, config, wsHandler, attributes);
		}

		@Override
		protected byte[] getPrelude(ServerHttpRequest request) {
			// We already validated the parameter above...
			String callback = getCallbackParam(request);
			String html = String.format(PARTIAL_HTML_CONTENT, callback);
			return html.getBytes(UTF8_CHARSET);
		}
	}

}
