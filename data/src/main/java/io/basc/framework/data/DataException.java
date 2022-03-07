package io.basc.framework.data;

import io.basc.framework.lang.NestedRuntimeException;

public class DataException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public DataException(String msg) {
		super(msg);
	}

	public DataException(Throwable cause) {
		super(cause);
	}

	public DataException(String message, Throwable cause) {
		super(message, cause);
	}
}
