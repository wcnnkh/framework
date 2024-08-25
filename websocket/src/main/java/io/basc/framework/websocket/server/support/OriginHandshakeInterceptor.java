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

package io.basc.framework.websocket.server.support;

import io.basc.framework.http.HttpStatus;
import io.basc.framework.http.HttpUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LoggerFactory;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.websocket.WebSocketHandler;
import io.basc.framework.websocket.server.HandshakeInterceptor;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * An interceptor to check request {@code Origin} header value against a
 * collection of allowed origins.
 *
 * @author Sebastien Deleuze
 */
public class OriginHandshakeInterceptor implements HandshakeInterceptor {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private final Set<String> allowedOrigins = new LinkedHashSet<String>();

	/**
	 * Default constructor with only same origin requests allowed.
	 */
	public OriginHandshakeInterceptor() {
	}

	public OriginHandshakeInterceptor(Collection<String> allowedOrigins) {
		setAllowedOrigins(allowedOrigins);
	}

	public void setAllowedOrigins(Collection<String> allowedOrigins) {
		Assert.notNull(allowedOrigins, "Allowed origins Collection must not be null");
		this.allowedOrigins.clear();
		this.allowedOrigins.addAll(allowedOrigins);
	}

	public Collection<String> getAllowedOrigins() {
		return Collections.unmodifiableSet(this.allowedOrigins);
	}

	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		if (!HttpUtils.isSameOrigin(request) && !HttpUtils.isValidOrigin(request, this.allowedOrigins)) {
			response.setStatusCode(HttpStatus.FORBIDDEN);
			if (logger.isDebugEnabled()) {
				logger.debug("Handshake request rejected, Origin header value " + request.getHeaders().getOrigin()
						+ " not allowed");
			}
			return false;
		}
		return true;
	}

	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
	}

}
