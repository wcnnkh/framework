package scw.data;

import scw.lang.NestedRuntimeException;

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
