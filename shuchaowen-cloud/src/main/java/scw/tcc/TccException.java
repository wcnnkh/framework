package scw.tcc;

import scw.lang.NestedRuntimeException;

public class TccException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public TccException(String msg) {
		super(msg);
	}

	public TccException(String message, Throwable cause) {
		super(message, cause);
	}
}
