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

import io.basc.framework.security.session.Session;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.websocket.WebSocketHandler;
import io.basc.framework.websocket.WebSocketSession;
import io.basc.framework.websocket.server.HandshakeInterceptor;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

/**
 * An interceptor to copy information from the HTTP session to the "handshake
 * attributes" map to made available
 * via{@link WebSocketSession#getAttributes()}.
 *
 * <p>
 * Copies a subset or all HTTP session attributes and/or the HTTP session id
 * under the key {@link #HTTP_SESSION_ID_ATTR_NAME}.
 *
 * @author Rossen Stoyanchev
 */
public class HttpSessionHandshakeInterceptor implements HandshakeInterceptor {

	/**
	 * The name of the attribute under which the HTTP session id is exposed when
	 * {@link #setCopyHttpSessionId(boolean) copyHttpSessionId} is "true".
	 */
	public static final String HTTP_SESSION_ID_ATTR_NAME = "HTTP.SESSION.ID";

	private final Collection<String> attributeNames;

	private boolean copyAllAttributes;

	private boolean copyHttpSessionId = true;

	private boolean createSession;

	/**
	 * Default constructor for copying all HTTP session attributes and the HTTP
	 * session id.
	 * 
	 * @see #setCopyAllAttributes
	 * @see #setCopyHttpSessionId
	 */
	public HttpSessionHandshakeInterceptor() {
		this.attributeNames = Collections.emptyList();
		this.copyAllAttributes = true;
	}

	/**
	 * Constructor for copying specific HTTP session attributes and the HTTP session
	 * id.
	 * 
	 * @param attributeNames session attributes to copy
	 * @see #setCopyAllAttributes
	 * @see #setCopyHttpSessionId
	 */
	public HttpSessionHandshakeInterceptor(Collection<String> attributeNames) {
		this.attributeNames = Collections.unmodifiableCollection(attributeNames);
		this.copyAllAttributes = false;
	}

	public Collection<String> getAttributeNames() {
		return this.attributeNames;
	}

	/**
	 * Whether to copy all attributes from the HTTP session. If set to "true", any
	 * explicitly configured attribute names are ignored.
	 * <p>
	 * By default this is set to either "true" or "false" depending on which
	 * constructor was used (default or with attribute names respectively).
	 * 
	 * @param copyAllAttributes whether to copy all attributes
	 */
	public void setCopyAllAttributes(boolean copyAllAttributes) {
		this.copyAllAttributes = copyAllAttributes;
	}

	public boolean isCopyAllAttributes() {
		return this.copyAllAttributes;
	}

	/**
	 * Whether the HTTP session id should be copied to the handshake attributes
	 * under the key {@link #HTTP_SESSION_ID_ATTR_NAME}.
	 * <p>
	 * By default this is "true".
	 * 
	 * @param copyHttpSessionId whether to copy the HTTP session id.
	 */
	public void setCopyHttpSessionId(boolean copyHttpSessionId) {
		this.copyHttpSessionId = copyHttpSessionId;
	}

	public boolean isCopyHttpSessionId() {
		return this.copyHttpSessionId;
	}

	public void setCreateSession(boolean createSession) {
		this.createSession = createSession;
	}

	public boolean isCreateSession() {
		return this.createSession;
	}

	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		Session session = request.getSession(isCreateSession());
		if (session != null) {
			if (isCopyHttpSessionId()) {
				attributes.put(HTTP_SESSION_ID_ATTR_NAME, session.getId());
			}
			Enumeration<String> names = session.getAttributeNames();
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				if (isCopyAllAttributes() || getAttributeNames().contains(name)) {
					attributes.put(name, session.getAttribute(name));
				}
			}
		}
		return true;
	}

	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception ex) {
	}

}
