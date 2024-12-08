package io.basc.framework.util;

public class ShouldNeverGetHereException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ShouldNeverGetHereException() {
		super();
	}

	public ShouldNeverGetHereException(String message) {
		super(message);
	}

	public ShouldNeverGetHereException(String message, Throwable cause) {
		super(message, cause);
	}

	public ShouldNeverGetHereException(Throwable cause) {
		super(cause);
	}
}
