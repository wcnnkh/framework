/*
 * Copyright 2002-2016 the original author or authors.
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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import scw.core.Assert;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.net.http.HttpStatus;
import scw.net.http.HttpUtils;
import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.ServerHttpResponse;
import scw.websocket.WebSocketHandler;
import scw.websocket.server.HandshakeInterceptor;

/**
 * An interceptor to check request {@code Origin} header value against a
 * collection of allowed origins.
 *
 * @author Sebastien Deleuze
 * @since 4.1.2
 */
public class OriginHandshakeInterceptor implements HandshakeInterceptor {

	protected final Logger logger = LoggerUtils.getLogger(getClass());

	private final Set<String> allowedOrigins = new LinkedHashSet<String>();


	/**
	 * Default constructor with only same origin requests allowed.
	 */
	public OriginHandshakeInterceptor() {
	}

	/**
	 * Constructor using the specified allowed origin values.
	 * @see #setAllowedOrigins(Collection)
	 */
	public OriginHandshakeInterceptor(Collection<String> allowedOrigins) {
		setAllowedOrigins(allowedOrigins);
	}


	/**
	 * Configure allowed {@code Origin} header values. This check is mostly
	 * designed for browsers. There is nothing preventing other types of client
	 * to modify the {@code Origin} header value.
	 * <p>Each provided allowed origin must have a scheme, and optionally a port
	 * (e.g. "https://example.org", "https://example.org:9090"). An allowed origin
	 * string may also be "*" in which case all origins are allowed.
	 * @see <a href="https://tools.ietf.org/html/rfc6454">RFC 6454: The Web Origin Concept</a>
	 */
	public void setAllowedOrigins(Collection<String> allowedOrigins) {
		Assert.notNull(allowedOrigins, "Allowed origins Collection must not be null");
		this.allowedOrigins.clear();
		this.allowedOrigins.addAll(allowedOrigins);
	}

	/**
	 * @since 4.1.5
	 * @see #setAllowedOrigins
	 */
	public Collection<String> getAllowedOrigins() {
		return Collections.unmodifiableSet(this.allowedOrigins);
	}


	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
			WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
		if (!HttpUtils.isSameOrigin(request) && !HttpUtils.isValidOrigin(request, this.allowedOrigins)) {
			response.setStatusCode(HttpStatus.FORBIDDEN);
			if (logger.isDebugEnabled()) {
				logger.debug("Handshake request rejected, Origin header value " +
						request.getHeaders().getOrigin() + " not allowed");
			}
			return false;
		}
		return true;
	}

	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
			WebSocketHandler wsHandler, Exception exception) {
	}

}
