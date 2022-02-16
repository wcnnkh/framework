package io.basc.framework.http.client;

public class HttpClientException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public HttpClientException(String msg) {
		super(msg);
	}

	public HttpClientException(String msg, Throwable ex) {
		super(msg, ex);
	}
}
