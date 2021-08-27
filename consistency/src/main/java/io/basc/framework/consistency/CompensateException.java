package io.basc.framework.consistency;

import io.basc.framework.lang.NestedRuntimeException;

public class CompensateException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public CompensateException(String msg) {
		super(msg);
	}

	public CompensateException(Throwable cause) {
		super(cause);
	}

	public CompensateException(String message, Throwable cause) {
		super(message, cause);
	}
}
