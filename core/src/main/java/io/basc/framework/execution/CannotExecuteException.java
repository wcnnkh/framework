package io.basc.framework.execution;

public class CannotExecuteException extends ExecutionException {
	private static final long serialVersionUID = 1L;

	public CannotExecuteException() {
		super();
	}

	public CannotExecuteException(String message) {
		super(message);
	}

	public CannotExecuteException(String message, Throwable cause) {
		super(message, cause);
	}

	public CannotExecuteException(Throwable cause) {
		super(cause);
	}
}
