package scw.upload;

import scw.lang.NestedRuntimeException;

public class UploaderException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public UploaderException(String msg) {
		super(msg);
	}

	public UploaderException(Throwable cause) {
		super(cause);
	}

	public UploaderException(String message, Throwable cause) {
		super(message, cause);
	}
}
