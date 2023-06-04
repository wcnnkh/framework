package io.basc.framework.beans;

import io.basc.framework.lang.Nullable;

public class FatalBeanException extends BeansException {
	private static final long serialVersionUID = 1L;

	/**
	 * Create a new FatalBeanException with the specified message.
	 * 
	 * @param msg the detail message
	 */
	public FatalBeanException(String msg) {
		super(msg);
	}

	/**
	 * Create a new FatalBeanException with the specified message and root cause.
	 * 
	 * @param msg   the detail message
	 * @param cause the root cause
	 */
	public FatalBeanException(String msg, @Nullable Throwable cause) {
		super(msg, cause);
	}

}
