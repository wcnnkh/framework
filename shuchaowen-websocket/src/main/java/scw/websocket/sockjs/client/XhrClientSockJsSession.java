/*
 * Copyright 2002-2017 the original author or authors.
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

package scw.websocket.sockjs.client;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import scw.core.Assert;
import scw.messageing.TextMessage;
import scw.net.http.HttpHeaders;
import scw.net.http.MediaType;
import scw.util.concurrent.SettableListenableFuture;
import scw.websocket.CloseStatus;
import scw.websocket.WebSocketExtension;
import scw.websocket.WebSocketHandler;
import scw.websocket.WebSocketSession;
import scw.websocket.sockjs.transport.TransportType;

/**
 * An extension of {@link AbstractClientSockJsSession} for use with HTTP
 * transports simulating a WebSocket session.
 *
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public class XhrClientSockJsSession extends AbstractClientSockJsSession {

	private final XhrTransport transport;

	private HttpHeaders headers;

	private HttpHeaders sendHeaders;

	private final URI sendUrl;

	private int textMessageSizeLimit = -1;

	private int binaryMessageSizeLimit = -1;


	public XhrClientSockJsSession(TransportRequest request, WebSocketHandler handler,
			XhrTransport transport, SettableListenableFuture<WebSocketSession> connectFuture) {

		super(request, handler, connectFuture);
		Assert.notNull(transport, "'transport' is required");
		this.transport = transport;
		this.headers = request.getHttpRequestHeaders();
		this.sendHeaders = new HttpHeaders();
		if (this.headers != null) {
			this.sendHeaders.putAll(this.headers);
		}
		this.sendHeaders.setContentType(MediaType.APPLICATION_JSON);
		this.sendUrl = request.getSockJsUrlInfo().getTransportUrl(TransportType.XHR_SEND);
	}


	public HttpHeaders getHeaders() {
		return this.headers;
	}

	public InetSocketAddress getLocalAddress() {
		return null;
	}

	public InetSocketAddress getRemoteAddress() {
		return new InetSocketAddress(getUri().getHost(), getUri().getPort());
	}

	public String getAcceptedProtocol() {
		return null;
	}

	public void setTextMessageSizeLimit(int messageSizeLimit) {
		this.textMessageSizeLimit = messageSizeLimit;
	}

	public int getTextMessageSizeLimit() {
		return this.textMessageSizeLimit;
	}

	public void setBinaryMessageSizeLimit(int messageSizeLimit) {
		this.binaryMessageSizeLimit = -1;
	}

	public int getBinaryMessageSizeLimit() {
		return this.binaryMessageSizeLimit;
	}

	public List<WebSocketExtension> getExtensions() {
		return Collections.emptyList();
	}

	protected void sendInternal(TextMessage message) {
		this.transport.executeSendRequest(this.sendUrl, this.sendHeaders, message);
	}

	protected void disconnect(CloseStatus status) {
		// Nothing to do: XHR transports check if session is disconnected.
	}

}