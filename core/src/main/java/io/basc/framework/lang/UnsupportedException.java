package io.basc.framework.lang;

public class UnsupportedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UnsupportedException(String message) {
		super(message);
	}

	public UnsupportedException(Throwable e) {
		super(e);
	}

	public UnsupportedException(String message, Throwable e) {
		super(message, e);
	}
}
