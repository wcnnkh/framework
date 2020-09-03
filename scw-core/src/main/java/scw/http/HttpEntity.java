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

package scw.http;

import java.io.Serializable;

import scw.core.utils.ObjectUtils;
import scw.net.message.Entity;
import scw.util.MultiValueMap;

public class HttpEntity<T> extends AbstractHttpMessage implements Entity<T>, HttpMessage, Serializable{
	private static final long serialVersionUID = 1L;

	/**
	 * The empty {@code HttpEntity}, with no body or headers.
	 */
	public static final HttpEntity<?> EMPTY = new HttpEntity<Object>();


	private final HttpHeaders headers;

	private final T body;


	/**
	 * Create a new, empty {@code HttpEntity}.
	 */
	protected HttpEntity() {
		this(null, null);
	}

	/**
	 * Create a new {@code HttpEntity} with the given body and no headers.
	 * @param body the entity body
	 */
	public HttpEntity(T body) {
		this(body, null);
	}

	/**
	 * Create a new {@code HttpEntity} with the given headers and no body.
	 * @param headers the entity headers
	 */
	public HttpEntity(MultiValueMap<String, String> headers) {
		this(null, headers);
	}

	/**
	 * Create a new {@code HttpEntity} with the given body and headers.
	 * @param body the entity body
	 * @param headers the entity headers
	 */
	public HttpEntity(T body, MultiValueMap<String, String> headers) {
		this.body = body;
		HttpHeaders tempHeaders = new HttpHeaders();
		if (headers != null) {
			tempHeaders.putAll(headers);
		}
		this.headers = tempHeaders;
	}


	/**
	 * Returns the headers of this entity.
	 */
	public HttpHeaders getHeaders() {
		return this.headers;
	}

	/**
	 * Returns the body of this entity.
	 */
	public T getBody() {
		return this.body;
	}

	/**
	 * Indicates whether this entity has a body.
	 */
	public boolean hasBody() {
		return (this.body != null);
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || other.getClass() != getClass()) {
			return false;
		}
		HttpEntity<?> otherEntity = (HttpEntity<?>) other;
		return (ObjectUtils.nullSafeEquals(this.headers, otherEntity.headers) &&
				ObjectUtils.nullSafeEquals(this.body, otherEntity.body));
	}

	@Override
	public int hashCode() {
		return (ObjectUtils.nullSafeHashCode(this.headers) * 29 + ObjectUtils.nullSafeHashCode(this.body));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("<");
		if (this.body != null) {
			builder.append(this.body);
			if (this.headers != null) {
				builder.append(',');
			}
		}
		if (this.headers != null) {
			builder.append(this.headers);
		}
		builder.append('>');
		return builder.toString();
	}

	@Override
	public MediaType getContentType() {
		return headers.getContentType();
	}
}
