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

package scw.websocket.server.support;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import scw.core.Assert;
import scw.core.Constants;
import scw.core.utils.StringUtils;
import scw.http.HttpMethod;
import scw.http.HttpStatus;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.websocket.SubProtocolCapable;
import scw.websocket.WebSocketExtension;
import scw.websocket.WebSocketHandler;
import scw.websocket.WebSocketHttpHeaders;
import scw.websocket.handler.WebSocketHandlerDecorator;
import scw.websocket.server.HandshakeFailureException;
import scw.websocket.server.HandshakeHandler;
import scw.websocket.server.RequestUpgradeStrategy;

/**
 * A base class for {@link HandshakeHandler} implementations, independent from
 * the Servlet API.
 *
 * <p>
 * Performs initial validation of the WebSocket handshake request - possibly
 * rejecting it through the appropriate HTTP status code - while also allowing
 * its subclasses to override various parts of the negotiation process (e.g.
 * origin validation, sub-protocol negotiation, extensions negotiation, etc).
 *
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 */
public abstract class DefaultHandshakeHandler implements HandshakeHandler {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private final RequestUpgradeStrategy requestUpgradeStrategy;

	private final List<String> supportedProtocols = new ArrayList<String>();

	/**
	 * A constructor that accepts a runtime-specific
	 * {@link RequestUpgradeStrategy}.
	 * 
	 * @param requestUpgradeStrategy
	 *            the upgrade strategy to use
	 */
	protected DefaultHandshakeHandler(RequestUpgradeStrategy requestUpgradeStrategy) {
		Assert.notNull(requestUpgradeStrategy, "RequestUpgradeStrategy must not be null");
		this.requestUpgradeStrategy = requestUpgradeStrategy;
	}

	/**
	 * Return the {@link RequestUpgradeStrategy} for WebSocket requests.
	 */
	public RequestUpgradeStrategy getRequestUpgradeStrategy() {
		return this.requestUpgradeStrategy;
	}

	/**
	 * Use this property to configure the list of supported sub-protocols. The
	 * first configured sub-protocol that matches a client-requested
	 * sub-protocol is accepted. If there are no matches the response will not
	 * contain a {@literal Sec-WebSocket-Protocol} header.
	 * <p>
	 * Note that if the WebSocketHandler passed in at runtime is an instance of
	 * {@link SubProtocolCapable} then there is not need to explicitly configure
	 * this property. That is certainly the case with the built-in STOMP over
	 * WebSocket support. Therefore this property should be configured
	 * explicitly only if the WebSocketHandler does not implement
	 * {@code SubProtocolCapable}.
	 */
	public void setSupportedProtocols(String... protocols) {
		this.supportedProtocols.clear();
		for (String protocol : protocols) {
			this.supportedProtocols.add(protocol.toLowerCase());
		}
	}

	/**
	 * Return the list of supported sub-protocols.
	 */
	public String[] getSupportedProtocols() {
		return StringUtils.toStringArray(this.supportedProtocols);
	}

	public final boolean doHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws HandshakeFailureException {
		WebSocketHttpHeaders headers = new WebSocketHttpHeaders(request.getHeaders());
		if (logger.isTraceEnabled()) {
			logger.trace("Processing request " + request.getURI() + " with headers=" + headers);
		}
		try {
			if (HttpMethod.GET != request.getMethod()) {
				response.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
				response.getHeaders().setAllow(Collections.singleton(HttpMethod.GET));
				if (logger.isErrorEnabled()) {
					logger.error("Handshake failed due to unexpected HTTP method: " + request.getMethod());
				}
				return false;
			}
			if (!"WebSocket".equalsIgnoreCase(headers.getUpgrade())) {
				handleInvalidUpgradeHeader(request, response);
				return false;
			}
			if (!headers.getConnection().contains("Upgrade") && !headers.getConnection().contains("upgrade")) {
				handleInvalidConnectHeader(request, response);
				return false;
			}
			if (!isWebSocketVersionSupported(headers)) {
				handleWebSocketVersionNotSupported(request, response);
				return false;
			}
			if (!isValidOrigin(request)) {
				response.setStatusCode(HttpStatus.FORBIDDEN);
				return false;
			}
			String wsKey = headers.getSecWebSocketKey();
			if (wsKey == null) {
				if (logger.isErrorEnabled()) {
					logger.error("Missing \"Sec-WebSocket-Key\" header");
				}
				response.setStatusCode(HttpStatus.BAD_REQUEST);
				return false;
			}
		} catch (IOException ex) {
			throw new HandshakeFailureException(
					"Response update failed during upgrade to WebSocket: " + request.getURI(), ex);
		}

		String subProtocol = selectProtocol(headers.getSecWebSocketProtocol(), wsHandler);
		List<WebSocketExtension> requested = headers.getSecWebSocketExtensions();
		List<WebSocketExtension> supported = this.requestUpgradeStrategy.getSupportedExtensions(request);
		List<WebSocketExtension> extensions = filterRequestedExtensions(request, requested, supported);
		Principal user = determineUser(request, wsHandler, attributes);

		if (logger.isTraceEnabled()) {
			logger.trace("Upgrading to WebSocket, subProtocol=" + subProtocol + ", extensions=" + extensions);
		}
		this.requestUpgradeStrategy.upgrade(request, response, subProtocol, extensions, user, wsHandler, attributes);
		return true;
	}

	protected void handleInvalidUpgradeHeader(ServerHttpRequest request, ServerHttpResponse response)
			throws IOException {
		if (logger.isErrorEnabled()) {
			logger.error("Handshake failed due to invalid Upgrade header: " + request.getHeaders().getUpgrade());
		}
		response.setStatusCode(HttpStatus.BAD_REQUEST);
		response.getBody().write("Can \"Upgrade\" only to \"WebSocket\".".getBytes(Constants.UTF_8));
	}

