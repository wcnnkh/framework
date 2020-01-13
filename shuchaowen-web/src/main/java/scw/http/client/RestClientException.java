package scw.http.client;

import scw.lang.NestedRuntimeException;

public class RestClientException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Construct a new instance of {@code HttpClientException} with the given message.
	 * @param msg the message
	 */
	public RestClientException(String msg) {
		super(msg);
	}

	/**
	 * Construct a new instance of {@code HttpClientException} with the given message and
	 * exception.
	 * @param msg the message
	 * @param ex the exception
	 */
	public RestClientException(String msg, Throwable ex) {
		super(msg, ex);
	}
}
