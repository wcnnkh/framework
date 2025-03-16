package io.basc.framework.http;

import java.net.URI;

import io.basc.framework.net.Request;

public interface HttpRequest extends HttpMessage, Request {
	public static interface HttpRequestWrapper<W extends HttpRequest>
			extends HttpRequest, HttpMessageWrapper<W>, RequestWrapper<W> {
		@Override
		default HttpHeaders getHeaders() {
			return getSource().getHeaders();
		}

		@Override
		default HttpMethod getMethod() {
			return getSource().getMethod();
		}

		@Override
		default String getRawMethod() {
			return getSource().getRawMethod();
		}

		@Override
		default URI getURI() {
			return getSource().getURI();
		}
	}

	HttpHeaders getHeaders();

	default HttpMethod getMethod() {
		return HttpMethod.resolve(getRawMethod());
	}

	String getRawMethod();

	URI getURI();
}
