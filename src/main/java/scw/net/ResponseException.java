package scw.net;

import scw.core.NestedRuntimeException;

public class ResponseException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public ResponseException(String msg) {
		super(msg);
	}

	public ResponseException(Throwable cause) {
		super(cause);
	}

	public ResponseException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
