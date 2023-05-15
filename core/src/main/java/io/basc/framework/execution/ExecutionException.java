package io.basc.framework.execution;

public class ExecutionException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ExecutionException() {
		super();
	}

	public ExecutionException(String message) {
		super(message);
	}

	public ExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExecutionException(Throwable cause) {
		super(cause);
	}
}
