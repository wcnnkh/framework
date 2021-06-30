package scw.util.stream;

import scw.lang.NestedRuntimeException;

public class StreamException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public StreamException(String msg) {
		super(msg);
	}

	public StreamException(Throwable cause) {
		super(cause);
	}

	public StreamException(String message, Throwable cause) {
		super(message, cause);
	}
}
