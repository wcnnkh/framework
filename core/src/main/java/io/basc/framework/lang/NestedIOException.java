package io.basc.framework.lang;

import java.io.IOException;

/**
 * Subclass of {@link IOException} that properly handles a root cause,
 * exposing the root cause just like NestedChecked/RuntimeException does.
 *
 * <p>Proper root cause handling has not been added to standard IOException before
 * Java 6, which is why we need to do it ourselves for Java 5 compatibility purposes.
 *
 * <p>The similarity between this class and the NestedChecked/RuntimeException
 * class is unavoidable, as this class needs to derive from IOException.
 *
 * @see #printStackTrace
 * @see io.basc.framework.lang.NestedRuntimeException
 */
@SuppressWarnings("serial")
public class NestedIOException extends IOException {

	static {
		// Eagerly load the NestedExceptionUtils class to avoid classloader deadlock
		// issues on OSGi when calling getMessage(). Reported by Don Brown; SPR-5607.
		NestedExceptionUtils.class.getName();
	}


	/**
	 * Construct a {@code NestedIOException} with the specified detail message.
	 * @param msg the detail message
	 */
	public NestedIOException(String msg) {
		super(msg);
	}

	/**
	 * Construct a {@code NestedIOException} with the specified detail message
	 * and nested exception.
	 * @param msg the detail message
	 * @param cause the nested exception
	 */
	public NestedIOException(@Nullable String msg, @Nullable Throwable cause) {
		super(msg, cause);
	}


	/**
	 * Return the detail message, including the message from the nested exception
	 * if there is one.
	 */
	@Override
	@Nullable
	public String getMessage() {
		return NestedExceptionUtils.buildMessage(super.getMessage(), getCause());
	}

}
