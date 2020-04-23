package scw.async;

import scw.lang.NestedRuntimeException;

public class AsyncException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public AsyncException(String msg) {
		super(msg);
	}

	public AsyncException(Throwable cause) {
		super(cause);
	}

	public AsyncException(String message, Throwable cause) {
		super(message, cause);
	}
}
