/*
 * Copyright 2002-2014 the original author or authors.
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

import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.websocket.WebSocketHandler;
import io.basc.framework.websocket.server.HandshakeInterceptor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A helper class that assists with invoking a list of handshake interceptors.
 *
 * @author Rossen Stoyanchev
 */
public class HandshakeInterceptorChain {

	private static final Logger logger = LoggerFactory.getLogger(HandshakeInterceptorChain.class);

	private final List<HandshakeInterceptor> interceptors;

	private final WebSocketHandler wsHandler;

	private int interceptorIndex = -1;


	public HandshakeInterceptorChain(List<HandshakeInterceptor> interceptors, WebSocketHandler wsHandler) {
		this.interceptors = (interceptors != null ? interceptors : Collections.<HandshakeInterceptor>emptyList());
		this.wsHandler = wsHandler;
	}


	public boolean applyBeforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
			Map<String, Object> attributes) throws Exception {

		for (int i = 0; i < this.interceptors.size(); i++) {
			HandshakeInterceptor interceptor = this.interceptors.get(i);
			if (!interceptor.beforeHandshake(request, response, this.wsHandler, attributes)) {
				if (logger.isDebugEnabled()) {
					logger.debug(interceptor + " returns false from beforeHandshake - precluding handshake");
				}
				applyAfterHandshake(request, response, null);
				return false;
			}
			this.interceptorIndex = i;
		}
		return true;
	}


	public void applyAfterHandshake(ServerHttpRequest request, ServerHttpResponse response, Exception failure) {
		for (int i = this.interceptorIndex; i >= 0; i--) {
			HandshakeInterceptor interceptor = this.interceptors.get(i);
			try {
				interceptor.afterHandshake(request, response, this.wsHandler, failure);
			}
			catch (Throwable ex) {
				if (logger.isWarnEnabled()) {
					logger.warn(interceptor + " threw exception in afterHandshake: " + ex);
				}
			}
		}
	}

}
