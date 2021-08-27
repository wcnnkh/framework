package io.basc.framework.web;

import io.basc.framework.lang.NestedRuntimeException;

public class WebException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public WebException(String msg) {
		super(msg);
	}

	public WebException(Throwable cause) {
		super(cause);
	}

	public WebException(String message, Throwable cause) {
		super(message, cause);
	}
}
