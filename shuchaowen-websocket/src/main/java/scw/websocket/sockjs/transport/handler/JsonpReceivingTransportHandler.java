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

import java.io.IOException;

import scw.core.utils.StringUtils;
import scw.net.http.HttpStatus;
import scw.net.http.MediaType;
import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.ServerHttpResponse;
import scw.net.http.server.mvc.HttpChannel;
import scw.websocket.WebSocketHandler;
import scw.websocket.sockjs.SockJsException;
import scw.websocket.sockjs.frame.SockJsMessageCodec;
import scw.websocket.sockjs.transport.TransportHandler;
import scw.websocket.sockjs.transport.TransportType;
import scw.websocket.sockjs.transport.session.AbstractHttpSockJsSession;

/**
 * A {@link TransportHandler} that receives messages over HTTP.
 *
 * @author Rossen Stoyanchev
 * @deprecated Will be removed as of Spring Framework 5.1, use others transports instead.
 */
@Deprecated
public class JsonpReceivingTransportHandler extends AbstractHttpReceivingTransportHandler {

	public TransportType getTransportType() {
		return TransportType.JSONP_SEND;
	}

	@Override
	public void handleRequestInternal(ServerHttpRequest request, ServerHttpResponse response,
			WebSocketHandler wsHandler, AbstractHttpSockJsSession sockJsSession) throws SockJsException {

		super.handleRequestInternal(request, response, wsHandler, sockJsSession);
		try {
			response.getBody().write("ok".getBytes(UTF8_CHARSET));
		}
		catch (IOException ex) {
			throw new SockJsException("Failed to write to the response body", sockJsSession.getId(), ex);
		}
	}

	@Override
	protected String[] readMessages(ServerHttpRequest request) throws IOException {
		SockJsMessageCodec messageCodec = getServiceConfig().getMessageCodec();
		MediaType contentType = request.getHeaders().getContentType();
		if (contentType != null && MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(contentType)) {
			String d = request.getParameter("d");
			return (StringUtils.hasText(d) ? messageCodec.decode(d) : null);
		}
		else {
			return messageCodec.decodeInputStream(request.getBody());
		}
	}

	@Override
	protected HttpStatus getResponseStatus() {
		return HttpStatus.OK;
	}

}
