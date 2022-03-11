package io.basc.framework.orm;

import io.basc.framework.lang.NestedRuntimeException;

public class OrmException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public OrmException(String msg) {
		super(msg);
	}

	public OrmException(Throwable cause) {
		super(cause);
	}

	public OrmException(String message, Throwable cause) {
		super(message, cause);
	}
}
