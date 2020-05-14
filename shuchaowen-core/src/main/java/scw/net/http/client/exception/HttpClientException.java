package scw.net.http.client.exception;

import scw.lang.NestedRuntimeException;

public class HttpClientException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public HttpClientException(String msg) {
		super(msg);
	}

	public HttpClientException(String msg, Throwable ex) {
		super(msg, ex);
	}
}
