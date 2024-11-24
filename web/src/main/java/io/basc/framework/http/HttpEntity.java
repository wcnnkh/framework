package io.basc.framework.http;

import java.io.Serializable;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.net.Entity;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.collect.MultiValueMap;

public class HttpEntity<T> implements Entity<T>, HttpMessage, Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * The empty {@code HttpEntity}, with no body or headers.
	 */
	public static final HttpEntity<?> EMPTY = new HttpEntity<Object>();

	private final HttpHeaders headers;

	@Nullable
	private final T body;

	@Nullable
	private final TypeDescriptor typeDescriptor;

	/**
	 * Create a new, empty {@code HttpEntity}.
	 */
	protected HttpEntity() {
		this(null, null);
	}

	/**
	 * Create a new {@code HttpEntity} with the given body and no headers.
	 * 
	 * @param body               the entity body
	 * @param bodyTypeDescriptor the entity body type
	 */
	public HttpEntity(T body, TypeDescriptor bodyTypeDescriptor) {
		this(body, bodyTypeDescriptor, null);
	}

	/**
	 * Create a new {@code HttpEntity} with the given headers and no body.
	 * 
	 * @param headers the entity headers
	 */
	public HttpEntity(MultiValueMap<String, String> headers) {
		this(null, null, headers);
	}

	/**
	 * Create a new {@code HttpEntity} with the given body and headers.
	 * 
	 * @param body               the entity body
	 * @param bodyTypeDescriptor the entity body type
	 * @param headers            the entity headers
	 */
	public HttpEntity(T body, TypeDescriptor bodyTypeDescriptor, MultiValueMap<String, String> headers) {
		this.body = body;
		this.typeDescriptor = bodyTypeDescriptor;
		HttpHeaders tempHeaders = new HttpHeaders();
		if (headers != null) {
			tempHeaders.putAll(headers);
		}
		this.headers = tempHeaders;
	}

	public HttpHeaders getHeaders() {
		return this.headers;
	}

	public T getBody() {
		return this.body;
	}

	public boolean hasBody() {
		return (this.body != null);
	}

	/**
	 * Return the type of the request's body.
	 * 
	 * @return the request's body type, or {@code null} if not known
	 */
	@Nullable
	public TypeDescriptor getTypeDescriptor() {
		if (this.typeDescriptor == null) {
			T body = getBody();
			if (body != null) {
				return TypeDescriptor.forObject(body);
			}
		}
		return this.typeDescriptor;
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
		return (ObjectUtils.equals(this.headers, otherEntity.headers)
				&& ObjectUtils.equals(this.body, otherEntity.body));
	}

	@Override
	public int hashCode() {
		return (ObjectUtils.hashCode(this.headers) * 29 + ObjectUtils.hashCode(this.body));
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
