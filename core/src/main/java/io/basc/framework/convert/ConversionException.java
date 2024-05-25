package io.basc.framework.convert;

import io.basc.framework.lang.NestedRuntimeException;
import io.basc.framework.lang.Nullable;

public class ConversionException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Construct a new conversion exception.
	 * 
	 * @param message the exception message
	 */
	public ConversionException(String message) {
		super(message);
	}

	public ConversionException(Throwable cause) {
		super(cause);
	}

	/**
	 * Construct a new conversion exception.
	 * 
	 * @param message the exception message
	 * @param cause   the cause
	 */
	public ConversionException(String message, @Nullable Throwable cause) {
		super(message, cause);
	}

}
