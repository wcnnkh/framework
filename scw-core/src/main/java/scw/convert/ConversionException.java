package scw.convert;

import scw.lang.NestedRuntimeException;

public class ConversionException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Construct a new conversion exception.
	 * @param message the exception message
	 */
	public ConversionException(String message) {
		super(message);
	}

	/**
	 * Construct a new conversion exception.
	 * @param message the exception message
	 * @param cause the cause
	 */
	public ConversionException(String message, Throwable cause) {
		super(message, cause);
	}

}	
