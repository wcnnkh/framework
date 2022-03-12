package io.basc.framework.csv;

import io.basc.framework.lang.NestedRuntimeException;

public class CsvException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public CsvException(String msg) {
		super(msg);
	}

	public CsvException(Throwable cause) {
		super(cause);
	}

	public CsvException(String message, Throwable cause) {
		super(message, cause);
	}
}
