package io.basc.framework.upload;

import io.basc.framework.lang.NestedRuntimeException;

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
