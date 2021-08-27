package io.basc.framework.data;

import io.basc.framework.lang.NestedRuntimeException;

public class StorageException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public StorageException(String msg) {
		super(msg);
	}

	public StorageException(Throwable cause) {
		super(cause);
	}

	public StorageException(String message, Throwable cause) {
		super(message, cause);
	}
}
