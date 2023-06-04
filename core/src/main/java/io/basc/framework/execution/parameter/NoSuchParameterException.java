package io.basc.framework.execution.parameter;

public class NoSuchParameterException extends ParameterException {
	private static final long serialVersionUID = 1L;

	public NoSuchParameterException(String message) {
		super(message);
	}

	public NoSuchParameterException(Throwable e) {
		super(e);
	}

	public NoSuchParameterException(String message, Throwable e) {
		super(message, e);
	}
}
