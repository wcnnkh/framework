package scw.configure;

import scw.lang.NestedRuntimeException;

public class ConfigureException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Construct a new configure exception.
	 * 
	 * @param message
	 *            the exception message
	 */
	public ConfigureException(String message) {
		super(message);
	}

	/**
	 * Construct a new configure exception.
	 * 
	 * @param message
	 *            the exception message
	 * @param cause
	 *            the cause
	 */
	public ConfigureException(String message, Throwable cause) {
		super(message, cause);
	}

}
