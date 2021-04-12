package scw.consistency;

import scw.lang.NestedRuntimeException;

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
