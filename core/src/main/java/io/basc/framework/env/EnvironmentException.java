package io.basc.framework.env;

import io.basc.framework.lang.NestedRuntimeException;

public class EnvironmentException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public EnvironmentException(String msg) {
		super(msg);
	}

	public EnvironmentException(Throwable cause) {
		super(cause);
	}

	public EnvironmentException(String message, Throwable cause) {
		super(message, cause);
	}
}
