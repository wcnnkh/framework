package run.soeasy.framework.core.convert;

public class ConversionException extends RuntimeException {
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
	public ConversionException(String message, Throwable cause) {
		super(message, cause);
	}

}
