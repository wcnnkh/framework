package scw.retry;

import scw.lang.NestedRuntimeException;

public class RetryException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public RetryException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public RetryException(String msg) {
		super(msg);
	}

}
