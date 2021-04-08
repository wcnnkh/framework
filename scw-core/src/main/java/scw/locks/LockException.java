package scw.locks;

import scw.lang.NestedRuntimeException;

public class LockException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public LockException(String msg) {
		super(msg);
	}

	public LockException(Throwable cause) {
		super(cause);
	}

	public LockException(String message, Throwable cause) {
		super(message, cause);
	}
}
