package io.basc.framework.context;

public class ContextException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ContextException() {
		super();
	}

	public ContextException(String message) {
		super(message);
	}

	public ContextException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContextException(Throwable cause) {
		super(cause);
	}
}