	protected void handleInvalidConnectHeader(ServerHttpRequest request, ServerHttpResponse response)
			throws IOException {
		if (logger.isErrorEnabled()) {
			logger.error("Handshake failed due to invalid Connection header " + request.getHeaders().getConnection());
		}
		response.setStatusCode(HttpStatus.BAD_REQUEST);
		response.getBody().write("\"Connection\" must be \"upgrade\".".getBytes(Constants.UTF_8));
	}

	protected boolean isWebSocketVersionSupported(WebSocketHttpHeaders httpHeaders) {
		String version = httpHeaders.getSecWebSocketVersion();
		String[] supportedVersions = getSupportedVersions();
		for (String supportedVersion : supportedVersions) {
			if (supportedVersion.trim().equals(version)) {
				return true;
			}
		}
		return false;
	}

	protected String[] getSupportedVersions() {
		return this.requestUpgradeStrategy.getSupportedVersions();
	}

	protected void handleWebSocketVersionNotSupported(ServerHttpRequest request, ServerHttpResponse response) {
		if (logger.isErrorEnabled()) {
			String version = request.getHeaders().getFirst("Sec-WebSocket-Version");
			logger.error("Handshake failed due to unsupported WebSocket version: " + version + ". Supported versions: "
					+ Arrays.toString(getSupportedVersions()));
		}
		response.setStatusCode(HttpStatus.UPGRADE_REQUIRED);
		response.getHeaders().set(WebSocketHttpHeaders.SEC_WEBSOCKET_VERSION,
				StringUtils.arrayToCommaDelimitedString(getSupportedVersions()));
	}

	/**
	 * Return whether the request {@code Origin} header value is valid or not.
	 * By default, all origins as considered as valid. Consider using an
	 * {@link OriginHandshakeInterceptor} for filtering origins if needed.
	 */
	protected boolean isValidOrigin(ServerHttpRequest request) {
		return true;
	}

	/**
	 * Perform the sub-protocol negotiation based on requested and supported
	 * sub-protocols. For the list of supported sub-protocols, this method first
	 * checks if the target WebSocketHandler is a {@link SubProtocolCapable} and
	 * then also checks if any sub-protocols have been explicitly configured
	 * with {@link #setSupportedProtocols(String...)}.
	 * 
	 * @param requestedProtocols
	 *            the requested sub-protocols
	 * @param webSocketHandler
	 *            the WebSocketHandler that will be used
	 * @return the selected protocols or {@code null}
	 * @see #determineHandlerSupportedProtocols(WebSocketHandler)
	 */
	protected String selectProtocol(List<String> requestedProtocols, WebSocketHandler webSocketHandler) {
		if (requestedProtocols != null) {
			List<String> handlerProtocols = determineHandlerSupportedProtocols(webSocketHandler);
			for (String protocol : requestedProtocols) {
				if (handlerProtocols.contains(protocol.toLowerCase())) {
					return protocol;
				}
				if (this.supportedProtocols.contains(protocol.toLowerCase())) {
					return protocol;
				}
			}
		}
		return null;
	}

	/**
	 * Determine the sub-protocols supported by the given WebSocketHandler by
	 * checking whether it is an instance of {@link SubProtocolCapable}.
	 * 
	 * @param handler
	 *            the handler to check
	 * @return a list of supported protocols, or an empty list if none available
	 */
	protected final List<String> determineHandlerSupportedProtocols(WebSocketHandler handler) {
		WebSocketHandler handlerToCheck = WebSocketHandlerDecorator.unwrap(handler);
		List<String> subProtocols = null;
		if (handlerToCheck instanceof SubProtocolCapable) {
			subProtocols = ((SubProtocolCapable) handlerToCheck).getSubProtocols();
		}
		return (subProtocols != null ? subProtocols : Collections.<String>emptyList());
	}

	/**
	 * Filter the list of requested WebSocket extensions.
	 * <p>
	 * As of 4.1, the default implementation of this method filters the list to
	 * leave only extensions that are both requested and supported.
	 * 
	 * @param request
	 *            the current request
	 * @param requestedExtensions
	 *            the list of extensions requested by the client
	 * @param supportedExtensions
	 *            the list of extensions supported by the server
	 * @return the selected extensions or an empty list
	 */
	protected List<WebSocketExtension> filterRequestedExtensions(ServerHttpRequest request,
			List<WebSocketExtension> requestedExtensions, List<WebSocketExtension> supportedExtensions) {

		List<WebSocketExtension> result = new ArrayList<WebSocketExtension>(requestedExtensions.size());
		for (WebSocketExtension extension : requestedExtensions) {
			if (supportedExtensions.contains(extension)) {
				result.add(extension);
			}
		}
		return result;
	}

	/**
	 * A method that can be used to associate a user with the WebSocket session
	 * in the process of being established. The default implementation calls
	 * {@link ServerHttpRequest#getPrincipal()}
	 * <p>
	 * Subclasses can provide custom logic for associating a user with a
	 * session, for example for assigning a name to anonymous users (i.e. not
	 * fully authenticated).
	 * 
	 * @param request
	 *            the handshake request
	 * @param wsHandler
	 *            the WebSocket handler that will handle messages
	 * @param attributes
	 *            handshake attributes to pass to the WebSocket session
	 * @return the user for the WebSocket session, or {@code null} if not
	 *         available
	 */
	protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
			Map<String, Object> attributes) {

		return request.getPrincipal();
	}

}
