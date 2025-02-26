package io.basc.framework.http;

import java.io.Serializable;

import io.basc.framework.core.convert.Data;
import io.basc.framework.net.Entity;
import io.basc.framework.util.collections.MultiValueMap;
import lombok.NonNull;

@lombok.Data
public class HttpEntity<T> implements Entity<T>, HttpMessage, Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * The empty {@code HttpEntity}, with no body or headers.
	 */
	public static final HttpEntity<?> EMPTY = new HttpEntity<Object>();

	@NonNull
	private final HttpHeaders headers;

	private final Data<T> body;

	/**
	 * Create a new, empty {@code HttpEntity}.
	 */
	protected HttpEntity() {
		this(null, null);
	}

	/**
	 * Create a new {@code HttpEntity} with the given headers and no body.
	 * 
	 * @param headers the entity headers
	 */
	public HttpEntity(MultiValueMap<String, String> headers) {
		this(null, headers);
	}

	/**
	 * Create a new {@code HttpEntity} with the given body and headers.
	 * 
	 * @param body    the entity body
	 * @param headers the entity headers
	 */
	public HttpEntity(Data<T> body, MultiValueMap<String, String> headers) {
		this.body = body;
		HttpHeaders tempHeaders = new HttpHeaders();
		if (headers != null) {
			tempHeaders.putAll(headers);
		}
		this.headers = tempHeaders;
	}

	public HttpHeaders getHeaders() {
		return this.headers;
	}

	public boolean hasBody() {
		return (this.body != null);
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
}
