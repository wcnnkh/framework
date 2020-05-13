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

package scw.websocket.sockjs.transport.handler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

import scw.core.utils.StringUtils;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.ServerHttpRequest;
import scw.net.http.MediaType;
import scw.net.uri.UriComponentsBuilder;
import scw.net.uri.UriUtils;
import scw.util.MultiValueMap;
import scw.websocket.WebSocketHandler;
import scw.websocket.sockjs.SockJsException;
import scw.websocket.sockjs.frame.SockJsFrame;
import scw.websocket.sockjs.frame.SockJsFrameFormat;
import scw.websocket.sockjs.transport.SockJsSession;
import scw.websocket.sockjs.transport.SockJsSessionFactory;
import scw.websocket.sockjs.transport.session.AbstractHttpSockJsSession;

/**
 * Base class for HTTP transport handlers that push messages to connected clients.
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public abstract class AbstractHttpSendingTransportHandler extends AbstractTransportHandler
		implements SockJsSessionFactory {

	/**
	 * Pattern for validating jsonp callback parameter values.
	 */
	private static final Pattern CALLBACK_PARAM_PATTERN = Pattern.compile("[0-9A-Za-z_\\.]*");


	public final void handleRequest(HttpChannel httpChannel,
			WebSocketHandler wsHandler, SockJsSession wsSession) throws SockJsException {

		AbstractHttpSockJsSession sockJsSession = (AbstractHttpSockJsSession) wsSession;

		String protocol = null;  // https://github.com/sockjs/sockjs-client/issues/130
		sockJsSession.setAcceptedProtocol(protocol);

		// Set content type before writing
		httpChannel.getResponse().getHeaders().setContentType(getContentType());

		handleRequestInternal(httpChannel, sockJsSession);
	}

	protected void handleRequestInternal(HttpChannel httpChannel,
			AbstractHttpSockJsSession sockJsSession) throws SockJsException {

		if (sockJsSession.isNew()) {
			if (logger.isDebugEnabled()) {
				logger.debug(httpChannel.getRequest().getMethod() + " " + httpChannel.getRequest().getURI());
			}
			sockJsSession.handleInitialRequest(httpChannel, getFrameFormat(httpChannel.getRequest()));
		}
		else if (sockJsSession.isClosed()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Connection already closed (but not removed yet) for " + sockJsSession);
			}
			SockJsFrame frame = SockJsFrame.closeFrameGoAway();
			try {
				httpChannel.getResponse().getBody().write(frame.getContentBytes());
			}
			catch (IOException ex) {
				throw new SockJsException("Failed to send " + frame, sockJsSession.getId(), ex);
			}
		}
		else if (!sockJsSession.isActive()) {
			if (logger.isTraceEnabled()) {
				logger.trace("Starting " + getTransportType() + " async request.");
			}
			sockJsSession.handleSuccessiveRequest(httpChannel, getFrameFormat(httpChannel.getRequest()));
		}
		else {
			if (logger.isDebugEnabled()) {
				logger.debug("Another " + getTransportType() + " connection still open for " + sockJsSession);
			}
			String formattedFrame = getFrameFormat(httpChannel.getRequest()).format(SockJsFrame.closeFrameAnotherConnectionOpen());
			try {
				httpChannel.getResponse().getBody().write(formattedFrame.getBytes(SockJsFrame.CHARSET));
			}
			catch (IOException ex) {
				throw new SockJsException("Failed to send " + formattedFrame, sockJsSession.getId(), ex);
			}
		}
	}


	protected abstract MediaType getContentType();

	protected abstract SockJsFrameFormat getFrameFormat(ServerHttpRequest request);


	protected final String getCallbackParam(ServerHttpRequest request) {
		String query = request.getURI().getQuery();
		MultiValueMap<String, String> params = UriComponentsBuilder.newInstance().query(query).build().getQueryParams();
		String value = params.getFirst("c");
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		try {
			String result = UriUtils.decode(value, "UTF-8");
			return (CALLBACK_PARAM_PATTERN.matcher(result).matches() ? result : null);
		}
		catch (UnsupportedEncodingException ex) {
			// should never happen
			throw new SockJsException("Unable to decode callback query parameter", null, ex);
		}
	}

}
