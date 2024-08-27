package io.basc.framework.util;

public class NoUniqueElementException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NoUniqueElementException() {
		super();
	}

	public NoUniqueElementException(String message) {
		super(message);
	}
}
