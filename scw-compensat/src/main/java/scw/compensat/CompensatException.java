package scw.compensat;

import scw.lang.NestedRuntimeException;

public class CompensatException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public CompensatException(String msg) {
		super(msg);
	}

	public CompensatException(Throwable cause) {
		super(cause);
	}

	public CompensatException(String message, Throwable cause) {
		super(message, cause);
	}
}
